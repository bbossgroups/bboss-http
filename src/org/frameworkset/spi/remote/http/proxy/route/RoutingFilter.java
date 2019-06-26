package org.frameworkset.spi.remote.http.proxy.route;
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


import org.frameworkset.spi.remote.http.proxy.HttpAddress;
import org.frameworkset.spi.remote.http.proxy.NoHttpServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/6/25 16:02
 * @author biaoping.yin
 * @version 1.0
 */
public class RoutingFilter {
	private static Logger logger = LoggerFactory.getLogger(RoutingFilter.class);
	private Map<String,RoutingGroup> routingGroupMap = new HashMap<String, RoutingGroup>();
	private RoutingGroup currentRoutingGroup = new RoutingGroup();
	private String currentRouting;
	private String message;
	private List<HttpAddress> addressList;

	public RoutingFilter(List<HttpAddress> addressList,String currentRouting) {
		this.currentRouting = currentRouting;
		this.addressList = addressList;
		message = new StringBuilder().append("All Http Server ").append(addressList.toString()).append(" can't been connected.").toString();
		grouped( addressList,  currentRouting);
	}

	private void grouped(List<HttpAddress> addressList,String currentRouting){
		if(addressList == null || addressList.size() ==0)
			return;
		HttpAddress httpAddress = null;
		if(logger.isInfoEnabled()){
			logger.info("grouped http address by routing rule.");
		}
		List<HttpAddress> commonGroup = new ArrayList<HttpAddress>();
		for(int i = 0; i < addressList.size(); i ++){
			httpAddress = addressList.get(i);
			if(httpAddress.getRouting() == null || httpAddress.getRouting().equals("")){
				commonGroup.add(httpAddress);
			}
			else if(httpAddress.getRouting().equals(currentRouting)){
				this.currentRoutingGroup.addHttpAddress(httpAddress);
			}
			else {
				RoutingGroup routingGroup = routingGroupMap.get(httpAddress.getRouting());

				if(routingGroup == null){
					routingGroupMap.put(httpAddress.getRouting(),routingGroup = new RoutingGroup());
				}
				routingGroup.addHttpAddress(httpAddress);

			}

		}
		this.currentRoutingGroup.after(commonGroup);
		Iterator<Map.Entry<String, RoutingGroup>> iterator = routingGroupMap.entrySet().iterator();
		while (iterator.hasNext()){
			Map.Entry<String, RoutingGroup> entry = iterator.next();
			entry.getValue().after(commonGroup);
		}
	}


	public HttpAddress get(){
		HttpAddress httpAddress = currentRoutingGroup.get();
		if(httpAddress == null){
			Iterator<Map.Entry<String, RoutingGroup>> iterator = routingGroupMap.entrySet().iterator();
			while (iterator.hasNext()){
				Map.Entry<String, RoutingGroup> entry = iterator.next();
				httpAddress = entry.getValue().get();
				if(httpAddress != null)
					break;
			}
		}
		if(httpAddress == null){
			throw new NoHttpServerException(message);
		}
		return httpAddress;
	}
	public static boolean access(String[] accessRoutings, HttpAddress httpHost){
		if(accessRoutings == null || accessRoutings.length == 0)
			return  true;
		String accessRouting = httpHost.getRouting();
		if(accessRouting == null){
			return true;
		}
		for(int i = 0; i < accessRoutings.length; i ++){
			if(accessRouting.equals(accessRoutings[i])){
				return true;
			}
		}
		return false;
	}
	public RoutingGroup getRoutingGroup(String routing){
		return routingGroupMap.get(routing);
	}

	public int size() {
		return addressList.size();
	}
}
