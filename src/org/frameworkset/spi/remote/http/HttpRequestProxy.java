package org.frameworkset.spi.remote.http;

import com.frameworkset.util.SimpleStringUtil;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.frameworkset.spi.remote.http.proxy.*;
import org.frameworkset.util.ResourceStartResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

import static org.frameworkset.spi.remote.http.HttpRequestUtil.object2json;

/**
 * @author yinbp
 * @Date:2016-11-20 11:39:59
 */
public class HttpRequestProxy {

    private static Logger logger = LoggerFactory.getLogger(HttpRequestProxy.class);
    public static ResourceStartResult startHttpPools(String configFile){
        return HttpRequestUtil.startHttpPools(configFile);
    }
    public static ResourceStartResult startHttpPools(Map<String,Object> configs){
        return HttpRequestUtil.startHttpPools(configs);
    }

    public static ResourceStartResult startHttpPoolsFromApollo(String namespaces){
        return HttpRequestUtil.startHttpPoolsFromApollo(namespaces);
    }
    public static ResourceStartResult startHttpPoolsFromApollo(String namespaces,String configChangeListener){
        return HttpRequestUtil.startHttpPoolsFromApollo(namespaces,configChangeListener);
    }

    public static ResourceStartResult startHttpPoolsFromApolloAwaredChange(String namespaces){
        return HttpRequestUtil.startHttpPoolsFromApolloAwaredChange(namespaces);
    }





    public static String httpGetforString(String url) throws HttpProxyRequestException {
        return httpGetforString(url, (String) null, (String) null, (Map<String, String>) null);
    }
    public static <T> T httpGetforObject(String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString("default",url, (String) null, (String) null, (Map<String, String>) null, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleResponse(url,  response, resultType);
            }

        });
    }
    public static String httpGetforString(String poolname, String url) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, (Map<String, String>) null);
    }

    public static <T> T httpGetforObject(String poolname, String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, (Map<String, String>) null, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleResponse(  url,response, resultType);
            }

        });
    }

    public static <T> List<T> httpGetforList(String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString("default",url, (String) null, (String) null, (Map<String, String>) null, new BaseURLResponseHandler<List<T>>() {

            @Override
            public List<T> handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleListResponse(  url,response, resultType);
            }

        });
    }

    public static <K,T> Map<K,T> httpGetforMap(String url,final Class<K> keyType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString("default",url, (String) null, (String) null, (Map<String, String>) null, new BaseURLResponseHandler<Map<K,T> >() {

            @Override
            public Map<K,T>  handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleMapResponse(  url,response,keyType, resultType);
            }

        });
    }

    public static <T> Set<T> httpGetforSet(String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString("default",url, (String) null, (String) null, (Map<String, String>) null, new BaseURLResponseHandler<Set<T>>() {

            @Override
            public Set<T> handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleSetResponse(url,  response, resultType);
            }

        });
    }

    public static <T> List<T> httpGetforList(String poolName,String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, (Map<String, String>) null, new BaseURLResponseHandler<List<T>>() {

            @Override
            public List<T> handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleListResponse( url, response, resultType);
            }

        });
    }
    public static <D,T> D httpGetforTypeObject(String url,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforTypeObject("default", url, containType, resultType);
    }
    public static <D,T> D httpGetforTypeObject(String poolName,String url,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, (Map<String, String>) null, new BaseURLResponseHandler<D>() {

            @Override
            public D handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleResponse( url, response,containType, resultType);
            }

        });
    }
    public static <K,T> Map<K,T> httpGetforMap(String poolName,String url,final Class<K> keyType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, (Map<String, String>) null, new BaseURLResponseHandler<Map<K,T> >() {

            @Override
            public Map<K,T>  handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleMapResponse( url, response,keyType, resultType);
            }

        });
    }

    public static <T> Set<T> httpGetforSet(String poolName,String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, (Map<String, String>) null, new BaseURLResponseHandler<Set<T>>() {

            @Override
            public Set<T> handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleSetResponse( url, response, resultType);
            }

        });
    }
    public static <T> T httpGet(String poolname, String url,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, (Map<String, String>) null,responseHandler);
    }

    public static <T> T httpGet(String poolname, String url,Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, headers,responseHandler);
    }

    public static <T> T httpGet(String url,Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString("default", url, (String) null, (String) null, headers,responseHandler);
    }

    public static String httpGetforString(String url, Map<String, String> headers) throws HttpProxyRequestException {
        return httpGetforString(url, (String) null, (String) null, headers);
    }

    public static String httpGetforString(String poolname, String url, Map<String, String> headers) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, headers);
    }

    public static <T> T httpGetforString(String poolname, String url, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, headers,responseHandler);
    }

    public static <T> T httpGetforString( String url, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString("default",  url, (String) null, (String) null, headers,responseHandler);
    }

    public static String httpGetforString(String url, String cookie, String userAgent, Map<String, String> headers) throws HttpProxyRequestException {
        return httpGetforString("default", url, cookie, userAgent, headers);
    }
    public static String httpGetforString(String poolname, String url, String cookie, String userAgent, Map<String, String> headers) throws HttpProxyRequestException{
        return  httpGetforString(poolname, url, cookie, userAgent, headers,new StringResponseHandler()) ;
    }
    /**
     * get请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpGetforString(String poolname, String url, String cookie, String userAgent, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
       return httpGet(  poolname,   url,   cookie,   userAgent,   headers, responseHandler);
    }
    private static Exception getException(ResponseHandler responseHandler,HttpServiceHosts httpServiceHosts ){
//        assertCheck(  httpServiceHosts );
        ExceptionWare exceptionWare = httpServiceHosts.getExceptionWare();
        if(exceptionWare != null) {
            return exceptionWare.getExceptionFromResponse(responseHandler);
        }
        return null;
    }
    /**
     * get请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpGet(String poolname, String url, final String cookie,final  String userAgent,final  Map<String, String> headers,final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        // String cookie = getCookie();
        // String userAgent = getUserAgent();
        return _handleRequest( poolname,  url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpGet httpGet = null;
                        try {
                            httpGet = HttpRequestUtil.getHttpGet(config, url, cookie, userAgent, headers);

                            Object responseBody = httpClient.execute(httpGet, responseHandler);
                            return responseBody;
                        }
                        finally {
                            // 释放连接
                            if (httpGet != null)
                                httpGet.releaseConnection();
                        }
                    }
                } );
        /**
        HttpClient httpClient = null;
        HttpGet httpGet = null;

        T responseBody = null;
//        int time = 0;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
//        int RETRY_TIME = config.getRetryTime();
//        do {
        String endpoint = null;
        Throwable e = null;
        int triesCount = 0;
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            endpoint = url;
            HttpAddress httpAddress = null;
			HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts );
            do {

                try {

                    httpAddress = httpServiceHosts.getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if(logger.isDebugEnabled()){
                        logger.debug("Get call {}",url);
                    }
                    httpClient = HttpRequestUtil.getHttpClient(config);
                    httpGet = HttpRequestUtil.getHttpGet(config, url, cookie, userAgent, headers);
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpGet, responseHandler);
                    httpAddress.recover();
                    e = getException(  responseHandler,httpServiceHosts );
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException( poolname,url,config,ex);
                    break;
                }
                catch (ConnectTimeoutException connectTimeoutException){
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                }

                catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(poolname,url,config, ex);
                    break;
                }
                catch (NoHttpServerException ex){
                    e = ex;

                    break;
                }
                catch (ClientProtocolException ex){
                    throw new HttpProxyRequestException(new StringBuilder().append("Get Request[").append(url)
                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                }

                catch (Exception ex) {
                    e = ex;
                    break;
                }
                catch (Throwable ex) {
                    e = ex;
                    break;
                } finally {
                    // 释放连接
                    if (httpGet != null)
                        httpGet.releaseConnection();
                    httpClient = null;
                }
            } while (true);
        }
        else{
            try {
                if(logger.isDebugEnabled()){
                    logger.debug("Get call {}",url);
                }
                httpClient = HttpRequestUtil.getHttpClient(config);
                httpGet = HttpRequestUtil.getHttpGet(config, url, cookie, userAgent, headers);
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpGet, responseHandler);

            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpGet != null)
                    httpGet.releaseConnection();
                httpClient = null;
            }
        }
        if (e != null){
            if(e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException)e;
            throw new HttpProxyRequestException("Get request url:"+url,e);
        }
        return responseBody;
         */

    }

    /**
     * head请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String poolname, String url,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpHead(  poolname,   url,   null, null, (Map<String, String>) null,responseHandler);

    }

    /**
     * get请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String url,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpHead(  "default",   url,   null, null, (Map<String, String>) null,responseHandler);

    }

    /**
     * head请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String poolname, String url,Map<String, Object> params,Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpHead(  poolname,   url,   null, null,params, (Map<String, String>) headers,responseHandler);

    }

    /**
     * get请求URL
     * ,Map<String, Object> params,Map<String, String> headers,
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String poolname, String url, String cookie, String userAgent, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
       return httpHead(  poolname,   url,   cookie,   userAgent,(Map<String, Object> )null, headers,responseHandler);

    }

    /**
     * get请求URL
     * ,Map<String, Object> params,Map<String, String> headers,
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String poolname, String url, final String cookie, final String userAgent,final Map<String, Object> params, final Map<String, String> headers,
                                 final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        // String cookie = getCookie();
        // String userAgent = getUserAgent();
        return _handleRequest( poolname, url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpHead httpHead = null;
                        try {
                            httpHead = HttpRequestUtil.getHttpHead(config, url, cookie, userAgent, headers);
                            HttpParams httpParams = null;
                            if (params != null && params.size() > 0) {
                                httpParams = new BasicHttpParams();
                                Iterator<Entry<String, Object>> it = params.entrySet().iterator();
                                NameValuePair paramPair_ = null;
                                for (int i = 0; it.hasNext(); i++) {
                                    Entry<String, Object> entry = it.next();
                                    httpParams.setParameter(entry.getKey(), entry.getValue());
                                }
                                httpHead.setParams(httpParams);
                            }

                            Object responseBody = httpClient.execute(httpHead, responseHandler);
                            return responseBody;
                        }
                        finally {
                            // 释放连接
                            if (httpHead != null)
                                httpHead.releaseConnection();
                        }
                    }
                } );
        /**
        HttpClient httpClient = null;
        HttpHead httpHead = null;

        T responseBody = null;
//        int time = 0;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
        String endpoint = null;
        Throwable e = null;
        int triesCount = 0;
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            endpoint = url;
            HttpAddress httpAddress = null;
			HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts );
            do {

                try {

                    httpAddress = httpServiceHosts.getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if(logger.isDebugEnabled()){
                        logger.debug("Head call {}",url);
                    }
                    httpClient = HttpRequestUtil.getHttpClient(config);
                    httpHead = HttpRequestUtil.getHttpHead(config, url, cookie, userAgent, headers);
                    HttpParams httpParams = null;
                    if (params != null && params.size() > 0) {
                        httpParams = new BasicHttpParams();
                        Iterator<Entry<String, Object>> it = params.entrySet().iterator();
                        NameValuePair paramPair_ = null;
                        for (int i = 0; it.hasNext(); i++) {
                            Entry<String, Object> entry = it.next();
                            httpParams.setParameter(entry.getKey(), entry.getValue());
                        }
                        httpHead.setParams(httpParams);
                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpHead, responseHandler);
                    httpAddress.recover();
                    e = getException(  responseHandler,httpServiceHosts );
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException( poolname,url,config,ex);
                    break;
                }
                catch (ConnectTimeoutException connectTimeoutException){
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                }

                catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(poolname,url,config, ex);
                    break;
                }
                catch (NoHttpServerException ex){
                    e = ex;

                    break;
                }
                catch (ClientProtocolException ex){
                    httpAddress.setStatus(1);
                    e = ex;
                    if(logger.isErrorEnabled())
                        logger.error(new StringBuilder().append("Head Request[").append(url)
                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }

                catch (Exception ex) {
                    e = ex;
                    break;
                }
                catch (Throwable ex) {
                    e = ex;
                    break;
                } finally {
                    // 释放连接
                    if (httpHead != null)
                        httpHead.releaseConnection();
                    httpClient = null;
                }
            } while (true);
        }
        else{
            try {

                if(logger.isDebugEnabled()){
                    logger.debug("Head call {}",url);
                }
                httpClient = HttpRequestUtil.getHttpClient(config);
                httpHead = HttpRequestUtil.getHttpHead(config, url, cookie, userAgent, headers);
                HttpParams httpParams = null;
                if (params != null && params.size() > 0) {
                    httpParams = new BasicHttpParams();
                    Iterator<Entry<String, Object>> it = params.entrySet().iterator();
                    NameValuePair paramPair_ = null;
                    for (int i = 0; it.hasNext(); i++) {
                        Entry<String, Object> entry = it.next();
                        httpParams.setParameter(entry.getKey(), entry.getValue());
                    }
                    httpHead.setParams(httpParams);
                }
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpHead, responseHandler);

            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpHead != null)
                    httpHead.releaseConnection();
                httpClient = null;
            }
        }
        if (e != null){
            if(e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException)e;
            throw new HttpProxyRequestException("Head request url:"+url,e);
        }
        return responseBody;
         */

    }


    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param files
     * @throws HttpProxyRequestException
     */
    public static String httpPostFileforString(String url, Map<String, Object> params, Map<String, File> files)
            throws HttpProxyRequestException {
        return httpPostFileforString("default", url, (String) null, (String) null, params, files);
    }

    public static String httpPostFileforString(String poolname, String url, Map<String, Object> params, Map<String, File> files)
            throws HttpProxyRequestException {
        return httpPostFileforString(poolname, url, (String) null, (String) null, params, files);
    }

    public static String httpPostforString(String url, Map<String, Object> params) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map<String, String>) null);
    }

    public static <T> T httpPost(String url, Map<String, Object> params,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map<String, String>) null, responseHandler);
    }

    public static <T> T httpPostForObject(String url, Map<String, Object> params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map<String, String>) null, new BaseURLResponseHandler<T>() {
            @Override
            public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleResponse(url,response,resultType);
            }
        });
    }

    public static <T> List<T> httpPostForList(String url, Map<String, Object> params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map<String, String>) null, new BaseURLResponseHandler<List<T>>() {
            @Override
            public List<T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
    }
    public static <T> Set<T> httpPostForSet(String url, Map<String, Object> params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map<String, String>) null, new BaseURLResponseHandler<Set<T>>() {
            @Override
            public Set<T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleSetResponse(url,response,resultType);
            }
        });
    }

    public static <K,T> Map<K,T> httpPostForMap(String url, Map<String, Object> params, final Class<K> keyType, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map<String, String>) null, new BaseURLResponseHandler<Map<K,T>>() {
            @Override
            public Map<K,T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
            }
        });
    }

    public static <T> T httpPostForObject(String poolName,String url, Map<String, Object> params, final Class<T> resultType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;

		return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<T>() {
			@Override
			public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				return ResponseUtil.handleResponse(url,response,resultType);
			}
		});
//        return httpPostforString(  poolName,url, params, (Map<String, String>) null);
    }

	public static <T> T httpPostForObject(String poolName,String url, Map<String, Object> params, final Class<T> resultType,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;

		return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<T>() {
			@Override
			public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				return ResponseUtil.handleResponse(url,response,resultType);
			}
		});
//        return httpPostforString(  poolName,url, params, (Map<String, String>) null);
	}

	public static <T> List<T> httpPostForList(String poolName,String url, Map<String, Object> params, final Class<T> resultType,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;

		return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<List<T>>() {
			@Override
			public List<T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				return ResponseUtil.handleListResponse(url,response,resultType);
			}
		});

	}
	public static <T> Set<T> httpPostForSet(String poolName,String url, Map<String, Object> params, final Class<T> resultType,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;

		return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<Set<T>>() {
			@Override
			public Set<T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				return ResponseUtil.handleSetResponse(url,response,resultType);
			}
		});
//		return httpPostforString(  poolName,url, params, (Map<String, String>) null, new ResponseHandler<Set<T>>() {
//			@Override
//			public Set<T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//				return ResponseUtil.handleSetResponse(response,resultType);
//			}
//		});
	}

	public static <K,T> Map<K,T> httpPostForMap(String poolName,String url, Map<String, Object> params, final Class<K> keyType, final Class<T> resultType,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;

		return httpPost(  poolName,   url,  httpOption,new BaseURLResponseHandler<Map<K,T>>() {
			@Override
			public Map<K,T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
			}
		});
//    	return httpPostforString(poolName,url, params, (Map<String, String>) null, new ResponseHandler<Map<K,T>>() {
//			@Override
//			public Map<K,T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//				return ResponseUtil.handleMapResponse(response,keyType,resultType);
//			}
//		});
	}

    public static <T> List<T> httpPostForList(String poolName,String url, Map<String, Object> params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(  poolName,url, params, (Map<String, String>) null, new BaseURLResponseHandler<List<T>>() {
            @Override
            public List<T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
    }
    public static <T> List<T> httpPostForList(String poolName,String url , final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(  poolName,url, (Map<String, Object>) null, (Map<String, String>) null, new BaseURLResponseHandler<List<T>>() {
            @Override
            public List<T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
    }
    public static <T> List<T> httpPostForList(String url , final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(  (String)null,url, (Map<String, Object>) null, (Map<String, String>) null, new BaseURLResponseHandler<List<T>>() {
            @Override
            public List<T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
    }
    public static <T> Set<T> httpPostForSet(String poolName,String url, Map<String, Object> params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(  poolName,url, params, (Map<String, String>) null, new BaseURLResponseHandler<Set<T>>() {
            @Override
            public Set<T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleSetResponse(url,response,resultType);
            }
        });
    }

    public static <K,T> Map<K,T> httpPostForMap(String poolName,String url, Map<String, Object> params, final Class<K> keyType, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(poolName,url, params, (Map<String, String>) null, new BaseURLResponseHandler<Map<K,T>>() {
            @Override
            public Map<K,T>  handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
            }
        });
    }
    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static String httpPostforString(String url, Map<String, Object> params, Map<String, String> headers) throws HttpProxyRequestException {
        return httpPostFileforString("default", url, (String) null, (String) null, params, (Map<String, File>) null, headers);
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static String httpPostforString(String poolName,String url, Map<String, Object> params, Map<String, String> headers) throws HttpProxyRequestException {
        return httpPostFileforString(poolName, url, (String) null, (String) null, params, (Map<String, File>) null, headers);
    }



    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static  <T> T  httpPost(String url, Map<String, Object> params, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpPost("default", url, (String) null, (String) null, params, (Map<String, File>) null, headers, responseHandler);
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPostforString(String url, Map<String, Object> params, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpPost("default", url, (String) null, (String) null, params, (Map<String, File>) null, headers,responseHandler);
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPostforString(String poolName,String url, Map<String, Object> params, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpPost(poolName, url, (String) null, (String) null, params, (Map<String, File>) null, headers,responseHandler);
    }

    public static String httpPostforString(String poolname, String url, Map<String, Object> params) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;

		return httpPost(  poolname,   url,  httpOption,new StringResponseHandler());
//        return httpPostFileforString(poolname, url, (String) null, (String) null, params, (Map<String, File>) null);
    }

	public static String httpPostforString(String poolname, String url, Map<String, Object> params,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;
		return httpPost(  poolname,   url,  httpOption,new StringResponseHandler());
//        return httpPostFileforString(poolname, url, (String) null, (String) null, params, (Map<String, File>) null);
	}

    public static String httpPostforString(String url) throws HttpProxyRequestException {
        return httpPostforString("default", url);
    }

    public static <T> T  httpPost(String url,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpPost("default", url, responseHandler);
    }

    /**
     * 公用post方法
     *
     * @param poolname
     * @param url
     * @throws HttpProxyRequestException
     */
    public static String httpPostforString(String poolname, String url) throws HttpProxyRequestException {
        return httpPostFileforString(poolname, url, (String) null, (String) null, (Map<String, Object>) null,
                (Map<String, File>) null);
    }

    /**
     * 公用post方法
     *
     * @param poolname
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T  httpPost(String poolname, String url,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
    	return httpPost(  poolname,   url, (String) null, (String) null, (Map<String, Object>) null,
    			 (Map<String, File>) null, (Map<String, String>)null,responseHandler) ;

    }

    public static String httpPostforString(String url, String cookie, String userAgent,
                                           Map<String, File> files) throws HttpProxyRequestException {
        return httpPostforString("default", url, cookie, userAgent,
                files);
    }

    public static String httpPostforString(String poolname, String url, String cookie, String userAgent,
                                           Map<String, File> files) throws HttpProxyRequestException {
        return httpPostFileforString(poolname, url, cookie, userAgent, null,
                files);
    }

    public static String httpPostforString(String url, String cookie, String userAgent, Map<String, Object> params,
                                           Map<String, File> files) throws HttpProxyRequestException {
        return httpPostFileforString("default", url, cookie, userAgent, params,
                files);
    }

    public static String httpPostFileforString(String poolname, String url, String cookie, String userAgent, Map<String, Object> params,
                                               Map<String, File> files) throws HttpProxyRequestException {
        return httpPostFileforString(poolname, url, cookie, userAgent, params,
                files, null);
    }



	public static class HttpOption{
		private String cookie;
		private String userAgent;
		private Map<String, Object> params;
		private Map<String, File> files;
		private Map<String, String> headers;
		private DataSerialType dataSerialType = DataSerialType.TEXT;
	}

	/**
	 * 公用post方法
	 *
	 * @param poolname
	 * @param url
	 * @param httpOption
	 * @throws HttpProxyRequestException
	 */
	public static <T> T httpPost(String poolname, String url, final HttpOption httpOption,final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
		// System.out.println("post_url==> "+url);
		// String cookie = getCookie(appContext);
		// String userAgent = getUserAgent(appContext);
        HttpEntity httpEntity = null;
        List<NameValuePair> paramPair = null;
        if (httpOption.files != null) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            // post表单参数处理
            int length = (httpOption.params == null ? 0 : httpOption.params.size()) + (httpOption.files == null ? 0 : httpOption.files.size());

            int i = 0;
            boolean hasdata = false;

            if (httpOption.params != null) {
                Iterator<Entry<String, Object>> it = httpOption.params.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, Object> entry = it.next();
                    if(entry.getValue() == null)
                        continue;
                    if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String) {
                        multipartEntityBuilder.addTextBody(entry.getKey(), String.valueOf(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
                    }
                    else{

                        multipartEntityBuilder.addTextBody(entry.getKey(), SimpleStringUtil.object2json(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
                    }
                    hasdata = true;
                }
            }
            if (httpOption.files != null) {
                Iterator<Entry<String, File>> it = httpOption.files.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, File> entry = it.next();

//						parts[i++] = new FilePart(entry.getKey(), entry.getValue());
                    File f = new File(String.valueOf(entry.getValue()));
                    if (f.exists()) {
                        FileBody file = new FileBody(f);
                        multipartEntityBuilder.addPart(entry.getKey(), file);
                        hasdata = true;
                    } else {

                    }

                    // System.out.println("post_key_file==> "+file);
                }
            }
            if (hasdata)
                httpEntity = multipartEntityBuilder.build();
        } else if (httpOption.params != null && httpOption.params.size() > 0) {
            paramPair = new ArrayList<NameValuePair>();
            Iterator<Entry<String, Object>> it = httpOption.params.entrySet().iterator();
            NameValuePair paramPair_ = null;
            for (int i = 0; it.hasNext(); i++) {
                Entry<String, Object> entry = it.next();
                if(entry.getValue() == null)
                    continue;
                if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String) {
                    paramPair_ = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
                }
                else{
                    paramPair_ = new BasicNameValuePair(entry.getKey(), SimpleStringUtil.object2json(entry.getValue()));
                }
                paramPair.add(paramPair_);
            }
        }
        final HttpEntity _httpEntity = httpEntity;
        final List<NameValuePair> _paramPair = paramPair;
        return _handleRequest( poolname,  url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpPost httpPost = null;
                        try {
                            httpPost = HttpRequestUtil.getHttpPost(config, url, httpOption.cookie, httpOption.userAgent, httpOption.headers);


                            if (_httpEntity != null) {
                                httpPost.setEntity(_httpEntity);
                            } else if (_paramPair != null && _paramPair.size() > 0) {
                                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(_paramPair, Consts.UTF_8);

                                httpPost.setEntity(entity);

                            }

                            Object responseBody = httpClient.execute(httpPost, responseHandler);
                            return responseBody;
                        }
                        finally {
                            // 释放连接
                            if (httpPost != null)
                                httpPost.releaseConnection();
                        }
                    }
                } );
        /**
		HttpClient httpClient = null;
		HttpPost httpPost = null;

//
//                .addPart("bin", bin)
//                .addPart("comment", comment)
//                .build();
//				 FileBody bin = new FileBody(new File(args[0]));
//        StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
		HttpEntity httpEntity = null;
		List<NameValuePair> paramPair = null;
		if (httpOption.files != null) {
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			// post表单参数处理
			int length = (httpOption.params == null ? 0 : httpOption.params.size()) + (httpOption.files == null ? 0 : httpOption.files.size());

			int i = 0;
			boolean hasdata = false;

			if (httpOption.params != null) {
				Iterator<Entry<String, Object>> it = httpOption.params.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Object> entry = it.next();
					if(entry.getValue() == null)
						continue;
					if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String) {
						multipartEntityBuilder.addTextBody(entry.getKey(), String.valueOf(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
					}
					else{

						multipartEntityBuilder.addTextBody(entry.getKey(), SimpleStringUtil.object2json(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
					}
					hasdata = true;
				}
			}
			if (httpOption.files != null) {
				Iterator<Entry<String, File>> it = httpOption.files.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, File> entry = it.next();

//						parts[i++] = new FilePart(entry.getKey(), entry.getValue());
					File f = new File(String.valueOf(entry.getValue()));
					if (f.exists()) {
						FileBody file = new FileBody(f);
						multipartEntityBuilder.addPart(entry.getKey(), file);
						hasdata = true;
					} else {

					}

					// System.out.println("post_key_file==> "+file);
				}
			}
			if (hasdata)
				httpEntity = multipartEntityBuilder.build();
		} else if (httpOption.params != null && httpOption.params.size() > 0) {
			paramPair = new ArrayList<NameValuePair>();
			Iterator<Entry<String, Object>> it = httpOption.params.entrySet().iterator();
			NameValuePair paramPair_ = null;
			for (int i = 0; it.hasNext(); i++) {
				Entry<String, Object> entry = it.next();
				if(entry.getValue() == null)
					continue;
				if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String) {
					paramPair_ = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
				}
				else{
					paramPair_ = new BasicNameValuePair(entry.getKey(), SimpleStringUtil.object2json(entry.getValue()));
				}
				paramPair.add(paramPair_);
			}
		}

		T responseBody = null;
//        int time = 0;
		ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
//        int RETRY_TIME = config.getRetryTime();
//        do {
		String endpoint = null;
		Throwable e = null;
		int triesCount = 0;
		if(!url.startsWith("http://") && !url.startsWith("https://")) {
			endpoint = url;
			HttpAddress httpAddress = null;
			HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts );
			do {

				try {

					httpAddress = httpServiceHosts.getHttpAddress();

					url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
					if(logger.isDebugEnabled()){
						logger.debug("Post call {}",url);
					}
					httpClient = HttpRequestUtil.getHttpClient(config);
					httpPost = HttpRequestUtil.getHttpPost(config, url, httpOption.cookie, httpOption.userAgent, httpOption.headers);


					if (httpEntity != null) {
						httpPost.setEntity(httpEntity);
					} else if (paramPair != null && paramPair.size() > 0) {
						UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramPair, Consts.UTF_8);

						httpPost.setEntity(entity);

					}
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
					responseBody = httpClient.execute(httpPost, responseHandler);
                    httpAddress.recover();
					e = getException(  responseHandler,httpServiceHosts );
					break;
				} catch (HttpHostConnectException ex) {
					httpAddress.setStatus(1);
					e = new NoHttpServerException(ex);
					if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
						triesCount++;
						continue;
					} else {
						break;
					}

				} catch (UnknownHostException ex) {
					httpAddress.setStatus(1);
					e = new NoHttpServerException(ex);
					if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
						triesCount++;
						continue;
					} else {
						break;
					}

				}
				catch (NoRouteToHostException ex) {
					httpAddress.setStatus(1);
					e = new NoHttpServerException(ex);
					if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
						triesCount++;
						continue;
					} else {
						break;
					}

				}
				catch (NoHttpResponseException ex) {
					httpAddress.setStatus(1);
					e = new NoHttpServerException(ex);
					if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
						triesCount++;
						continue;
					} else {
						break;
					}

				}
				catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出

					e = handleConnectionPoolTimeOutException( poolname,url,config,ex);
					break;
				}
				catch (ConnectTimeoutException connectTimeoutException){
					httpAddress.setStatus(1);
					e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
					if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
						triesCount++;
						continue;
					} else {
						break;
					}
				}

				catch (SocketTimeoutException ex) {
					e = handleSocketTimeoutException(poolname,url,config, ex);
					break;
				}
				catch (NoHttpServerException ex){
					e = ex;

					break;
				}
				catch (ClientProtocolException ex){
                    httpAddress.setStatus(1);
                    e = ex;
                    if(logger.isErrorEnabled())
                        logger.error(new StringBuilder().append("Post Request[").append(url)
							.append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
//					throw new HttpProxyRequestException(new StringBuilder().append("Post Request[").append(url)
//							.append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
				}

				catch (Exception ex) {
					e = ex;
					break;
				}
				catch (Throwable ex) {
					e = ex;
					break;
				} finally {
					// 释放连接
					if (httpPost != null)
						httpPost.releaseConnection();
					httpClient = null;
				}
			} while (true);
		}
		else{
			try {

				if(logger.isDebugEnabled()){
					logger.debug("Post call {}",url);
				}
				httpClient = HttpRequestUtil.getHttpClient(config);
				httpPost = HttpRequestUtil.getHttpPost(config, url, httpOption.cookie, httpOption.userAgent, httpOption.headers);


				if (httpEntity != null) {
					httpPost.setEntity(httpEntity);
				} else if (paramPair != null && paramPair.size() > 0) {
					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramPair, Consts.UTF_8);

					httpPost.setEntity(entity);

				}
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
				responseBody = httpClient.execute(httpPost, responseHandler);

			} catch (Exception ex) {
				e = ex;
			} finally {
				// 释放连接
				if (httpPost != null)
					httpPost.releaseConnection();
				httpClient = null;
			}
		}
		if (e != null){
			if(e instanceof HttpProxyRequestException)
				throw (HttpProxyRequestException)e;
			throw new HttpProxyRequestException("Post Url:"+url,e);
		}
		return responseBody;
         */

	}
    /**
     * 公用post方法
     *
     * @param poolname
     * @param url
     * @param cookie
     * @param userAgent
     * @param params
     * @param files
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPost(String poolname, String url, String cookie, String userAgent, Map<String, Object> params,
                                               Map<String, File> files, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();
		httpOption.cookie = cookie;
		httpOption.userAgent = userAgent;
		httpOption.params = params;
		httpOption.files = files;
		httpOption.headers = headers;
    	return httpPost(  poolname,   url,  httpOption,  responseHandler);

    }

    /**
     * 公用post方法
     *
     * @param poolname
     * @param url
     * @param cookie
     * @param userAgent
     * @param params
     * @param files
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static String httpPutforString(String poolname, String url, String cookie, String userAgent, Map<String, Object> params,
                                               Map<String, File> files, Map<String, String> headers) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.cookie = cookie;
        httpOption.userAgent = userAgent;
        httpOption.params = params;
        httpOption.files = files;
        httpOption.headers = headers;
        return httpPut(  poolname,   url,   httpOption,  new StringResponseHandler());

    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static String httpPutforString(  String url,Map<String, Object> params, Map<String, String> headers ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut(  "default",   url,   httpOption,  new StringResponseHandler());
//    	return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//    			( Map<String, File> )null,   headers,new StringResponseHandler());
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static <T> T httpPutforObject(String url, Map<String, Object> params, Map<String, String> headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut(  "default",   url,   httpOption,  new BaseURLResponseHandler<T>(){

            @Override
            public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleResponse(url,response,resultType);
            }
        });
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<T>(){
//
//                    @Override
//                    public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//                        return ResponseUtil.handleResponse(response,resultType);
//                    }
//                });
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static <T> List<T> httpPutforList(String url, Map<String, Object> params, Map<String, String> headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( "default",   url,   httpOption, new BaseURLResponseHandler<List<T>>(){

            @Override
            public List<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<List<T>>(){
//
//                    @Override
//                    public List<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//                        return ResponseUtil.handleListResponse(response,resultType);
//                    }
//                });
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static <T> Set<T> httpPutforSet(String url, Map<String, Object> params, Map<String, String> headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( "default",   url,   httpOption, new BaseURLResponseHandler<Set<T>>(){

            @Override
            public Set<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleSetResponse(url,response,resultType);
            }
        });
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<Set<T>>(){
//
//                    @Override
//                    public Set<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//                        return ResponseUtil.handleSetResponse(response,resultType);
//                    }
//                });
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static <K,T> Map<K,T> httpPutforObject(String url, Map<String, Object> params, Map<String, String> headers, final Class<K> keyType , final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut(  "default",   url,   httpOption,  new BaseURLResponseHandler<Map<K,T>>(){

            @Override
            public Map<K,T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
            }
        });
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<Map<K,T>>(){
//
//                    @Override
//                    public Map<K,T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//                        return ResponseUtil.handleMapResponse(response,keyType,resultType);
//                    }
//                });
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static <T> T httpPutforObject(String poolName,String url, Map<String, Object> params, Map<String, String> headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolName,   url,   httpOption, new BaseURLResponseHandler<T>(){

            @Override
            public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleResponse(url,response,resultType);
            }
        });
//        return httpPut(  poolName,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<T>(){
//
//                    @Override
//                    public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//                        return ResponseUtil.handleResponse(response,resultType);
//                    }
//                });
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static <T> List<T> httpPutforList(String poolName,String url, Map<String, Object> params, Map<String, String> headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolName,   url,   httpOption, new BaseURLResponseHandler<List<T>>(){

            @Override
            public List<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
//        return httpPut(  poolName,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<List<T>>(){
//
//                    @Override
//                    public List<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//                        return ResponseUtil.handleListResponse(response,resultType);
//                    }
//                });
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static <T> Set<T> httpPutforSet(String poolName,String url, Map<String, Object> params, Map<String, String> headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolName,   url,   httpOption, new BaseURLResponseHandler<Set<T>>(){

            @Override
            public Set<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleSetResponse(url,response,resultType);
            }
        });
//        return httpPut(  poolName,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<Set<T>>(){
//
//                    @Override
//                    public Set<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//                        return ResponseUtil.handleSetResponse(response,resultType);
//                    }
//                });
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static <K,T> Map<K,T> httpPutforObject(String poolName,String url, Map<String, Object> params, Map<String, String> headers, final Class<K> keyType , final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolName,   url,   httpOption, new BaseURLResponseHandler<Map<K,T>>(){

            @Override
            public Map<K,T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
            }
        });
//        return httpPut(   poolName,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<Map<K,T>>(){
//
//                    @Override
//                    public Map<K,T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//                        return ResponseUtil.handleMapResponse(response,keyType,resultType);
//                    }
//                });
    }

    public static <T> T httpPutforString(  String url,Map<String, Object> params, Map<String, String> headers ,ResponseHandler<T> responseHandler ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( "default",   url,   httpOption, responseHandler);
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers ,responseHandler);
    }

    public static String httpPutforString(String poolname,  String url,Map<String, Object> params, Map<String, String> headers ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolname,   url,   httpOption,new StringResponseHandler());
//        return httpPut(  poolname,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new StringResponseHandler());
    }

    public static <T> T httpPutforString(String poolname,  String url,Map<String, Object> params, Map<String, String> headers ,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolname,   url,   httpOption,responseHandler);
//        return httpPut(  poolname,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,responseHandler);
    }

    /**
     * 公用post方法
     *

     * @param url
     * @param cookie
     * @param userAgent
     * @param params
     * @param files
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPut(String url, String cookie, String userAgent, Map<String, Object> params,
                                               Map<String, File> files, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        HttpOption httpOption = new HttpOption();
        httpOption.cookie = cookie;
        httpOption.userAgent = userAgent;
        httpOption.params = params;
        httpOption.headers = headers;
        httpOption.files = files;
        return httpPut( "default",   url,   httpOption,responseHandler);
//        return httpPut("default", url, cookie, userAgent, params,
//                                                    files, headers, responseHandler) ;
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @param responseHandler
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPut(String url, Map<String, Object> params,  Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
    	return httpPut( url, (String)null, (String)null, (Map<String, Object>)params,
    							(Map<String, File>)null, headers, responseHandler) ;
    }

    /**
     *
     * @param url
     * @param params
     * @param responseHandler
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPut(String url, Map<String, Object> params,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
    	return httpPut( url, (String)null, (String)null, (Map<String, Object>)params,
    							(Map<String, File>)null, (Map<String, String>)null, responseHandler) ;
    }

    /**
     *
     * @param url
     * @param responseHandler
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPut(String url,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
    	return httpPut( url, (String)null, (String)null, (Map<String, Object>)null,
    							(Map<String, File>)null, (Map<String, String>)null, responseHandler) ;
    }

    /**
     *
     * @param url
     * @param resultType
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPut(String url, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPut(url, (String) null, (String) null, (Map<String, Object>) null,
                (Map<String, File>) null, (Map<String, String>) null, new BaseURLResponseHandler<T>() {
                    @Override
                    public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return ResponseUtil.handleResponse(url,response,resultType);
                    }
                }) ;
    }

    /**
     *
     * @param url
     * @param resultType
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    public static <T> List<T> httpPutForList(String url, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPut(url, (String) null, (String) null, (Map<String, Object>) null,
                (Map<String, File>) null, (Map<String, String>) null, new BaseURLResponseHandler<List<T>>() {
                    @Override
                    public List<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return ResponseUtil.handleListResponse(url,response,resultType);
                    }
                }) ;
    }

    /**
     *
     * @param url
     * @param resultType
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    public static <T> Set<T> httpPutForSet(String url, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPut(url, (String) null, (String) null, (Map<String, Object>) null,
                (Map<String, File>) null, (Map<String, String>) null, new BaseURLResponseHandler<Set<T>>() {
                    @Override
                    public Set<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return ResponseUtil.handleSetResponse(url,response,resultType);
                    }
                }) ;
    }

    /**
     *
     * @param url
     * @param resultType
     * @return
     * @throws HttpProxyRequestException
     */
    public static <K,T> Map<K,T> httpPutForMap(String url, final Class<K> keyType, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPut(url, (String) null, (String) null, (Map<String, Object>) null,
                (Map<String, File>) null, (Map<String, String>) null, new BaseURLResponseHandler<Map<K,T>>() {
                    @Override
                    public Map<K,T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
                    }
                }) ;
    }

    /**
     *
     * @param url
     * @param resultType
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPut(String poolName,String url, final Class<T> resultType) throws HttpProxyRequestException {
        HttpOption httpOption = new HttpOption();
        return httpPut(  poolName,url, httpOption, new BaseURLResponseHandler<T>() {
                    @Override
                    public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return ResponseUtil.handleResponse(url,response,resultType);
                    }
                }) ;
    }

    /**
     *
     * @param url
     * @param resultType
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    public static <T> List<T> httpPutForList(String poolName,String url, final Class<T> resultType) throws HttpProxyRequestException {
        HttpOption httpOption = new HttpOption();
        return httpPut(  poolName,url, httpOption, new BaseURLResponseHandler<List<T>>() {
                    @Override
                    public List<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return ResponseUtil.handleListResponse(url,response,resultType);
                    }
                }) ;
    }

    /**
     *
     * @param url
     * @param resultType
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    public static <T> Set<T> httpPutForSet(String poolName,String url, final Class<T> resultType) throws HttpProxyRequestException {
        HttpOption httpOption = new HttpOption();
        return httpPut(  poolName,url,httpOption, new BaseURLResponseHandler<Set<T>>() {
                    @Override
                    public Set<T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return ResponseUtil.handleSetResponse(url,response,resultType);
                    }
                }) ;
    }

    /**
     *
     * @param url
     * @param resultType
     * @return
     * @throws HttpProxyRequestException
     */
    public static <K,T> Map<K,T> httpPutForMap(String poolName,String url, final Class<K> keyType, final Class<T> resultType) throws HttpProxyRequestException {
        HttpOption httpOption = new HttpOption();
        return httpPut(  poolName,url, httpOption, new BaseURLResponseHandler<Map<K,T>>() {
                    @Override
                    public Map<K,T> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                        return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
                    }
                }) ;
    }
    /**
     * 公用post方法
     *
     * @param poolname
     * @param url
     * @param cookie
     * @param userAgent
     * @param params
     * @param files
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPut(String poolname, String url, String cookie, String userAgent, Map<String, Object> params,
                                               Map<String, File> files, Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        // System.out.println("post_url==> "+url);
        // String cookie = getCookie(appContext);
        // String userAgent = getUserAgent(appContext);
        HttpOption httpOption = new HttpOption();
        httpOption.cookie = cookie;
        httpOption.userAgent = userAgent;
        httpOption.params = params;
        httpOption.files = files;
        httpOption.headers = headers;
        return httpPut(  poolname,   url,   httpOption,  responseHandler);

    }


    /**
     * 公用put方法
     *
     * @param poolname
     * @param url
     * @param httpOption
     * @param responseHandler
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPut(String poolname, String url, final HttpOption httpOption,final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        // System.out.println("post_url==> "+url);
        // String cookie = getCookie(appContext);
        // String userAgent = getUserAgent(appContext);
        HttpEntity httpEntity = null;
        List<NameValuePair> paramPair = null;
        if (httpOption.files != null) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            // post表单参数处理
            int length = (httpOption.params == null ? 0 : httpOption.params.size()) + (httpOption.files == null ? 0 : httpOption.files.size());

            int i = 0;
            boolean hasdata = false;

            if (httpOption.params != null) {
                Iterator<Entry<String, Object>> it = httpOption.params.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, Object> entry = it.next();
                    if(entry.getValue() == null){
                        continue;
                    }
                    if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String)
                        multipartEntityBuilder.addTextBody(entry.getKey(), String.valueOf(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
                    else {
                        multipartEntityBuilder.addTextBody(entry.getKey(), SimpleStringUtil.object2json(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
                    }
                    hasdata = true;
                }
            }
            if (httpOption.files != null) {
                Iterator<Entry<String, File>> it = httpOption.files.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, File> entry = it.next();

//						parts[i++] = new FilePart(entry.getKey(), entry.getValue());
                    File f = new File(String.valueOf(entry.getValue()));
                    if (f.exists()) {
                        FileBody file = new FileBody(f);
                        multipartEntityBuilder.addPart(entry.getKey(), file);
                        hasdata = true;
                    } else {

                    }

                    // System.out.println("post_key_file==> "+file);
                }
            }
            if (hasdata)
                httpEntity = multipartEntityBuilder.build();
        } else if (httpOption.params != null && httpOption.params.size() > 0) {
            paramPair = new ArrayList<NameValuePair>();
            Iterator<Entry<String, Object>> it = httpOption.params.entrySet().iterator();
            NameValuePair paramPair_ = null;
            for (int i = 0; it.hasNext(); i++) {
                Entry<String, Object> entry = it.next();
                if(entry.getValue() == null){
                    continue;
                }
                if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String) {
                    paramPair_ = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
                }
                else{
                    paramPair_ = new BasicNameValuePair(entry.getKey(), SimpleStringUtil.object2json(entry.getValue()));
                }
                paramPair.add(paramPair_);
            }
        }
        final HttpEntity _httpEntity = httpEntity;
        final List<NameValuePair> _paramPair = paramPair;
        return _handleRequest( poolname,  url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpPut httpPut = null;
                        try {
                            httpPut = HttpRequestUtil.getHttpPut(config, url, httpOption.cookie, httpOption.userAgent, httpOption.headers);


                            if (_httpEntity != null) {
                                httpPut.setEntity(_httpEntity);
                            } else if (_paramPair != null && _paramPair.size() > 0) {
                                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(_paramPair, Consts.UTF_8);

                                httpPut.setEntity(entity);

                            }

                            Object responseBody = httpClient.execute(httpPut, responseHandler);
                            return responseBody;
                        }
                        finally {
                            // 释放连接
                            if (httpPut != null)
                                httpPut.releaseConnection();
                        }
                    }
                } );
        /**
        HttpClient httpClient = null;
        HttpPut httpPut = null;

//
//                .addPart("bin", bin)
//                .addPart("comment", comment)
//                .build();
//				 FileBody bin = new FileBody(new File(args[0]));
//        StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
        HttpEntity httpEntity = null;
        List<NameValuePair> paramPair = null;
        if (httpOption.files != null) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            // post表单参数处理
            int length = (httpOption.params == null ? 0 : httpOption.params.size()) + (httpOption.files == null ? 0 : httpOption.files.size());

            int i = 0;
            boolean hasdata = false;

            if (httpOption.params != null) {
                Iterator<Entry<String, Object>> it = httpOption.params.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, Object> entry = it.next();
                    if(entry.getValue() == null){
                        continue;
                    }
                    if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String)
                        multipartEntityBuilder.addTextBody(entry.getKey(), String.valueOf(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
                    else {
                        multipartEntityBuilder.addTextBody(entry.getKey(), SimpleStringUtil.object2json(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
                    }
                    hasdata = true;
                }
            }
            if (httpOption.files != null) {
                Iterator<Entry<String, File>> it = httpOption.files.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, File> entry = it.next();

//						parts[i++] = new FilePart(entry.getKey(), entry.getValue());
                    File f = new File(String.valueOf(entry.getValue()));
                    if (f.exists()) {
                        FileBody file = new FileBody(f);
                        multipartEntityBuilder.addPart(entry.getKey(), file);
                        hasdata = true;
                    } else {

                    }

                    // System.out.println("post_key_file==> "+file);
                }
            }
            if (hasdata)
                httpEntity = multipartEntityBuilder.build();
        } else if (httpOption.params != null && httpOption.params.size() > 0) {
            paramPair = new ArrayList<NameValuePair>();
            Iterator<Entry<String, Object>> it = httpOption.params.entrySet().iterator();
            NameValuePair paramPair_ = null;
            for (int i = 0; it.hasNext(); i++) {
                Entry<String, Object> entry = it.next();
                if(entry.getValue() == null){
                    continue;
                }
                if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String) {
                    paramPair_ = new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()));
                }
                else{
                    paramPair_ = new BasicNameValuePair(entry.getKey(), SimpleStringUtil.object2json(entry.getValue()));
                }
                paramPair.add(paramPair_);
            }
        }

        T responseBody = null;
//        int time = 0;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
//        int RETRY_TIME = config.getRetryTime();
//        do {
        String endpoint = null;
        Throwable e = null;
        int triesCount = 0;
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            endpoint = url;
            HttpAddress httpAddress = null;
			HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts );
            do {

                try {

                    httpAddress = httpServiceHosts.getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if(logger.isDebugEnabled()){
                        logger.debug("Put call {}",url);
                    }
                    httpClient = HttpRequestUtil.getHttpClient(config);
                    httpPut = HttpRequestUtil.getHttpPut(config, url, httpOption.cookie, httpOption.userAgent, httpOption.headers);


                    if (httpEntity != null) {
                        httpPut.setEntity(httpEntity);
                    } else if (paramPair != null && paramPair.size() > 0) {
                        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramPair, Consts.UTF_8);

                        httpPut.setEntity(entity);

                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpPut, responseHandler);
                    httpAddress.recover();
                    e = getException(  responseHandler,httpServiceHosts );
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException( poolname,url,config,ex);
                    break;
                }
                catch (ConnectTimeoutException connectTimeoutException){
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                }

                catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(poolname,url,config, ex);
                    break;
                }
                catch (NoHttpServerException ex){
                    e = ex;

                    break;
                }
                catch (ClientProtocolException ex){
                    httpAddress.setStatus(1);
                    e = ex;
                    if(logger.isErrorEnabled())
                        logger.error(new StringBuilder().append("Put request[").append(url)
                                .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
//					throw new HttpProxyRequestException(new StringBuilder().append("Put request[").append(url)
//                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                }

                catch (Exception ex) {
                    e = ex;
                    break;
                }
                catch (Throwable ex) {
                    e = ex;
                    break;
                } finally {
                    // 释放连接
                    if (httpPut != null)
                        httpPut.releaseConnection();
                    httpClient = null;
                }
//        } while (time < RETRY_TIME);
            } while (true);
        }
        else
        {
            try {
                if(logger.isDebugEnabled()){
                    logger.debug("Put call {}",url);
                }
                httpClient = HttpRequestUtil.getHttpClient(config);
                httpPut = HttpRequestUtil.getHttpPut(config, url, httpOption.cookie, httpOption.userAgent, httpOption.headers);
                if (httpEntity != null) {
                    httpPut.setEntity(httpEntity);
                } else if (paramPair != null && paramPair.size() > 0) {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramPair, Consts.UTF_8);

                    httpPut.setEntity(entity);

                }
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpPut, responseHandler);
            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpPut != null)
                    httpPut.releaseConnection();
                httpClient = null;
            }
        }
        if (e != null){
            if(e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException)e;
            throw new HttpProxyRequestException("Put request Url:"+url,e);
        }
        return responseBody;
         */

    }

    /**
     * 公用post方法
     *
     * @param poolname
     * @param url
     * @param cookie
     * @param userAgent
     * @param params
     * @param files
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static String httpPostFileforString(String poolname, String url, String cookie, String userAgent, Map<String, Object> params,
                                               Map<String, File> files, Map<String, String> headers) throws HttpProxyRequestException {

    	return httpPost(  poolname,   url,   cookie,   userAgent,   params,
                  files,  headers,new StringResponseHandler() );


    }




    /**
     * 公用delete方法
     *
     * @param poolname
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDelete(String poolname, String url) throws HttpProxyRequestException{
       return httpDelete(  poolname,   url, (String) null, (String) null, (Map<String, Object>) null,
               (Map<String, String>) null);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDelete( String url) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, (String) null, (String) null, (Map<String, Object>) null,
                (Map<String, String>) null);

    }
    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDeleteWithbody( String url,String requestBody) throws HttpProxyRequestException{
        return httpDelete(  "default",   url,requestBody, (String) null, (String) null, (Map<String, Object>) null,
                (Map<String, String>) null);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDelete( String url,Map<String, String> headers) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, (String) null, (String) null, (Map<String, Object>) null,
                headers);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDelete( String url,String requestBody,Map<String, String> headers) throws HttpProxyRequestException{
        return httpDelete(  "default",   url,  requestBody, (String) null, (String) null, (Map<String, Object>) null,
                headers);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDeleteWithbody( String url,String requestBody,Map<String, Object> params,Map<String, String> headers) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, requestBody, (String) null, (String) null, params,
                headers);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDelete( String url,Map<String, Object> params,Map<String, String> headers) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, (String) null, (String) null, params,
                headers);

    }



    public static <T> T httpDelete( String url,Map<String, Object> params,Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, (String)null,(String) null, (String) null, params,
                headers, responseHandler);

    }

    public static <T> T httpDeleteWithBody (String url,String requestBody,Map<String, Object> params,Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, requestBody,(String) null, (String) null, params,
                headers, responseHandler);

    }

    public static String httpDelete( String poolname,String url,Map<String, Object> params,Map<String, String> headers) throws HttpProxyRequestException{
        return httpDelete(  poolname,   url, (String) null, (String) null, params,
                headers);

    }

    public static String httpDelete ( String poolname,String url,String requestBody,Map<String, Object> params,Map<String, String> headers) throws HttpProxyRequestException{
        return httpDelete(  poolname,   url,  requestBody,(String)null, (String) null, params,
                headers);

    }

    public static <T> T httpDelete( String poolname,String url,Map<String, Object> params,Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
        return httpDelete(  poolname,   url,(String)null, (String) null, (String) null, params,
                headers,responseHandler);

    }

    public static <T> T httpDelete( String poolname,String url,String requestBody,Map<String, Object> params,Map<String, String> headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
        return httpDelete(  poolname,     url, requestBody,(String) null, (String) null, params,
                headers,responseHandler);

    }
    /**
     * 公用delete方法
     *
     * @param poolname
     * @param url
     * @param cookie
     * @param userAgent
     * @param params
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static String httpDelete(String poolname, String url, String cookie, String userAgent, Map<String, Object> params,
                                                Map<String, String> headers) throws HttpProxyRequestException {
    	return httpDelete(  poolname,   url, (String)null  ,cookie,   userAgent,   params,
                  headers,new StringResponseHandler());
    }
    /**
     * 公用delete方法
     *
     * @param poolname
     * @param url
     * @param cookie
     * @param userAgent
     * @param params
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static String httpDelete(String poolname,String url,String requestBody,  String cookie, String userAgent, Map<String, Object> params,
                                    Map<String, String> headers) throws HttpProxyRequestException {
        return httpDelete(  poolname,   url,  requestBody, cookie,   userAgent,   params,
                headers,new StringResponseHandler());
    }
    /**
     * 公用delete方法
     *
     * @param poolname
     * @param url
     * @param cookie
     * @param userAgent
     * @param params
     * @param headers
     * @throws HttpProxyRequestException
     */
    public static <T> T httpDelete(String poolname, String url, String requestBody, final String cookie,final  String userAgent, Map<String, Object> params,
                                   final Map<String, String> headers,final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        final HttpEntity httpEntity = requestBody == null?null:new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        HttpParams httpParams = null;
        if (params != null && params.size() > 0) {
            httpParams = new BasicHttpParams();
            Iterator<Entry<String, Object>> it = params.entrySet().iterator();
            for (int i = 0; it.hasNext(); i++) {
                Entry<String, Object> entry = it.next();
                httpParams.setParameter(entry.getKey(), entry.getValue());
            }
        }
        final HttpParams _httpParams = httpParams;
        return _handleRequest( poolname,  url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpDeleteWithBody httpDeleteWithBody = null;
                        HttpDelete httpDelete = null;
                        try {
                            Object responseBody = null;
                            if (httpEntity != null) {
                                httpDeleteWithBody = HttpRequestUtil.getHttpDeleteWithBody(config, url, cookie, userAgent, headers);
                                httpDeleteWithBody.setEntity(httpEntity);
                                if (_httpParams != null) {

                                    httpDeleteWithBody.setParams(_httpParams);
                                }

                                responseBody = httpClient.execute(httpDeleteWithBody, responseHandler);
                            } else {
                                httpDelete = HttpRequestUtil.getHttpDelete(config, url, cookie, userAgent, headers);
                                if (_httpParams != null) {
                                    httpDelete.setParams(_httpParams);
                                }

                                responseBody = httpClient.execute(httpDelete, responseHandler);
                            }
                            return responseBody;
                        }
                        finally {
                            // 释放连接
                            if (httpDelete != null)
                                try {
                                    httpDelete.releaseConnection();
                                }
                                catch (Exception e){

                                }
                            if (httpDeleteWithBody != null)
                                try {
                                    httpDeleteWithBody.releaseConnection();
                                }
                                catch (Exception e){

                                }

                        }
                    }
                } );
        /**
        HttpClient httpClient = null;
        HttpDeleteWithBody httpDeleteWithBody = null;
        HttpDelete httpDelete = null;


        T responseBody = null;
//        int time = 0;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
//        int RETRY_TIME = config.getRetryTime();
        HttpEntity httpEntity = requestBody == null?null:new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
//        do {
        String endpoint = null;
        Throwable e = null;
        int triesCount = 0;
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            endpoint = url;
            HttpAddress httpAddress = null;
			HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts );
            do {

                try {

                    httpAddress = httpServiceHosts.getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if(logger.isDebugEnabled()){
                        logger.debug("Delete call {}",url);
                    }
                    httpClient = HttpRequestUtil.getHttpClient(config);
                    HttpParams httpParams = null;
                    if (params != null && params.size() > 0) {
                        httpParams = new BasicHttpParams();
                        Iterator<Entry<String, Object>> it = params.entrySet().iterator();
                        NameValuePair paramPair_ = null;
                        for (int i = 0; it.hasNext(); i++) {
                            Entry<String, Object> entry = it.next();
                            httpParams.setParameter(entry.getKey(), entry.getValue());
                        }
                    }
                    if (httpEntity != null) {
                        httpDeleteWithBody = HttpRequestUtil.getHttpDeleteWithBody(config, url, cookie, userAgent, headers);
                        httpDeleteWithBody.setEntity(httpEntity);
                        if (httpParams != null) {

                            httpDeleteWithBody.setParams(httpParams);
                        }
                        if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                            ((URLResponseHandler)responseHandler).setUrl(url);
                        }
                        responseBody = httpClient.execute(httpDeleteWithBody, responseHandler);
                        httpAddress.recover();
                    } else {
                        httpDelete = HttpRequestUtil.getHttpDelete(config, url, cookie, userAgent, headers);
                        if (httpParams != null) {
                            httpDelete.setParams(httpParams);
                        }
                        if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                            ((URLResponseHandler)responseHandler).setUrl(url);
                        }
                        responseBody = httpClient.execute(httpDelete, responseHandler);
                        httpAddress.recover();
                    }

                    e = getException(  responseHandler,httpServiceHosts );
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException( poolname,url,config,ex);
                    break;
                }
                catch (ConnectTimeoutException connectTimeoutException){
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                }

                catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(poolname,url,config, ex);
                    break;
                }
                catch (NoHttpServerException ex){
                    e = ex;

                    break;
                }
                catch (ClientProtocolException ex){
                    httpAddress.setStatus(1);
                    e = ex;
                    if(logger.isErrorEnabled())
                        logger.error(new StringBuilder().append("Delete request[").append(url)
                                .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
//                    throw new HttpProxyRequestException(new StringBuilder().append("Delete Request[").append(url)
//                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                }

                catch (Exception ex) {
                    e = ex;
                    break;
                }
                catch (Throwable ex) {
                    e = ex;
                    break;
                } finally {
                    // 释放连接
                    if (httpDelete != null)
                        httpDelete.releaseConnection();
                    httpClient = null;
                }
            } while (true);
        }
        else{
            try {
                if(logger.isDebugEnabled()){
                    logger.debug("Delete call {}",url);
                }
                httpClient = HttpRequestUtil.getHttpClient(config);
                HttpParams httpParams = null;
                if (params != null && params.size() > 0) {
                    httpParams = new BasicHttpParams();
                    Iterator<Entry<String, Object>> it = params.entrySet().iterator();
                    NameValuePair paramPair_ = null;
                    for (int i = 0; it.hasNext(); i++) {
                        Entry<String, Object> entry = it.next();
                        httpParams.setParameter(entry.getKey(), entry.getValue());
                    }
                }
                if (httpEntity != null) {
                    httpDeleteWithBody = HttpRequestUtil.getHttpDeleteWithBody(config, url, cookie, userAgent, headers);
                    httpDeleteWithBody.setEntity(httpEntity);
                    if (httpParams != null) {

                        httpDeleteWithBody.setParams(httpParams);
                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpDeleteWithBody, responseHandler);
                } else {
                    httpDelete = HttpRequestUtil.getHttpDelete(config, url, cookie, userAgent, headers);
                    if (httpParams != null) {
                        httpDelete.setParams(httpParams);
                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpDelete, responseHandler);
                }


            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpDelete != null)
                    httpDelete.releaseConnection();
                httpClient = null;
            }
        }
        if (e != null){
            if(e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException)e;
            throw new HttpProxyRequestException("Delete request Url:"+ url,e);
        }
        return responseBody;
         */

    }


    public static String sendStringBody(String poolname,String requestBody, String url, Map<String, String> headers) throws HttpProxyRequestException {
        return  sendBody(poolname,  requestBody,   url,   headers,ContentType.create(
                "text/plain", Consts.UTF_8));
    }

    public static String sendJsonBody(String poolname,String requestBody, String url) throws HttpProxyRequestException {

        return  sendBody(   poolname, requestBody,   url,   null,ContentType.APPLICATION_JSON);
    }

    public static <T> T sendJsonBody(String poolname,String requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBody(   poolname, requestBody,   url,   null,ContentType.APPLICATION_JSON,resultType);
    }
    public static <T> List<T> sendJsonBodyForList(String poolname,String requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForList(   poolname, requestBody,   url,   null,ContentType.APPLICATION_JSON,resultType);
    }

    public static <T> Set<T> sendJsonBodyForSet(String poolname,String requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForSet(   poolname, requestBody,   url,   null,ContentType.APPLICATION_JSON,resultType);
    }

    public static <K,T> Map<K,T> sendJsonBodyForMap(String poolname,String requestBody, String url,Class<K> keyType,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForMap(   poolname, requestBody,   url,   null,ContentType.APPLICATION_JSON, keyType,resultType);
    }
    public static String sendJsonBody(String poolname,Object requestBody, String url) throws HttpProxyRequestException {

        return  sendBody(   poolname, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON);
    }

    public static <T> T sendJsonBody(String poolname,Object requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBody(   poolname, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,  resultType);
    }

    public static <T> List<T> sendJsonBodyForList(String poolname,Object requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForList(   poolname, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,  resultType);
    }
	public static <T> List<T> sendJsonBodyForList(Object requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

		return  sendBodyForList(   (String) null, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,  resultType);
	}
    public static <T> Set<T> sendJsonBodyForSet(String poolname,Object requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForSet(   poolname, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,  resultType);
    }

    public static <T> Set<T> sendJsonBodyForSet(Object requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForSet(   (String)null, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,  resultType);
    }


    public static <K,T> Map<K,T> sendJsonBodyForMap(String poolname,Object requestBody, String url,Class<K> keyType,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForMap(   poolname, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON, keyType, resultType);
    }
    public static <K,T> Map<K,T> sendJsonBodyForMap(Object requestBody, String url,Class<K> keyType,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForMap(   (String)null, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON, keyType, resultType);
    }
    public static String sendStringBody(String poolname,String requestBody, String url) throws HttpProxyRequestException {
        return  sendBody(  poolname,  requestBody,   url,   null,ContentType.create(
                "text/plain", Consts.UTF_8));
    }


    public static String sendJsonBody(String poolname, Object requestBody, String url, Map<String, String> headers) throws HttpProxyRequestException {

        return  sendBody(  poolname, object2json(requestBody),   url,   headers,ContentType.APPLICATION_JSON);
    }
    public static String sendJsonBody(String poolname, String requestBody, String url, Map<String, String> headers) throws HttpProxyRequestException {

        return  sendBody(  poolname, requestBody,   url,   headers,ContentType.APPLICATION_JSON);
    }
    public static String sendStringBody(String requestBody, String url, Map<String, String> headers) throws HttpProxyRequestException {
        return  sendBody("default",  requestBody,   url,   headers,ContentType.create(
                "text/plain", Consts.UTF_8));
    }

    public static String sendJsonBody(String requestBody, String url) throws HttpProxyRequestException {

        return  sendBody( "default", requestBody,   url,   null,ContentType.APPLICATION_JSON);
    }
    public static <T> T sendJsonBody(String requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBody( "default", requestBody,   url,   null,ContentType.APPLICATION_JSON,  resultType);
    }
    public static String sendJsonBody( String url) throws HttpProxyRequestException {

        return  sendBody( "default", (String)null,   url,   null,ContentType.APPLICATION_JSON);
    }

    public static String sendJsonBodyWithPool(String poolName, String url) throws HttpProxyRequestException {

        return  sendBody( poolName, (String)null,   url,   null,ContentType.APPLICATION_JSON);
    }

    public static String sendJsonBody(Object requestBody, String url) throws HttpProxyRequestException {

        return  sendBody( "default", object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON);
    }

    public static <T> T sendJsonBody(Object requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBody( "default", object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,resultType);
    }

    public static <D,T> D sendJsonBody(Object requestBody, String url,Class<D> containType,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBody( "default", object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,containType,resultType);
    }


    public static String sendStringBody(String requestBody, String url) throws HttpProxyRequestException {
        return  sendBody("default",  requestBody,   url,   null,ContentType.create(
                "text/plain", Consts.UTF_8));
    }

    public static String sendStringBody(String requestBody, String url, String mimeType, Charset charSet) throws HttpProxyRequestException {
        return  sendBody("default",  requestBody,   url,   null,ContentType.create(
                mimeType, charSet));
    }

    public static String sendJsonBody(String requestBody, String url, Map<String, String> headers ) throws HttpProxyRequestException {

        return  sendBody( "default", requestBody,   url,   headers,ContentType.APPLICATION_JSON);
    }
    public static <T> T sendJsonBody(String requestBody, String url, Map<String, String> headers  ,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {

        return  sendBody( "default", requestBody,   url,   headers,ContentType.APPLICATION_JSON, responseHandler);
    }

    public static <T> T sendJsonBody(String poolname,String requestBody, String url, Map<String, String> headers  ,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {

        return  sendBody( poolname, requestBody,   url,   headers,ContentType.APPLICATION_JSON, responseHandler);
    }
    public static <T> T sendBody(final String poolname,  String requestBody, String url,
                                 final Map<String, String> headers, ContentType contentType,
                                 final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        final HttpEntity httpEntity = new StringEntity(
                requestBody,
                contentType);

        return _handleRequest( poolname, url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpPost httpPost = null;
                        try {
                            httpPost = HttpRequestUtil.getHttpPost(config, url, "", "", headers);
                            if (httpEntity != null) {
                                httpPost.setEntity(httpEntity);
                            }

                            Object responseBody = httpClient.execute(httpPost, responseHandler);
                            return responseBody;
                        }
                        finally {
                            // 释放连接
                            if (httpPost != null)
                                httpPost.releaseConnection();
                        }
                    }
                } );
        /**
        HttpClient httpClient = null;
        HttpPost httpPost = null;


        HttpEntity httpEntity = new StringEntity(
                requestBody,
                contentType);
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
//        int RETRY_TIME = config.getRetryTime();
        T responseBody = null;
        String endpoint = null;
        Throwable e = null;
        int triesCount = 0;
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            endpoint = url;
            HttpAddress httpAddress = null;
			HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts );
            do {

                try {

                    httpAddress = httpServiceHosts.getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if(logger.isDebugEnabled()){
                        logger.debug("sendBody call {}",url);
                    }
                    httpClient = HttpRequestUtil.getHttpClient(config);
                    httpPost = HttpRequestUtil.getHttpPost(config, url, "", "", headers);
                    if (httpEntity != null) {
                        httpPost.setEntity(httpEntity);
                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpPost, responseHandler);
                    httpAddress.recover();
                    e = getException(  responseHandler,httpServiceHosts );
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException(poolname,url, config,ex);
                    break;
                }
                catch (ConnectTimeoutException connectTimeoutException){
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                }

                catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(poolname,url,config, ex);
                    break;
                }
                catch (NoHttpServerException ex){
                    e = ex;

                    break;
                }
                catch (ClientProtocolException ex){
                    httpAddress.setStatus(1);
                    e = ex;
                    if(logger.isErrorEnabled())
                        logger.error(new StringBuilder().append("Post request[").append(url)
                                .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
//                    throw new HttpProxyRequestException(new StringBuilder().append("Post Request[").append(url)
//                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                }

                catch (Exception ex) {
                    e = ex;
                    break;
                }
                catch (Throwable ex) {
                    e = ex;
                    break;
                }  finally {
                    // 释放连接
                    if (httpPost != null)
                        httpPost.releaseConnection();
                    httpClient = null;
                }


            } while (true);
        }
        else{
            try {
                if(logger.isDebugEnabled()){
                    logger.debug("sendBody call {}",url);
                }
                httpClient = HttpRequestUtil.getHttpClient(config);
                httpPost = HttpRequestUtil.getHttpPost(config, url, "", "", headers);
                if (httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpPost, responseHandler);

            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpPost != null)
                    httpPost.releaseConnection();
                httpClient = null;
            }
        }
        if (e != null){
            if(e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException)e;
            throw new HttpProxyRequestException("Post request Url:"+ url,e);
        }
        return responseBody;
         */
    }

    public static ClientConfiguration stopHttpClient(String poolName){
        return ClientConfiguration.stopHttpClient(poolName);
    }

    public static void stopHttpClients(ResourceStartResult resourceStartResult){

        if(resourceStartResult != null) {
            Map<String,Object> reses = resourceStartResult.getResourceStartResult();
            if(reses != null && reses.size() > 0) {
                Iterator<Map.Entry<String,Object>> iterator = reses.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String,Object> entry = iterator.next();
                    try {
                        ClientConfiguration.stopHttpClient(entry.getKey());
                    }
                    catch (Exception e){
                        logger.warn("stop http pool "+ entry.getKey() + " failed:",e);
                    }
                }

            }
        }
    }

    /**
     * 公共处理请求方法
     * @param poolname
     * @param url
     * @param responseHandler
     * @param executeRequest
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    private static <T> T _handleRequest(String poolname, String url,

                                   ResponseHandler<T> responseHandler,ExecuteRequest executeRequest) throws HttpProxyRequestException {
        HttpClient httpClient = null;

        final ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);

//        int RETRY_TIME = config.getRetryTime();
        T responseBody = null;
        String endpoint = null;
        Throwable e = null;
        int triesCount = 0;
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            endpoint = url;
            HttpAddress httpAddress = null;
            HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts );
            do {

                try {

                    httpAddress = httpServiceHosts.getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if(logger.isDebugEnabled()){
                        logger.debug("Send request {}",url);
                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }

                    httpClient = HttpRequestUtil.getHttpClient(config);

                    responseBody = (T)executeRequest.execute( config,httpClient,url,triesCount);
                    httpClient = null;
//                    responseBody = httpClient.execute(httpPost, responseHandler);
                    httpAddress.recover();
                    e = getException(  responseHandler,httpServiceHosts );
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException(poolname,url, config,ex);
                    break;
                }
                catch (ConnectTimeoutException connectTimeoutException){
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                }

                catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(poolname,url,config, ex);
                    break;
                }
                catch (NoHttpServerException ex){
                    e = ex;

                    break;
                }
                catch (ClientProtocolException ex){
                    httpAddress.setStatus(1);
                    e = ex;
                    if(logger.isErrorEnabled())
                        logger.error(new StringBuilder().append("Send request[").append(url)
                                .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
//                    throw new HttpProxyRequestException(new StringBuilder().append("Post Request[").append(url)
//                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                }

                catch (Exception ex) {
                    e = ex;
                    break;
                }
                catch (Throwable ex) {
                    e = ex;
                    break;
                }  finally {
                    // 释放连接

                    httpClient = null;
                }


            } while (true);
        }
        else{
            try {
                if(logger.isDebugEnabled()){
                    logger.debug("Send request {}",url);
                }
                httpClient = HttpRequestUtil.getHttpClient(config);
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = (T)executeRequest.execute( config,httpClient,url,triesCount);
//                httpPost = HttpRequestUtil.getHttpPost(config, url, "", "", headers);
//                if (httpEntity != null) {
//                    httpPost.setEntity(httpEntity);
//                }
//
//                responseBody = httpClient.execute(httpPost, responseHandler);

            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接

                httpClient = null;
            }
        }
        if (e != null){
            if(e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException)e;
            throw new HttpProxyRequestException("Send request Url:"+ url,e);
        }
        return responseBody;
    }
    private static HttpRequestBase getHttpEntityEnclosingRequestBase(String action,ClientConfiguration config, String url,  Map<String, String> headers){
        if(action.equals(HttpRequestUtil.HTTP_POST)){
            return HttpRequestUtil.getHttpPost(config, url, null, null, headers);
        }
        else if(action.equals(HttpRequestUtil.HTTP_GET)){
            return HttpRequestUtil.getHttpGet(config, url, null, null, headers);
        }
        else if(action.equals(HttpRequestUtil.HTTP_PUT)){
            return HttpRequestUtil.getHttpPut(config, url, null, null, headers);
        }
        else if(action.equals(HttpRequestUtil.HTTP_DELETE)){
            return HttpRequestUtil.getHttpDelete(config, url, null, null, headers);
        }
        else if(action.equals(HttpRequestUtil.HTTP_HEAD)){
            return HttpRequestUtil.getHttpHead(config, url, null, null, headers);
        }
        else if(action.equals(HttpRequestUtil.HTTP_TRACE)){
            return HttpRequestUtil.getHttpTrace(config, url, null, null, headers);
        }
        else if(action.equals(HttpRequestUtil.HTTP_OPTIONS)){
            return HttpRequestUtil.getHttpOptions(config, url, null, null, headers);
        }
        else if(action.equals(HttpRequestUtil.HTTP_PATCH)){
            return HttpRequestUtil.getHttpPatch(config, url, null, null, headers);
        }
        throw new IllegalArgumentException("not support http action:"+action);
    }
    private static HttpProxyRequestException handleSocketTimeoutException(String poolName,String url,ClientConfiguration configuration,SocketTimeoutException ex){
        if(configuration == null){
            StringBuilder builder = new StringBuilder();
            builder.append("Send request to url[").append(url).append("] socket Timeout with http pool[").append(poolName).append("].");
            return new HttpProxyRequestException(builder.toString(),ex);
        }
        else{
            StringBuilder builder = new StringBuilder();
            builder.append("Send request to url[").append(url).append("]  socket Timeout for ").append(configuration.getTimeoutSocket()).append("ms with http pool[").append(poolName).append("].");

            return new HttpProxyRequestException(builder.toString(),ex);
        }
    }

    private static HttpProxyRequestException handleConnectionPoolTimeOutException(String poolName,String url,ClientConfiguration configuration,ConnectionPoolTimeoutException ex){
        if(configuration == null){
            StringBuilder builder = new StringBuilder();
            builder.append("Send request to url[").append(url).append("] Wait timeout for idle http connection from http connection pool[").append(poolName).append("].");
            return new HttpProxyRequestException(builder.toString(),ex);
        }
        else{
            StringBuilder builder = new StringBuilder();
            builder.append("Send request to url[").append(url).append("] Wait timeout for ").append(configuration.getConnectionRequestTimeout()).append("ms for idle http connection from http connection pool[").append(poolName).append("].");

            return new HttpProxyRequestException(builder.toString(),ex);
        }
    }

    private static HttpProxyRequestException handleConnectionTimeOutException(String poolName,String url,ClientConfiguration configuration,ConnectTimeoutException ex){
        if(configuration == null){
            StringBuilder builder = new StringBuilder();
            builder.append("Send request to url[").append(url).append("] wait timeout for idle http connection from http connection pool[").append(poolName).append("].");
            return new HttpProxyRequestException(builder.toString(),ex);
        }
        else{
            StringBuilder builder = new StringBuilder();
            builder.append("Send request to url[").append(url).append("] wait timeout  for ").append(configuration.getTimeoutConnection()).append("ms from http connection pool[").append(poolName).append("].");

            return new HttpProxyRequestException(builder.toString(),ex);
        }
    }

    public static <T> T sendBody(String poolname,String requestBody, String url,final Map<String, String> headers,ContentType contentType,
                                 final ResponseHandler<T> responseHandler,final String action) throws HttpProxyRequestException {




        final HttpEntity httpEntity = requestBody != null?new StringEntity(
                requestBody,
                contentType):null;
        return _handleRequest( poolname,  url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpEntityEnclosingRequestBase httpPost = null;
                        try {
                            httpPost = (HttpEntityEnclosingRequestBase) getHttpEntityEnclosingRequestBase(action, config, url, headers);
                            if (httpEntity != null) {
                                httpPost.setEntity(httpEntity);
                            }

                            Object responseBody = httpClient.execute(httpPost, responseHandler);
                            return responseBody;
                        }
                        finally {
                            // 释放连接
                            if (httpPost != null)
                                httpPost.releaseConnection();
                        }
                    }
                } );
        /**
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
//        int RETRY_TIME = config.getRetryTime();
        T responseBody = null;
//        int time = 0;
//        do {
        String endpoint = null;
        Throwable e = null;
        int triesCount = 0;
        if(!url.startsWith("http://") && !url.startsWith("https://")){
            endpoint = url;
            HttpAddress httpAddress = null;
			HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts );
            do {

                try {

                    httpAddress = httpServiceHosts.getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(),endpoint);
                    if(logger.isDebugEnabled()){
                        logger.debug("sendBody call {}",url);
                    }
                    httpClient = HttpRequestUtil.getHttpClient(config);
                    httpPost = (HttpEntityEnclosingRequestBase) getHttpEntityEnclosingRequestBase(action, config, url, headers);
                    if (httpEntity != null) {
                        httpPost.setEntity(httpEntity);
                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpPost, responseHandler);
                    httpAddress.recover();
                    e = getException(  responseHandler,httpServiceHosts );
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException(poolname,url, config,ex);
                    break;
                }
                catch (ConnectTimeoutException connectTimeoutException){
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                }

                catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(poolname,url,config, ex);
                    break;
                }
                catch (NoHttpServerException ex){
                    e = ex;

                    break;
                }
                catch (ClientProtocolException ex){
                    httpAddress.setStatus(1);
                    e = ex;
                    if(logger.isErrorEnabled())
                        logger.error(new StringBuilder().append("Post request[").append(url)
                                .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
//                    throw new HttpProxyRequestException(new StringBuilder().append("Post Request[").append(url)
//                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                }

                catch (Exception ex) {
                    e = ex;
                    break;
                }
                catch (Throwable ex) {
                    e = ex;
                    break;
                } finally {
                    // 释放连接
                    if (httpPost != null)
                        httpPost.releaseConnection();
                    httpClient = null;
                }

//        } while (time < RETRY_TIME);
            }while(true);
        }
        else{
            try {

                if(logger.isDebugEnabled()){
                    logger.debug("sendBody call {}",url);
                }
                httpClient = HttpRequestUtil.getHttpClient(config);
                httpPost = (HttpEntityEnclosingRequestBase) getHttpEntityEnclosingRequestBase(action, config, url, headers);
                if (httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpPost, responseHandler);

            } catch (Exception ex) {
                 e = ex;
            } finally {
                // 释放连接
                if (httpPost != null)
                    httpPost.releaseConnection();
                httpClient = null;
            }
        }
        if (e != null){
            if(e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException)e;
            throw new HttpProxyRequestException("Post request Url:"+ url,e);
        }
        return responseBody;*/
    }

    public static String sendBody(String poolname,String requestBody, String url, Map<String, String> headers,ContentType contentType) throws HttpProxyRequestException {
    	return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<String>() {

            @Override
            public String handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleStringResponse(url, response);
            }

        });
        
    }

   
   
    public static <T> T sendBody(String poolname,String requestBody, String url, Map<String, String> headers,ContentType contentType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleResponse( url, response, resultType);
            }

        });

    }

    public static <D,T> D sendBody(String poolname,String requestBody, String url, Map<String, String> headers,ContentType contentType,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<D>() {

            @Override
            public D handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleResponse( url, response, containType,resultType);
            }

        });

    }
    public static <D,T> D httpPostForTypeObject(String url, Map<String, Object> params, final Class<D> containType, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostForTypeObject("default", url, params,  containType, resultType);
//        return httpPostforString(  poolName,url, params, (Map<String, String>) null);
    }
    public static <D,T> D httpPostForTypeObject(String poolName,String url, Map<String, Object> params, final Class<D> containType, final Class<T> resultType) throws HttpProxyRequestException {
        HttpOption httpOption = new HttpOption();

        httpOption.params = params;

        return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<D>() {
            @Override
            public D handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return ResponseUtil.handleResponse(url,response,containType,resultType);
            }
        });
//        return httpPostforString(  poolName,url, params, (Map<String, String>) null);
    }
    public static <T> List<T> sendBodyForList(String poolname,String requestBody, String url, Map<String, String> headers,ContentType contentType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<List<T>>() {

            @Override
            public List<T> handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleListResponse( url, response, resultType);
            }

        });

    }

    public static <T> Set<T> sendBodyForSet(String poolname,String requestBody, String url, Map<String, String> headers,ContentType contentType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<Set<T>>() {

            @Override
            public Set<T> handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleSetResponse( url, response, resultType);
            }

        });

    }

    public static <K,T> Map<K,T> sendBodyForMap(String poolname,String requestBody, String url, Map<String, String> headers,ContentType contentType,final Class<K> keyType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<Map<K,T> >() {

            @Override
            public Map<K,T>  handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                return ResponseUtil.handleMapResponse( url, response, keyType,resultType);
            }

        });

    }
    private static void assertCheck(HttpServiceHosts httpServiceHosts ){


        if(httpServiceHosts == null){
            if(logger.isWarnEnabled()){
                logger.warn("Http Request Proxy is not properly initialized, please refer to the document: https://esdoc.bbossgroups.com/#/httpproxy?id=_32-http负载均衡器配置和启动");
            }
            throw new HttpProxyRequestException("Http Request Proxy is not properly initialized, please refer to the document: https://esdoc.bbossgroups.com/#/httpproxy?id=_32-http负载均衡器配置和启动");
        }
    }
    public static <T> T putBody(String poolname,String requestBody, String url, final Map<String, String> headers,ContentType contentType,final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {



        final HttpEntity httpEntity = new StringEntity(
                requestBody,
                contentType);
        return _handleRequest( poolname,  url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpPut httpPost = null;
                        try {
                            httpPost = HttpRequestUtil.getHttpPut(config, url, "", "", headers);
                            if (httpEntity != null) {
                                httpPost.setEntity(httpEntity);
                            }

                            Object responseBody = httpClient.execute(httpPost, responseHandler);
                            return responseBody;
                        }
                        finally {
                            // 释放连接
                            if (httpPost != null)
                                httpPost.releaseConnection();
                        }
                    }
                } );
        /**
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
//        int RETRY_TIME = config.getRetryTime();
        T responseBody = null;
//        int time = 0;
//        do {
        String endpoint = null;
        Throwable e = null;
        int triesCount = 0;
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            endpoint = url;
            HttpAddress httpAddress = null;
			HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts );
            do {

                try {

                    httpAddress = httpServiceHosts.getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if(logger.isDebugEnabled()){
                        logger.debug("putBody call {}",url);
                    }
                    httpClient = HttpRequestUtil.getHttpClient(config);
                    httpPost = HttpRequestUtil.getHttpPut(config, url, "", "", headers);
                    if (httpEntity != null) {
                        httpPost.setEntity(httpEntity);
                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpPost, responseHandler);
                    httpAddress.recover();
                    e = getException(  responseHandler,httpServiceHosts );
                    break;
                } catch (HttpHostConnectException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoRouteToHostException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoHttpResponseException ex) {
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出

                    e = handleConnectionPoolTimeOutException( poolname,url,config,ex);
                    break;
                }
                catch (ConnectTimeoutException connectTimeoutException){
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                }

                catch (SocketTimeoutException ex) {
                    e = handleSocketTimeoutException(poolname,url,config, ex);
                    break;
                }
                catch (NoHttpServerException ex){
                    e = ex;

                    break;
                }
                catch (ClientProtocolException ex){
                    throw new HttpProxyRequestException(new StringBuilder().append("Put Request[").append(url)
                            .append("] handle failed: must use http/https protocol port such as 9200,do not use other transport such as 9300.").toString(),ex);
                }

                catch (Exception ex) {
                    e = ex;
                    break;
                }
                catch (Throwable ex) {
                    e = ex;
                    break;
                }  finally {
                    // 释放连接
                    if (httpPost != null)
                        httpPost.releaseConnection();
                    httpClient = null;
                }

            } while (true);
        }
        else{
            try {

                if(logger.isDebugEnabled()){
                    logger.debug("putBody call {}",url);
                }
                httpClient = HttpRequestUtil.getHttpClient(config);
                httpPost = HttpRequestUtil.getHttpPut(config, url, "", "", headers);
                if (httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpPost, responseHandler);
            } catch (Exception ex) {
                e = ex;
            } finally {
                // 释放连接
                if (httpPost != null)
                    httpPost.releaseConnection();
                httpClient = null;
            }
        }
        if (e != null){
            if(e instanceof HttpProxyRequestException)
                throw (HttpProxyRequestException)e;
            throw new HttpProxyRequestException("Put request url:"+url,e);
        }
        return responseBody;
         */
    }
    
    public static String putBody(String poolname,String requestBody, String url, Map<String, String> headers,ContentType contentType) throws HttpProxyRequestException {
    	return putBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<String>() {

            @Override
            public String handleResponse(final HttpResponse response)
                    throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();

                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    HttpEntity entity = response.getEntity();
                    if (entity != null ) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(new StringBuilder().append("PUT Request url:").append(url).append(",status:").append(status).toString());
                        }
                        throw new HttpProxyRequestException(new StringBuilder().append("Request url:").append(url).append(",error:").append(EntityUtils.toString(entity)).toString());
                    }
                    else
                        throw new HttpProxyRequestException(new StringBuilder().append("Put request url:").append(url).append(",Unexpected response status: " ).append( status).toString());
                }
            }

        });
        
    }
    
    public static <T> T putBody(String requestBody, String url, Map<String, String> headers,ContentType contentType, ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return putBody( "default", requestBody,   url,  headers,  contentType,  responseHandler) ;
    }
    
    public static String putBody(String requestBody, String url, Map<String, String> headers,ContentType contentType) throws HttpProxyRequestException {
    	return putBody( "default",requestBody,   url,   headers,  contentType) ;
        
    }
    
    



    
    public static <T> T putJson(String requestBody, String url, Map<String, String> headers, ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return putBody( "default", requestBody,   url,  headers,   ContentType.APPLICATION_JSON,  responseHandler) ;
    }

    public static <T> T putJson(String poolName,String requestBody, String url, Map<String, String> headers, ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return putBody( poolName, requestBody,   url,  headers,   ContentType.APPLICATION_JSON,  responseHandler) ;
    }


    public static String putJson(String requestBody, String url, Map<String, String> headers) throws HttpProxyRequestException {
    	return putBody( "default",requestBody,   url,   headers,  ContentType.APPLICATION_JSON) ;
        
    }

    public static String putJson(String poolName,String requestBody, String url, Map<String, String> headers) throws HttpProxyRequestException {
        return putBody(poolName,requestBody,   url,   headers,  ContentType.APPLICATION_JSON) ;

    }

    public static String putJson(String poolName,String requestBody, String url) throws HttpProxyRequestException {
        return putBody(poolName,requestBody,   url,   (Map<String, String> )null,  ContentType.APPLICATION_JSON) ;

    }

    private interface ExecuteRequest{
        Object execute(ClientConfiguration config,HttpClient httpClient ,String url, int triesCount) throws Exception;
    }


}
