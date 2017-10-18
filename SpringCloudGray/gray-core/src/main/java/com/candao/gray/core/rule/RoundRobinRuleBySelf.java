package com.candao.gray.core.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.StringUtils;

import com.candao.gray.core.CoreHeaderInterceptor;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

public class RoundRobinRuleBySelf extends RoundRobinRule {

	private static AtomicInteger nextServerCyclicCounter;

	private static RoundRobinRuleBySelf instance;

	private static Boolean is_close = true;

	private RoundRobinRuleBySelf() {
	}

	public static RoundRobinRuleBySelf getInstance() {
		if (instance == null) {
			nextServerCyclicCounter = new AtomicInteger(0);
			instance = new RoundRobinRuleBySelf();
		}
		return instance;
	}

	@Override
	public Server choose(ILoadBalancer lb, Object key) {
		if (lb == null) {
			System.out.println("no load balancer");
			return null;
		}

		Server server = null;
		int count = 0;
		while (server == null && count++ < 10) {

			List<Server> reachableServers = excludeMetaDataNoEmpty(lb.getReachableServers());

			List<Server> allServers = excludeMetaDataNoEmpty(lb.getAllServers());

			int upCount = reachableServers.size();
			int serverCount = allServers.size();

			if ((upCount == 0) || (serverCount == 0)) {
				System.out.println("No up servers available from load balancer: " + lb);
				return null;
			}

			int nextServerIndex = incrementAndGetModulo(serverCount);
			server = allServers.get(nextServerIndex);

			if (server == null) {
				/* Transient. */
				Thread.yield();
				continue;
			}

			if (server.isAlive() && (server.isReadyToServe())) {
				return (server);
			}

			// Next.
			server = null;
		}

		if (count >= 10) {
			System.out.println("No available alive servers after 10 tries from load balancer: " + lb);
		}
		return server;
	}

	/**
	 * @param lb
	 * @param key
	 * @param is_close
	 *            灰度开关是否关闭
	 * @return
	 */
	public Server choose(ILoadBalancer lb, Object key, Boolean is_close) {
		RoundRobinRuleBySelf.is_close = is_close;
		return choose(lb, key);
	}

	private int incrementAndGetModulo(int modulo) {
		for (;;) {
			int current = nextServerCyclicCounter.get();
			int next = (current + 1) % modulo;
			if (nextServerCyclicCounter.compareAndSet(current, next))
				return next;
		}
	}

	private List<Server> excludeMetaDataNoEmpty(List<Server> upList) {
		List<Server> reachableServers = new ArrayList<Server>();
		// 存在tag【被选定了灰度对象】,寻找不到metaData,默认使用空metaData的服务填充
		List<Server> exit_tag_no_metaData = new ArrayList<Server>();

		// 存在tag【被选定了灰度对象】,使用metaData匹配到的服务填充
		List<Server> exit_tag_has_metaData = new ArrayList<Server>();

		for (Server server : upList) {
			Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();

			// 灰度开关关闭情况
			if (is_close) {
				if (metadata.isEmpty()) {
					reachableServers.add(server);
				}
				continue;
			}
			String tag = CoreHeaderInterceptor.tag.get();

			// 灰度开关启动,正常用户对象,走metaData为空的正常服务
			if (StringUtils.isEmpty(tag)) {
				if (metadata.isEmpty()) {
					reachableServers.add(server);
				}
				continue;
			}

			// 断言,tag标签是否存在于metaData中
			if (metadata.containsKey(tag)) {
				// 根据灰度标签取下灰度值域
				String metaDataValue = metadata.get(tag);
				if (!StringUtils.isEmpty(metaDataValue)) {
					String[] metaValues = metaDataValue.split(CoreHeaderInterceptor.HEADER_LABEL_SPLIT);
					List<String> metadataLabel = Arrays.asList(metaValues);
					for (String label : metadataLabel) {
						if (CoreHeaderInterceptor.label.get().contains(label)) {
							exit_tag_has_metaData.add(server);
						}
					}
				}
			} else {
				if (metadata.isEmpty()) {
					exit_tag_no_metaData.add(server);
				}
			}
		}
		
		if (!reachableServers.isEmpty()) {
			return reachableServers;
		}
		
		if (exit_tag_has_metaData.isEmpty()) {
			reachableServers.addAll(exit_tag_no_metaData);
		} else {
			reachableServers.addAll(exit_tag_has_metaData);
		}
		return reachableServers;
	}

}
