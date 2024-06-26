package org.frameworkset.http.client;
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

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.callback.HttpClientBuilderCallback;

import java.io.IOException;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2020/5/13 17:57
 * @author biaoping.yin
 * @version 1.0
 */
public class HttpClientBuilderCallbackTest {
	public static void main(String[] args){
		/**
		 * AWSCredentials credentials = new BasicAWSCredentials("", "");
		 * 		AWS4Signer signer = new AWS4Signer();
		 * 		AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(credentials);
		 * 		signer.setServiceName("es");
		 * 		signer.setRegionName("us-east-1");
		 *
		 * 		final HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(
		 * 				"es", signer, awsCredentialsProvider);
		 */
		final HttpRequestInterceptor interceptor = new HttpRequestInterceptor(){

			@Override
			public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                request.addHeader("test","value");//添加自定义请求头
			}
		};
		HttpClientBuilderCallback httpClientBuilderCallback = new HttpClientBuilderCallback() {
			@Override
			public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder, ClientConfiguration clientConfiguration) {
				return builder.addInterceptorFirst(interceptor);
			}
		};

	}
}
