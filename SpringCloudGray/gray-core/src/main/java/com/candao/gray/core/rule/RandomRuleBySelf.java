package com.candao.gray.core.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.util.StringUtils;

import com.candao.gray.core.CoreHeaderInterceptor;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

public class RandomRuleBySelf extends RandomRule {

	private static RandomRuleBySelf instance;
	
	private static Random rand;
	
	private static Boolean is_close = true;

	private RandomRuleBySelf() {
	}

	public static RandomRuleBySelf getInstance() {
		if (instance == null) {
			rand = new Random();
			instance = new RandomRuleBySelf();
		}
		return instance;
	}

	@Override
	public Server choose(ILoadBalancer lb, Object key) {
		if (lb == null) {
            return null;
        }
        Server server = null;

        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> upList = excludeMetaDataNoEmpty(lb.getReachableServers());
            List<Server> allList = excludeMetaDataNoEmpty(lb.getAllServers());
            
            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }

            int index = rand.nextInt(serverCount);
            server = upList.get(index);

            if (server == null) {
                /*
                 * The only time this should happen is if the server list were
                 * somehow trimmed. This is a transient condition. Retry after
                 * yielding.
                 */
                Thread.yield();
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }

            // Shouldn't actually happen.. but must be transient or a bug.
            server = null;
            Thread.yield();
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
		RandomRuleBySelf.is_close = is_close;
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
