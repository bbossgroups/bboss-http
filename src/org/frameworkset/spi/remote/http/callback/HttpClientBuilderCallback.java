package org.frameworkset.spi.remote.http.callback;
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

import org.apache.http.impl.client.HttpClientBuilder;
import org.frameworkset.spi.remote.http.ClientConfiguration;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2020/5/13 17:56
 * @author biaoping.yin
 * @version 1.0
 */
public interface HttpClientBuilderCallback {
	public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder , ClientConfiguration clientConfiguration) throws Exception;
}
