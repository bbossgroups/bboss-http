package org.frameworkset.spi.remote.http.proxy;
/**
 * Copyright 2008 biaoping.yin
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>Description:
 * 1.从外部服务注册中心监听服务地址变更后调用本组件handleDiscoverHosts方法刷新负载组件地址清单
 * 2.切换路由组，例如主备切换，灰度生产切换
 * </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/6/20 12:47
 * @author biaoping.yin
 * @version 1.0
 */
public class HttpProxyUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpProxyUtil.class);
	public static void changeRouting(String poolName,String newCurrentRounte){
		if(newCurrentRounte == null)
			return;
		if(poolName == null)
			poolName = "default";
		try {
			ClientConfiguration clientConfiguration = ClientConfiguration.getClientConfiguration(poolName);
			if (clientConfiguration != null) {
				HttpHostDiscover httpHostDiscover = null;
				HttpServiceHosts httpServiceHosts = clientConfiguration.getHttpServiceHosts();
				httpHostDiscover = httpServiceHosts.getHostDiscover();
				if (httpHostDiscover == null) {
					if (logger.isInfoEnabled()) {//Registry default HttpHostDiscover
						logger.info("Registry default HttpHostDiscover to httppool[{}]", poolName);
					}
					synchronized (HttpProxyUtil.class) {
						httpHostDiscover = httpServiceHosts.getHostDiscover();
						if (httpHostDiscover == null) {
							httpHostDiscover = new DefaultHttpHostDiscover();
							httpHostDiscover.setHttpServiceHosts(httpServiceHosts);
							httpServiceHosts.setHostDiscover(httpHostDiscover);
						}
					}
				}
				if (httpHostDiscover != null) {

					httpHostDiscover.changeRouting(newCurrentRounte);
				}
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled())
				logger.info(new StringBuilder().append("Change Routing ")
						.append(poolName).append(", new routing[").append(newCurrentRounte).append("]  failed:").toString(),e);
		}
	}
	/**
	 *
	 * @param poolName 服务组名称
	 * @param hosts 新的主机节点信息
	 */
	public static void handleDiscoverHosts(String poolName, List<HttpHost> hosts){
		handleDiscoverHosts(  poolName,  hosts,(String)null);
	}
	/**
	 *
	 * @param poolName 服务组名称
	 * @param hosts 新的主机节点信息
	 * @param newCurrentRounte 新的路由组
	 */
	public static void handleDiscoverHosts(String poolName, List<HttpHost> hosts,String newCurrentRounte){
		if(poolName == null)
			poolName = "default";
		try {
			ClientConfiguration clientConfiguration = ClientConfiguration.getClientConfiguration(poolName);
			if (clientConfiguration != null) {
				HttpHostDiscover httpHostDiscover = null;
				HttpServiceHosts httpServiceHosts = clientConfiguration.getHttpServiceHosts();
				if (httpServiceHosts != null) {
					httpHostDiscover = httpServiceHosts.getHostDiscover();
					if (httpHostDiscover == null) {
						if (logger.isInfoEnabled()) {//Registry default HttpHostDiscover
							logger.info("Registry default HttpHostDiscover to httppool[{}]", poolName);
						}
						synchronized (HttpProxyUtil.class) {
							httpHostDiscover = httpServiceHosts.getHostDiscover();
							if (httpHostDiscover == null) {
								httpHostDiscover = new DefaultHttpHostDiscover();
								httpHostDiscover.setHttpServiceHosts(httpServiceHosts);
								httpServiceHosts.setHostDiscover(httpHostDiscover);
							}
						}
					}
					if (httpHostDiscover != null) {
						if (hosts == null || hosts.size() == 0) {
							Boolean handleNullOrEmptyHostsByDiscovery = httpHostDiscover.handleNullOrEmptyHostsByDiscovery();
							if (handleNullOrEmptyHostsByDiscovery == null) {
								handleNullOrEmptyHostsByDiscovery = httpServiceHosts.getHandleNullOrEmptyHostsByDiscovery();
							}
							if (handleNullOrEmptyHostsByDiscovery == null || !handleNullOrEmptyHostsByDiscovery) {
								if (logger.isInfoEnabled())
									logger.info(new StringBuilder().append("Discovery ")
											.append(httpServiceHosts.getClientConfiguration().getBeanName()).append(" servers : ignore with httpHosts == null || httpHosts.size() == 0").toString());
								return;
							}
						}
						httpHostDiscover.handleDiscoverHosts(hosts,newCurrentRounte);
					}
				}
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				StringBuilder message = new StringBuilder();
				if(newCurrentRounte != null) {
					message.append("Change Routing ")
							.append(poolName).append(", new routing[").append(newCurrentRounte).append("] and ");
				}
				message.append("Discovery ")
						.append(poolName).append(" servers failed:");
				logger.info(message.toString(), e);
			}
		}


	}


}
