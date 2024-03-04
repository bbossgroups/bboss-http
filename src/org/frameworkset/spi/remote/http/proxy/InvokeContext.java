package org.frameworkset.spi.remote.http.proxy;
/**
 * Copyright 2024 bboss
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

import org.apache.http.entity.ContentType;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/3/3
 */
public class InvokeContext {

    private Map<String,Object> context;
    private ContentType requestContentType;
    private java.nio.charset.Charset responseCharset;
    private Map headers;
    
    public Map<String, Object> getContext() {
        return context;
    }

    public InvokeContext addParam(String name,Object value){
        if(context == null){
            context = new LinkedHashMap<>();
        }
        context.put(name,value);
        return this;
    }
    
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public ContentType getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(ContentType requestContentType) {
        this.requestContentType = requestContentType;
    }

    public Charset getResponseCharset() {
        return responseCharset;
    }

    public void setResponseCharset(Charset responseCharset) {
        this.responseCharset = responseCharset;
    }


    public Map getHeaders() {
        return headers;
    }

    public void setHeaders(Map headers) {
        this.headers = headers;
    }
}
