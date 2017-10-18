package com.candao.gray.core.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.util.StringUtils;

import com.candao.gray.core.CoreHeaderInterceptor;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

public class WeightedResponseTimeRuleBySelf {

	private Random random = new Random();

	private static Boolean is_close = true;

	private static final String META_DATA_KEY_WEIGHT = "weight";

	private static WeightedResponseTimeRuleBySelf instance;

	private WeightedResponseTimeRuleBySelf() {
	}

	public static WeightedResponseTimeRuleBySelf getInstance() {
		if (instance == null) {
			instance = new WeightedResponseTimeRuleBySelf();
		}
		return instance;
	}

	public Server choose(ILoadBalancer lb, Object key) {
		if (lb == null) {
			System.out.println("no load balancer");
			return null;
		}
		// 计算总值并剔除0权重节点
		int totalWeight = 0;
		Map<Server, Integer> serverWeightMap = new HashMap<Server, Integer>();

		List<Server> allList = excludeMetaDataNoEmpty(lb.getAllServers());

		for (Server server : allList) {
			Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();

			String strWeight = metadata.get(META_DATA_KEY_WEIGHT);

			int weight = 100;
			try {
				weight = Integer.parseInt(strWeight);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (weight <= 0) {
				continue;
			}
			serverWeightMap.put(server, weight);
			totalWeight += weight;
		}

		// 权重随机
		int randomWight = this.random.nextInt(totalWeight);
		int current = 0;
		for (Map.Entry<Server, Integer> entry : serverWeightMap.entrySet()) {
			current += entry.getValue();
			if (randomWight <= current) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * @param lb
	 * @param key
	 * @param is_close
	 *            灰度开关是否关闭
	 * @return
	 */
	public Server choose(ILoadBalancer lb, Object key, Boolean is_close) {
		WeightedResponseTimeRuleBySelf.is_close = is_close;
		return choose(lb, key);
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
				} else if (metadata.size() == 1 && metadata.containsKey("weight")) {
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
