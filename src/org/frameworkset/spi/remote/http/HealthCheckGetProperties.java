package org.frameworkset.spi.remote.http;
/**
 * Copyright 2020 bboss
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

import org.frameworkset.spi.assemble.GetProperties;

import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2020/5/14 11:07
 * @author biaoping.yin
 * @version 1.0
 */
public class HealthCheckGetProperties implements GetProperties {
	private GetProperties context;
	public HealthCheckGetProperties(GetProperties context){
		this.context = context;
	}

	@Override
	public void reset() {
		this.context.reset();
	}

	@Override
	public String getExternalProperty(String property) {
		return context.getExternalProperty(property);
	}
    /**
     * 根据属性名称前缀获取属性集
     * @param namespace
     * @param propertyPrex 属性名称前缀
     * @param truncated 返回的key是否截取掉前缀
     * @return
     */
    @Override
    public Map<String,Object> getExternalProperties(String namespace,String propertyPrex,boolean truncated){
        return context.getExternalProperties(namespace,propertyPrex,truncated);
    }
	@Override
	public String getSystemEnvProperty(String property) {
		return context.getSystemEnvProperty(property);
	}

	@Override
	public String getExternalProperty(String property, String defaultValue) {
		return context.getExternalProperty(property,defaultValue);
	}

	@Override
	public Object getExternalObjectProperty(String property) {
		/**
		if(property.endsWith("http.automaticRetriesDisabled") || property.endsWith("http.evictExpiredConnections"))
			return true;
		else if(property.endsWith("http.staleConnectionCheckEnabled")){
			return false;
		}
		else if(property.endsWith("http.retryTime")
				|| property.endsWith("http.retryInterval")
				|| property.endsWith("http.validateAfterInactivity")){
			return -1;
		}*/
        Object config = context.getExternalObjectProperty(property);
        if(config == null)
            return config;
		if(property.endsWith("http.defaultMaxPerRoute")) {
            Integer tmp = Integer.parseInt(String.valueOf(config));
            
            return tmp > 5?5:tmp;
        }
		else if(property.endsWith("http.maxTotal")){
            Integer maxTotal = Integer.parseInt(String.valueOf(config));
            int ret = 0;
            String key = null;
            String defaultKey = null;
            if(property.equals("http.maxTotal")){
                key = "http.defaultMaxPerRoute";
                defaultKey = "default.http.defaultMaxPerRoute";
            }
            else{
                key = property.substring(0,property.indexOf("http.maxTotal"))+ "http.defaultMaxPerRoute";
            }
            Object defaultMaxPerRoute_ = context.getExternalObjectProperty(key);
            if(defaultMaxPerRoute_ == null){
                defaultMaxPerRoute_ = context.getExternalObjectProperty(defaultKey);
            }
            if(defaultMaxPerRoute_ != null) {
                Integer defaultMaxPerRoute = Integer.parseInt(String.valueOf(defaultMaxPerRoute_));
                int tmp = defaultMaxPerRoute > 5 ? 5 : defaultMaxPerRoute;
                ret = maxTotal / defaultMaxPerRoute * tmp;
            }
            else{
                ret = maxTotal > 100? 100: maxTotal;
            }
			return ret;
		}
		return config;
	}
    public static void main(String[] args){
        String property = "log.http.maxTotal";
        String key = property.substring(0,property.indexOf("http.maxTotal"))+ "http.defaultMaxPerRoute";
        System.out.println(key);
        int per = 5;
        int max = 55 / 10 * per;
        System.out.println(max);
    }

	@Override
	public Object getExternalObjectProperty(String property, Object defaultValue) {
		return context.getExternalObjectProperty(property,defaultValue);
	}

	@Override
	public boolean getExternalBooleanProperty(String property, boolean defaultValue) {
		return context.getExternalBooleanProperty(property, defaultValue);
	}

	@Override
	public String getExternalPropertyWithNS(String namespace, String property) {
//		if(property.endsWith("http.customHttpRequestRetryHandler"))
//			return null;
		return context.getExternalPropertyWithNS(namespace,property);
	}

	@Override
	public String getExternalPropertyWithNS(String namespace, String property, String defaultValue) {
//		if(property.endsWith("http.customHttpRequestRetryHandler"))
//			return null;
		return context.getExternalPropertyWithNS(namespace,property,defaultValue);
	}

	@Override
	public Object getExternalObjectPropertyWithNS(String namespace, String property) {
		return context.getExternalObjectPropertyWithNS(namespace,property);
	}

	@Override
	public Object getExternalObjectPropertyWithNS(String namespace, String property, Object defaultValue) {
		return context.getExternalObjectPropertyWithNS(namespace,property,defaultValue);
	}

	@Override
	public Map getAllExternalProperties() {
		return context.getAllExternalProperties();
	}
}
