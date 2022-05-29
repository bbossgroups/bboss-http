package org.frameworkset.spi.remote.http;
/**
 * Copyright 2022 bboss
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

import com.frameworkset.util.SimpleStringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.io.EmptyInputStream;
import org.apache.http.util.EntityUtils;
import org.frameworkset.spi.remote.http.proxy.HttpProxyRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2022/5/28
 * @author biaoping.yin
 * @version 1.0
 */
public class ResponseUtil {

	private static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);
	public static <K,T> Map<K,T> handleMapResponse(String url,HttpResponse response,Class<K> keyType,Class<T> beanType)
			throws ClientProtocolException, IOException {
		int status = response.getStatusLine().getStatusCode();

		if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			return entity != null ? converJson2Map(  entity,  keyType,  beanType) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",error:").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}


	public static <T> List<T> handleListResponse(String url,HttpResponse response, Class<T> resultType)
			throws ClientProtocolException, IOException {
		int status = response.getStatusLine().getStatusCode();

		if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			return entity != null ? converJson2List(  entity,  resultType) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",error:").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}
	public static <T> Set<T> handleSetResponse(String url,HttpResponse response, Class<T> resultType)
			throws ClientProtocolException, IOException {
		int status = response.getStatusLine().getStatusCode();

		if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			return entity != null ? converJson2Set(  entity,  resultType) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",error:").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}
	public static String handleStringResponse(String url,HttpResponse response)
			throws ClientProtocolException, IOException {
		int status = response.getStatusLine().getStatusCode();

		if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			return entity != null ? EntityUtils.toString(entity) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null )
				throw new HttpProxyRequestException(new StringBuilder().append("send request to ")
						.append(url).append(" failed:")
						.append(EntityUtils.toString(entity)).toString());
			else
				throw new HttpProxyRequestException(new StringBuilder().append("send request to ").append(url).append(",Unexpected response status: " ).append( status).toString());
		}
	}
	public static <T> T handleResponse(String url,HttpResponse response, Class<T> resultType)
			throws ClientProtocolException, IOException {
		if(resultType != null  ){
			if(resultType.isAssignableFrom(String.class)) {
				return (T) handleStringResponse(url, response);
			}
			else if(resultType.isAssignableFrom(Integer.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
					return (T)Integer.valueOf(Integer.parseInt(value));
				}
			}
			else if(resultType.isAssignableFrom(int.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return (T)new Integer(0);
				}
				else{
					return (T)Integer.valueOf(Integer.parseInt(value));
				}
			}
			else if(resultType.isAssignableFrom(Long.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
					return (T)Long.valueOf(Long.parseLong(value));
				}
			}
			else if(resultType.isAssignableFrom(long.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return (T)new Long(0l);
				}
				else{
					return (T)Long.valueOf(Long.parseLong(value));
				}
			}
			else if(resultType.isAssignableFrom(Short.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
					return (T)Short.valueOf(Short.parseShort(value));
				}
			}
			else if(resultType.isAssignableFrom(short.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return (T)new Short((short)0);
				}
				else{
					return (T)Short.valueOf(Short.parseShort(value));
				}
			}
			else if(resultType.isAssignableFrom(Float.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
					return (T)Float.valueOf(Float.parseFloat(value));
				}
			}
			else if(resultType.isAssignableFrom(float.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return (T)new Float(0f);
				}
				else{
					return (T)Float.valueOf(Float.parseFloat(value));
				}
			}
			else if(resultType.isAssignableFrom(Double.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
					return (T)Double.valueOf(Double.parseDouble(value));
				}
			}
			else if(resultType.isAssignableFrom(double.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return (T)new Double(0d);
				}
				else{
					return (T)Double.valueOf(Double.parseDouble(value));
				}
			}
			else if(resultType.isAssignableFrom(Boolean.class) ) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return null;
				}
				else{
					return (T)Boolean.valueOf(Boolean.parseBoolean(value));
				}
			}
			else if(resultType.isAssignableFrom(boolean.class)) {
				String value =  handleStringResponse(url, response);
				if(value == null){
					return (T)new Boolean(false);
				}
				else{
					return (T)Boolean.valueOf(Boolean.parseBoolean(value));
				}
			}
		}
		int status = response.getStatusLine().getStatusCode();

		if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			return entity != null ? converJson(  entity,  resultType) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",error:").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}

	public static <D,T> D handleResponse(String url,HttpResponse response,Class<D> containType, Class<T> resultType)
			throws ClientProtocolException, IOException {
		int status = response.getStatusLine().getStatusCode();

		if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			return entity != null ? converJson(  entity, containType, resultType) : null;
		} else {
			HttpEntity entity = response.getEntity();
			if (entity != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug(new StringBuilder().append("Request url:").append(url).append(",status:").append(status).toString());
				}
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",error:").append(EntityUtils.toString(entity)).toString());
			}
			else
				throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",Unexpected response status: ").append( status).toString());
		}
	}

	public static boolean entityEmpty(HttpEntity entity,InputStream inputStream) throws IOException {
//        long contentLength = entity.getContentLength();
//        if(contentLength <= 0){
//            return true;
//        }

		if(inputStream instanceof EmptyInputStream)
			return true;
		return false;

	}
	public static <T> T converJson(HttpEntity entity, Class<T> clazz) throws IOException {
		InputStream inputStream = null;

		T var4;
		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}

			var4 = SimpleStringUtil.json2Object(inputStream, clazz);
		} finally {
			inputStream.close();
		}

		return var4;
	}

	public static <D,T> D converJson(HttpEntity entity, Class<D> containType ,Class<T> clazz) throws IOException {
		InputStream inputStream = null;

		D var4;
		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}
			var4 = SimpleStringUtil.json2TypeObject(inputStream,containType, clazz);
		} finally {
			inputStream.close();
		}

		return var4;
	}

	public static <T> List<T> converJson2List(HttpEntity entity, Class<T> clazz) throws IOException {
		InputStream inputStream = null;

		List<T> var4 = null;
		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}
			var4 = SimpleStringUtil.json2ListObject(inputStream, clazz);
		} finally {
			if(inputStream != null)
				inputStream.close();
		}

		return var4;
	}

	public static <T> Set<T> converJson2Set(HttpEntity entity, Class<T> clazz) throws IOException {
		InputStream inputStream = null;

		Set<T> var4;
		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}
			var4 = SimpleStringUtil.json2LSetObject(inputStream, clazz);
		} finally {
			inputStream.close();
		}

		return var4;
	}

	public static <K,T> Map<K,T> converJson2Map(HttpEntity entity, Class<K> keyType, Class<T> beanType) throws IOException {
		InputStream inputStream = null;

		Map<K,T> var4;
		try {

			inputStream = entity.getContent();
			if(entityEmpty(entity,inputStream)){
				return null;
			}
			var4 = SimpleStringUtil.json2LHashObject(inputStream,  keyType, beanType);
		} finally {
			inputStream.close();
		}

		return var4;
	}
}
