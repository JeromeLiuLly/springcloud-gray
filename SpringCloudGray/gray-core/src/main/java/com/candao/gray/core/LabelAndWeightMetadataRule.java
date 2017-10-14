package com.candao.gray.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.candao.gray.core.net.HttpClient;
import com.candao.gray.core.net.HttpResult;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

public class LabelAndWeightMetadataRule extends ZoneAvoidanceRule {
	public static final String META_DATA_KEY_LABEL_AND = "labelAnd";
	public static final String META_DATA_KEY_LABEL_OR = "labelOr";

	public static final String META_DATA_KEY_WEIGHT = "weight";

	private Random random = new Random();

	@Override
	public Server choose(Object key) {

		// 计算总值并剔除0权重节点
		int totalWeight = 0;
		Map<Server, Integer> serverWeightMap = new HashMap<Server, Integer>();

		String url = "http://10.200.102.136:6015/eureka/apps/switch";
		HttpResult result = new HttpResult();
		result.statusCode = 500;
		try {
			result = HttpClient.get(url, null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// 灰度开关被设置成关闭状态,默认走空metadata或者是特定标识是正常的服务,随机访问
		if (result.statusCode == 200 && !Boolean.parseBoolean(JSONObject.parseObject(result.content).getString("errorMsg"))) {
			List<Server> serverList = this.getPredicate().getEligibleServers(this.getLoadBalancer().getAllServers());
			for (Server server : serverList) {
				Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
				if (metadata == null || metadata.isEmpty()) {
					System.out.println("目标服务：" + server.getHostPort() + "服务名称：" + ((DiscoveryEnabledServer) server).getInstanceInfo().getAppName());
					return server;
				}
			}
			System.out.println("没有找到正常的服务");
			return null;
		}

		// 断言是否,存在 flag 位
		if (StringUtils.isEmpty(CoreHeaderInterceptor.label.get())) {
			List<Server> serverList = this.getPredicate().getEligibleServers(this.getLoadBalancer().getAllServers());

			if (CollectionUtils.isEmpty(serverList)) {
				return null;
			}

			for (Server server : serverList) {
				Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
				if (metadata == null || metadata.isEmpty()) {
					System.out.println("目标服务：" + server.getHostPort() + "服务名称："
							+ ((DiscoveryEnabledServer) server).getInstanceInfo().getAppName());
					return server;
				}
			}
			return null;
		}

		List<Server> serverList = this.getPredicate().getEligibleServers(this.getLoadBalancer().getAllServers(), key);
		if (CollectionUtils.isEmpty(serverList)) {
			return null;
		}

		// 获取访问服务列表
		for (Server server : serverList) {
			Map<String, String> metadata = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();

			// 优先匹配label
			String labelOr = metadata.get(META_DATA_KEY_LABEL_OR);
			if (!StringUtils.isEmpty(labelOr)) {
				List<String> metadataLabel = Arrays.asList(labelOr.split(CoreHeaderInterceptor.HEADER_LABEL_SPLIT));
				for (String label : metadataLabel) {
					if (CoreHeaderInterceptor.label.get().contains(label)) {
						System.out.println("目标服务：" + server.getHostPort() + "服务名称："
								+ ((DiscoveryEnabledServer) server).getInstanceInfo().getAppName());
						return server;
					}
				}
			}

			String labelAnd = metadata.get(META_DATA_KEY_LABEL_AND);
			if (!StringUtils.isEmpty(labelAnd)) {
				List<String> metadataLabel = Arrays.asList(labelAnd.split(CoreHeaderInterceptor.HEADER_LABEL_SPLIT));
				if (CoreHeaderInterceptor.label.get().containsAll(metadataLabel)) {
					System.out.println("目标服务：" + server.getHostPort() + "服务名称："
							+ ((DiscoveryEnabledServer) server).getInstanceInfo().getAppName());
					return server;
				}
			}

			String strWeight = metadata.get(META_DATA_KEY_WEIGHT);

			int weight = 100;
			try {
				weight = Integer.parseInt(strWeight);
			} catch (Exception e) {
				// 无需处理
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
}
