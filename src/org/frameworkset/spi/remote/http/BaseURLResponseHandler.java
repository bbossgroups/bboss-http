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

import org.frameworkset.spi.remote.http.callback.ExecuteIntercepter;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2020/2/27 15:40
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class BaseURLResponseHandler<T> implements URLResponseHandler<T> {
	protected String url;
    /**
     * 标记是否在响应对象中放置请求报文，便于在异常处理中放置请求报文数据
     */
    protected boolean enableSetRequestBody;
    protected String requestBody;
    private ExecuteIntercepter executeIntercepter; 
    public ExecuteIntercepter getExecuteIntercepter(){
        return executeIntercepter;
    }
    public void setExecuteIntercepter(ExecuteIntercepter executeIntercepter){
        this.executeIntercepter = executeIntercepter;
    }

    protected boolean truncateLogBody;
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    public boolean isEnableSetRequestBody() {
        return enableSetRequestBody;
    }

    public void setEnableSetRequestBody(boolean enableSetRequestBody) {
        this.enableSetRequestBody = enableSetRequestBody;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    public boolean isTruncateLogBody() {
        return truncateLogBody;
    }

    public void setTruncateLogBody(boolean truncateLogBody) {
        this.truncateLogBody = truncateLogBody;
    }
}
