/**
 *
 */
package org.frameworkset.spi.remote.http;

import com.frameworkset.util.SimpleStringUtil;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.frameworkset.spi.remote.http.proxy.HttpProxyRequestException;
import org.frameworkset.util.ResourceStartResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author yinbp
 * @Date:2016-11-20 11:39:59
 */
public class HttpRequestUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestProxy.class);
    public static final String HTTP_GET = "get";
    public static final String HTTP_POST = "post";
    public static final String HTTP_DELETE = "delete";
    public static final String HTTP_PUT = "put";
    public static final String HTTP_HEAD = "head";
    public static final String HTTP_TRACE = "trace";
    public static final String HTTP_OPTIONS = "options";
    public static final String HTTP_PATCH = "patch";
    public static final String UTF_8 = "UTF-8";
    // public static final String DESC = "descend";
    // public static final String ASC = "ascend";

//	private final static int TIMEOUT_CONNECTION = 20000;
//	private final static int TIMEOUT_SOCKET = 20000;
//	private final static int RETRY_TIME = 3;
    private static long retryInterval = -1;
    public static ResourceStartResult startHttpPools(String configFile){
        return ClientConfiguration.startHttpPools(configFile);
    }
    public static ResourceStartResult startHttpPoolsFromApollo(String namespaces){
        return ClientConfiguration.startHttpPoolsFromApollo(namespaces);
    }
    public static ResourceStartResult startHttpPoolsFromApolloAwaredChange(String namespaces){
        return ClientConfiguration.startHttpPoolsFromApolloAwaredChange(namespaces);
    }
    public static ResourceStartResult startHttpPoolsFromApollo(String namespaces,String configChangeListener){
        return ClientConfiguration.startHttpPoolsFromApollo(namespaces,configChangeListener);
    }

    public static ResourceStartResult startHttpPools(Map<String,Object>  configs){
        return ClientConfiguration.startHttpPools(  configs);
    }

    static HttpClient getHttpClient(ClientConfiguration config) throws Exception {
        return config.getHttpClient();
    }

    static String getCookie() {
        // if(appCookie == null || appCookie == "") {
        // appCookie = appContext.getProperty("cookie");
        // }
        // return appCookie;
        return null;
    }

    static String getUserAgent() {
        // if(appUserAgent == null || appUserAgent == "") {
        // StringBuilder ua = new StringBuilder("OSChina.NET");
        // ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App版本
        // ua.append("/Android");//手机系统平台
        // ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本
        // ua.append("/"+android.os.Build.MODEL); //手机型号
        // ua.append("/"+appContext.getAppId());//客户端唯一标识
        // appUserAgent = ua.toString();
        // }
        // return appUserAgent;
        return null;
    }

    static HttpGet getHttpGet(String url, String cookie, String userAgent, Map headers) {
        return getHttpGet(ClientConfiguration.getDefaultClientConfiguration(null), url, cookie, userAgent, headers);
    }

    static HttpGet getHttpGet(ClientConfiguration config, String url, String cookie, String userAgent, Map headers) {

        HttpGet httpget = new HttpGet(url);
        // Request configuration can be overridden at the request level.
        // They will take precedence over the one set at the client level.
        RequestConfig requestConfig = config.getRequestConfig();


        httpget.setConfig(requestConfig);
//        httpget.addHeader("Host", "www.bbossgroups.com");
        addHeaders(httpget,   headers,config.getKeepAlive());
        return httpget;
    }
    static HttpTrace getHttpTrace(ClientConfiguration config, String url, String cookie, String userAgent, Map headers) {

        HttpTrace httpget = new HttpTrace(url);
        // Request configuration can be overridden at the request level.
        // They will take precedence over the one set at the client level.
        RequestConfig requestConfig = config.getRequestConfig();


        httpget.setConfig(requestConfig);


        addHeaders(httpget,   headers,config.getKeepAlive());
        return httpget;
    }
    static void addHeaders(AbstractHttpMessage abstractHttpMessage, Map headers,long keepAlive){
        if(keepAlive  > 0L)
            abstractHttpMessage.addHeader("Connection", "Keep-Alive");
        if (headers != null && headers.size() > 0) {
            Iterator<Entry> entries = headers.entrySet().iterator();
            while (entries.hasNext()) {
                Entry entry = entries.next();
                abstractHttpMessage.addHeader(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
    }

    static HttpOptions getHttpOptions(ClientConfiguration config, String url, String cookie, String userAgent, Map headers) {

        HttpOptions httpget = new HttpOptions(url);
        // Request configuration can be overridden at the request level.
        // They will take precedence over the one set at the client level.
        RequestConfig requestConfig = config.getRequestConfig();


        httpget.setConfig(requestConfig);
//        httpget.addHeader("Host", "www.bbossgroups.com");
        addHeaders(httpget,   headers,config.getKeepAlive());
        return httpget;
    }

    static HttpPatch getHttpPatch(ClientConfiguration config, String url, String cookie, String userAgent, Map headers) {

        HttpPatch httpget = new HttpPatch(url);
        // Request configuration can be overridden at the request level.
        // They will take precedence over the one set at the client level.
        RequestConfig requestConfig = config.getRequestConfig();


        httpget.setConfig(requestConfig);
//        httpget.addHeader("Host", "www.bbossgroups.com");
        addHeaders(httpget,   headers,config.getKeepAlive());
        return httpget;
    }

    static HttpHead getHttpHead(ClientConfiguration config, String url, String cookie, String userAgent, Map headers) {

        HttpHead httpHead = new HttpHead(url);
        // Request configuration can be overridden at the request level.
        // They will take precedence over the one set at the client level.
        RequestConfig requestConfig = config.getRequestConfig();


        httpHead.setConfig(requestConfig);
//        httpget.addHeader("Host", "www.bbossgroups.com");
        addHeaders(httpHead,   headers,config.getKeepAlive());
        return httpHead;
    }

    static HttpPost getHttpPost(String url, String cookie, String userAgent) {
        return getHttpPost(ClientConfiguration.getDefaultClientConfiguration(), url, cookie, userAgent, null);
    }

    static HttpPost getHttpPost(ClientConfiguration config, String url, String cookie, String userAgent, Map headers) {
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig =   config.getRequestConfig();
        httpPost.setConfig(requestConfig);
//        httpPost.addHeader("Host", "www.bbossgroups.com");
        addHeaders(httpPost,   headers,config.getKeepAlive());


        return httpPost;
    }

    static HttpDelete getHttpDelete(ClientConfiguration config, String url, String cookie, String userAgent, Map headers) {
        HttpDelete httpDelete = new HttpDelete(url);
        RequestConfig requestConfig =   config.getRequestConfig();
        httpDelete.setConfig(requestConfig);
        addHeaders(httpDelete,   headers,config.getKeepAlive());


        return httpDelete;
    }

    static HttpDeleteWithBody getHttpDeleteWithBody(ClientConfiguration config, String url, String cookie, String userAgent, Map headers) {
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        RequestConfig requestConfig =   config.getRequestConfig();
        httpDelete.setConfig(requestConfig);
//        httpDelete.addHeader("Host", "www.bbossgroups.com");
        addHeaders(httpDelete,   headers,config.getKeepAlive());


        return httpDelete;
    }
    
    static HttpPut getHttpPut(ClientConfiguration config , String url, String cookie, String userAgent, Map headers) {
    	HttpPut httpPut = new HttpPut(url);
        RequestConfig requestConfig =   config .getRequestConfig();
        httpPut.setConfig(requestConfig);
//        httpDelete.addHeader("Host", "www.bbossgroups.com");
        addHeaders(httpPut,   headers,config.getKeepAlive());


        return httpPut;
    }

    public static String httpGetforString(String url) throws Exception {
        return httpGetforString(url, (String) null, (String) null, (Map) null);
    }

    public static String httpGetforString(String poolname, String url) throws Exception {
        return httpGetforString(poolname, url, (String) null, (String) null, (Map) null);
    }

    public static <T> T httpGet(String poolname, String url,ResponseHandler<T> responseHandler) throws Exception {
        return httpGetforString(poolname, url, (String) null, (String) null, (Map) null,responseHandler);
    }

    public static <T> T httpGet(String poolname, String url,Map headers,ResponseHandler<T> responseHandler) throws Exception {
        return httpGetforString(poolname, url, (String) null, (String) null, headers,responseHandler);
    }

    public static <T> T httpGet(String url,Map headers,ResponseHandler<T> responseHandler) throws Exception {
        return httpGetforString("default", url, (String) null, (String) null, headers,responseHandler);
    }

    public static String httpGetforString(String url, Map headers) throws Exception {
        return httpGetforString(url, (String) null, (String) null, headers);
    }

    public static String httpGetforString(String poolname, String url, Map headers) throws Exception {
        return httpGetforString(poolname, url, (String) null, (String) null, headers);
    }

    public static <T> T httpGetforString(String poolname, String url, Map headers,ResponseHandler<T> responseHandler) throws Exception {
        return httpGetforString(poolname, url, (String) null, (String) null, headers,responseHandler);
    }

    public static <T> T httpGetforString( String url, Map headers,ResponseHandler<T> responseHandler) throws Exception {
        return httpGetforString("default",  url, (String) null, (String) null, headers,responseHandler);
    }

    public static String httpGetforString(String url, String cookie, String userAgent, Map headers) throws Exception {
        return httpGetforString("default", url, cookie, userAgent, headers);
    }
    public static String httpGetforString(String poolname, String url, String cookie, String userAgent, Map headers) throws Exception{
        return  httpGetforString(poolname, url, cookie, userAgent, headers,new StringResponseHandler()) ;
    }
    /**
     * get请求URL
     *
     * @param url
     * @throws Exception
     */
    public static <T> T httpGetforString(String poolname, String url, String cookie, String userAgent, Map headers,ResponseHandler<T> responseHandler) throws Exception {
       return httpGet(  poolname,   url,   cookie,   userAgent,   headers, responseHandler);
    }

    /**
     * get请求URL
     *
     * @param url
     * @throws Exception
     */
    public static <T> T httpGet(String poolname, String url, String cookie, String userAgent, Map headers,ResponseHandler<T> responseHandler) throws Exception {

        HttpClient httpClient = null;
        HttpGet httpGet = null;

        T responseBody = null;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
            try {
                httpClient = getHttpClient(config);
                httpGet = getHttpGet(config, url, cookie, userAgent, headers);
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpGet, responseHandler);

            }
            catch (Exception e) {               
                throw   e;
            }finally {
                // 释放连接
                if(httpGet!=null)
                    httpGet.releaseConnection();
                httpClient = null;
            }

        return responseBody;

    }

    /**
     * head请求URL
     *
     * @param url
     * @throws Exception
     */
    public static <T> T httpHead(String poolname, String url,ResponseHandler<T> responseHandler) throws Exception {
        return httpHead(  poolname,   url,   null, null, (Map) null,responseHandler);

    }

    /**
     * get请求URL
     *
     * @param url
     * @throws Exception
     */
    public static <T> T httpHead(String url,ResponseHandler<T> responseHandler) throws Exception {
        return httpHead(  "default",   url,   null, null, (Map) null,responseHandler);

    }

    /**
     * head请求URL
     *
     * @param url
     * @throws Exception
     */
    public static <T> T httpHead(String poolname, String url,Map params,Map headers,ResponseHandler<T> responseHandler) throws Exception {
        return httpHead(  poolname,   url,   null, null,params, (Map) headers,responseHandler);

    }

    /**
     * get请求URL
     * ,Map params,Map headers,
     * @param url
     * @throws Exception
     */
    public static <T> T httpHead(String poolname, String url, String cookie, String userAgent, Map headers,ResponseHandler<T> responseHandler) throws Exception {
       return httpHead(  poolname,   url,   cookie,   userAgent,(Map )null, headers,responseHandler);

    }

    /**
     * get请求URL
     * ,Map params,Map headers,
     * @param url
     * @throws Exception
     */
    public static <T> T httpHead(String poolname, String url, String cookie, String userAgent,Map params, Map headers,ResponseHandler<T> responseHandler) throws Exception {

        HttpClient httpClient = null;
        HttpHead httpHead = null;

        T responseBody = null;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
            try {
                httpClient = getHttpClient(config);
                httpHead = getHttpHead(config, url, cookie, userAgent, headers);
                HttpParams httpParams = null;
                if(params != null && params.size() > 0) {
                    httpParams = new BasicHttpParams();
                    Iterator<Entry> it = params.entrySet().iterator();
                    NameValuePair paramPair_ = null;
                    for (int i = 0; it.hasNext(); i++) {
                        Entry entry = it.next();
                        httpParams.setParameter(String.valueOf(entry.getKey()), entry.getValue());
                    }
                    httpHead.setParams(httpParams);
                }

                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpHead, responseHandler);

            }
            catch (Exception e) {
                throw   e;
            } finally {
                // 释放连接
                if(httpHead!=null)
                    httpHead.releaseConnection();
                httpClient = null;
            }

        return responseBody;

    }


    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param files
     * @throws Exception
     */
    public static String httpPostFileforString(String url, Map params, Map<String, File> files)
            throws Exception {
        return httpPostFileforString("default", url, (String) null, (String) null, params, files);
    }

    public static String httpPostFileforString(String poolname, String url, Map params, Map<String, File> files)
            throws Exception {
        return httpPostFileforString(poolname, url, (String) null, (String) null, params, files);
    }

    public static String httpPostforString(String url, Map params) throws Exception {
        return httpPostforString(url, params, (Map) null);
    }
    
    public static <T> T httpPost(String url, Map params,ResponseHandler<T> responseHandler) throws Exception {
        return httpPostforString(url, params, (Map) null, responseHandler);
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws Exception
     */
    public static String httpPostforString(String url, Map params, Map headers) throws Exception {
        return httpPostFileforString("default", url, (String) null, (String) null, params, (Map<String, File>) null, headers);
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws Exception
     */
    public static String httpPostforString(String poolName,String url, Map params, Map headers) throws Exception {
        return httpPostFileforString(poolName, url, (String) null, (String) null, params, (Map<String, File>) null, headers);
    }
    
   
    
    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws Exception
     */
    public static  <T> T  httpPost(String url, Map params, Map headers,ResponseHandler<T> responseHandler) throws Exception {
        return httpPost("default", url, (String) null, (String) null, params, (Map<String, File>) null, headers, responseHandler);
    }
    
    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws Exception
     */
    public static <T> T httpPostforString(String url, Map params, Map headers,ResponseHandler<T> responseHandler) throws Exception {
        return httpPost("default", url, (String) null, (String) null, params, (Map<String, File>) null, headers,responseHandler);
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param headers
     * @throws Exception
     */
    public static <T> T httpPostforString(String poolName,String url, Map params, Map headers,ResponseHandler<T> responseHandler) throws Exception {
        return httpPost(poolName, url, (String) null, (String) null, params, (Map<String, File>) null, headers,responseHandler);
    }
    
    public static String httpPostforString(String poolname, String url, Map params) throws Exception {
        return httpPostFileforString(poolname, url, (String) null, (String) null, params, (Map<String, File>) null);
    }

    public static String httpPostforString(String url) throws Exception {
        return httpPostforString("default", url);
    }
    
    public static <T> T  httpPost(String url,ResponseHandler<T> responseHandler) throws Exception {
        return httpPost("default", url, responseHandler);
    }

    /**
     * 公用post方法
     *
     * @param poolname
     * @param url
     * @throws Exception
     */
    public static String httpPostforString(String poolname, String url) throws Exception {
        return httpPostFileforString(poolname, url, (String) null, (String) null, (Map) null,
                (Map<String, File>) null);
    }
    
    /**
     * 公用post方法
     *
     * @param poolname
     * @param url
     * @throws Exception
     */
    public static <T> T  httpPost(String poolname, String url,ResponseHandler<T> responseHandler) throws Exception {
    	return httpPost(  poolname,   url, (String) null, (String) null, (Map) null,
    			 (Map<String, File>) null, (Map)null,responseHandler) ;
        
    }

    public static String httpPostforString(String url, String cookie, String userAgent,
                                           Map<String, File> files) throws Exception {
        return httpPostforString("default", url, cookie, userAgent,
                files);
    }

    public static String httpPostforString(String poolname, String url, String cookie, String userAgent,
                                           Map<String, File> files) throws Exception {
        return httpPostFileforString(poolname, url, cookie, userAgent, null,
                files);
    }

    public static String httpPostforString(String url, String cookie, String userAgent, Map params,
                                           Map<String, File> files) throws Exception {
        return httpPostFileforString("default", url, cookie, userAgent, params,
                files);
    }

    public static String httpPostFileforString(String poolname, String url, String cookie, String userAgent, Map params,
                                               Map<String, File> files) throws Exception {
        return httpPostFileforString(poolname, url, cookie, userAgent, params,
                files, null);
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
     * @throws Exception
     */
    public static <T> T httpPost(String poolname, String url, String cookie, String userAgent, Map params,
                                               Map<String, File> files, Map headers,ResponseHandler<T> responseHandler) throws Exception {


        HttpClient httpClient = null;
        HttpPost httpPost = null;


        HttpEntity httpEntity = null;
        List<NameValuePair> paramPair = null;
        if (files != null) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            // post表单参数处理
            int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());

            int i = 0;
            boolean hasdata = false;

            if (params != null) {
                Iterator<Entry> it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Entry entry = it.next();
                    multipartEntityBuilder.addTextBody(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
                    hasdata = true;
                }
            }
            if (files != null) {
                Iterator<Entry<String, File>> it = files.entrySet().iterator();
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
        } else if (params != null && params.size() > 0) {
            paramPair = new ArrayList<NameValuePair>();
            Iterator<Entry> it = params.entrySet().iterator();
            NameValuePair paramPair_ = null;
            for (int i = 0; it.hasNext(); i++) {
                Entry entry = it.next();
                paramPair_ = new BasicNameValuePair(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                paramPair.add(paramPair_);
            }
        }

        T responseBody = null;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
            try {
                httpClient = getHttpClient(config);
                httpPost = getHttpPost(  config, url, cookie, userAgent, headers);


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

            }
            catch (Exception e) {               
                throw   e;
            } finally {
                // 释放连接
            	if(httpPost != null)
            		httpPost.releaseConnection();
                httpClient = null;
            }
        return responseBody;

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
     * @throws Exception
     */
    public static String httpPutforString(String poolname, String url, String cookie, String userAgent, Map params,
                                               Map<String, File> files, Map headers) throws Exception{
    	return httpPut(  poolname,   url,   cookie,   userAgent,  params,
                  files,   headers,new StringResponseHandler());
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws Exception
     */

    public static String httpPutforString(  String url,Map params, Map headers ) throws Exception{
    	return httpPut(  "default",   url,   (String)null,   (String)null,  params,
    			( Map<String, File> )null,   headers,new StringResponseHandler());
    }
    public static <T> T httpPutforString(  String url,Map params, Map headers ,ResponseHandler<T> responseHandler ) throws Exception{
        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
                ( Map<String, File> )null,   headers ,responseHandler);
    }

    public static String httpPutforString(String poolname,  String url,Map params, Map headers ) throws Exception{
        return httpPut(  poolname,   url,   (String)null,   (String)null,  params,
                ( Map<String, File> )null,   headers,new StringResponseHandler());
    }

    public static <T> T httpPutforString(String poolname,  String url,Map params, Map headers ,ResponseHandler<T> responseHandler) throws Exception{
        return httpPut(  poolname,   url,   (String)null,   (String)null,  params,
                ( Map<String, File> )null,   headers,responseHandler);
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
     * @throws Exception
     */
    public static <T> T httpPut(String url, String cookie, String userAgent, Map params,
                                               Map<String, File> files, Map headers,ResponseHandler<T> responseHandler) throws Exception {
    	return httpPut("default", url, cookie, userAgent, params,
                                                    files, headers, responseHandler) ;
    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @param responseHandler
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T httpPut(String url, Map params,  Map headers,ResponseHandler<T> responseHandler) throws Exception {
    	return httpPut( url, (String)null, (String)null, (Map)params,
    							(Map<String, File>)null, headers, responseHandler) ;
    }

    /**
     *
     * @param url
     * @param params
     * @param responseHandler
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T httpPut(String url, Map params,ResponseHandler<T> responseHandler) throws Exception {
    	return httpPut( url, (String)null, (String)null, (Map)params,
    							(Map<String, File>)null, (Map)null, responseHandler) ;
    }

    /**
     *
     * @param url
     * @param responseHandler
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T httpPut(String url,ResponseHandler<T> responseHandler) throws Exception {
    	return httpPut( url, (String)null, (String)null, (Map)null,
    							(Map<String, File>)null, (Map)null, responseHandler) ;
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
     * @throws Exception
     */
    public static <T> T httpPut(String poolname, String url, String cookie, String userAgent, Map params,
                                               Map<String, File> files, Map headers,ResponseHandler<T> responseHandler) throws Exception {

        HttpClient httpClient = null;
        HttpPut httpPut = null;

        HttpEntity httpEntity = null;
        List<NameValuePair> paramPair = null;
        if (files != null) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            // post表单参数处理
            int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());

            int i = 0;
            boolean hasdata = false;

            if (params != null) {
                Iterator<Entry> it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Entry entry = it.next();
                    multipartEntityBuilder.addTextBody(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
                    hasdata = true;
                }
            }
            if (files != null) {
                Iterator<Entry<String, File>> it = files.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, File> entry = it.next();

                    File f = new File(String.valueOf(entry.getValue()));
                    if (f.exists()) {
                        FileBody file = new FileBody(f);
                        multipartEntityBuilder.addPart(entry.getKey(), file);
                        hasdata = true;
                    } else {

                    }

                }
            }
            if (hasdata)
                httpEntity = multipartEntityBuilder.build();
        } else if (params != null && params.size() > 0) {
            paramPair = new ArrayList<NameValuePair>();
            Iterator<Entry> it = params.entrySet().iterator();
            NameValuePair paramPair_ = null;
            for (int i = 0; it.hasNext(); i++) {
                Entry entry = it.next();
                paramPair_ = new BasicNameValuePair(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                paramPair.add(paramPair_);
            }
        }

        T responseBody = null;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
            try {
                httpClient = getHttpClient(config);
                httpPut = getHttpPut(config, url, cookie, userAgent, headers);


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
            }
            catch (Exception e) {               
                throw   e;
            } finally {
                // 释放连接
            	if(httpPut != null)
            		httpPut.releaseConnection();
                httpClient = null;
            }
//        } while (time < RETRY_TIME);
        return responseBody;

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
     * @throws Exception
     */
    public static String httpPostFileforString(String poolname, String url, String cookie, String userAgent, Map params,
                                               Map<String, File> files, Map headers) throws Exception {
    	
    	return httpPost(  poolname,   url,   cookie,   userAgent,   params,
                  files,  headers,new StringResponseHandler() );
       

    }
    
    
    /**
     * 公用delete方法
     *
     * @param poolname
     * @param url

     * @throws Exception
     */
    public static String httpDelete(String poolname, String url) throws Exception{
       return httpDelete(  poolname,   url, (String) null, (String) null, (Map) null,
               (Map) null);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws Exception
     */
    public static String httpDelete( String url) throws Exception{
        return httpDelete(  "default",   url, (String) null, (String) null, (Map) null,
                (Map) null);

    }
    /**
     * 公用delete方法
     *
     * @param url

     * @throws Exception
     */
    public static String httpDeleteWithbody( String url,String requestBody) throws Exception{
        return httpDelete(  "default",   url,requestBody, (String) null, (String) null, (Map) null,
                (Map) null);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws Exception
     */
    public static String httpDelete( String url,Map headers) throws Exception{
        return httpDelete(  "default",   url, (String) null, (String) null, (Map) null,
                headers);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws Exception
     */
    public static String httpDelete( String url,String requestBody,Map headers) throws Exception{
        return httpDelete(  "default",   url,  requestBody, (String) null, (String) null, (Map) null,
                headers);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws Exception
     */
    public static String httpDeleteWithbody( String url,String requestBody,Map params,Map headers) throws Exception{
        return httpDelete(  "default",   url, requestBody, (String) null, (String) null, params,
                headers);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws Exception
     */
    public static String httpDelete( String url,Map params,Map headers) throws Exception{
        return httpDelete(  "default",   url, (String) null, (String) null, params,
                headers);

    }



    public static <T> T httpDelete( String url,Map params,Map headers,ResponseHandler<T> responseHandler) throws Exception{
        return httpDelete(  "default",   url, (String)null,(String) null, (String) null, params,
                headers, responseHandler);

    }

    public static <T> T httpDeleteWithBody (String url,String requestBody,Map params,Map headers,ResponseHandler<T> responseHandler) throws Exception{
        return httpDelete(  "default",   url, requestBody,(String) null, (String) null, params,
                headers, responseHandler);

    }

    public static String httpDelete( String poolname,String url,Map params,Map headers) throws Exception{
        return httpDelete(  poolname,   url, (String) null, (String) null, params,
                headers);

    }

    public static String httpDelete ( String poolname,String url,String requestBody,Map params,Map headers) throws Exception{
        return httpDelete(  poolname,   url,  requestBody,(String)null, (String) null, params,
                headers);

    }

    public static <T> T httpDelete( String poolname,String url,Map params,Map headers,ResponseHandler<T> responseHandler) throws Exception{
        return httpDelete(  poolname,   url,(String)null, (String) null, (String) null, params,
                headers,responseHandler);

    }

    public static <T> T httpDelete( String poolname,String url,String requestBody,Map params,Map headers,ResponseHandler<T> responseHandler) throws Exception{
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
     * @throws Exception
     */
    public static String httpDelete(String poolname, String url, String cookie, String userAgent, Map params,
                                                Map headers) throws Exception {
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
     * @throws Exception
     */
    public static String httpDelete(String poolname,String url,String requestBody,  String cookie, String userAgent, Map params,
                                    Map headers) throws Exception {
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
     * @throws Exception
     */
    public static <T> T httpDelete(String poolname, String url, String requestBody, String cookie, String userAgent, Map params,
                                                Map headers,ResponseHandler<T> responseHandler) throws Exception {


        HttpClient httpClient = null;
        HttpDeleteWithBody httpDeleteWithBody = null;
        HttpDelete httpDelete = null;


        T responseBody = null;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
        HttpEntity httpEntity = requestBody == null?null:new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
            try {
                httpClient = getHttpClient(config);
                HttpParams httpParams = null;
                if(params != null && params.size() > 0) {
                    httpParams = new BasicHttpParams();
                    Iterator<Entry> it = params.entrySet().iterator();
                    NameValuePair paramPair_ = null;
                    for (int i = 0; it.hasNext(); i++) {
                        Entry entry = it.next();
                        httpParams.setParameter(String.valueOf(entry.getKey()), entry.getValue());
                    }
                }
                if(httpEntity != null) {
                    httpDeleteWithBody = getHttpDeleteWithBody(config, url, cookie, userAgent, headers);
                    httpDeleteWithBody.setEntity(httpEntity);
                    if(httpParams != null) {

                        httpDeleteWithBody.setParams(httpParams);
                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpDeleteWithBody, responseHandler);
                }
                else {
                    httpDelete = getHttpDelete(config, url, cookie, userAgent, headers);
                    if(httpParams != null) {
                        httpDelete.setParams(httpParams);
                    }
                    if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                        ((URLResponseHandler)responseHandler).setUrl(url);
                    }
                    responseBody = httpClient.execute(httpDelete, responseHandler);
                }

            }
            catch (Exception e) {               
                throw   e;
            }  finally {
                // 释放连接
                if(httpDelete != null)
                	httpDelete.releaseConnection();
                httpClient = null;
            }
//        } while (time < RETRY_TIME);
        return responseBody;

    }
    
    
    public static String sendStringBody(String poolname,String requestBody, String url, Map headers) throws Exception {
        return  sendBody(poolname,  requestBody,   url,   headers,ContentType.create(
                "text/plain", Consts.UTF_8));
    }

    public static String sendJsonBody(String poolname,String requestBody, String url) throws Exception {

        return  sendBody(   poolname, requestBody,   url,   null,ContentType.APPLICATION_JSON);
    }
    public static String object2json(Object data){
        if(data == null){
            return null;
        }
        if(data instanceof String){
            return (String)data;
        }
        else{
            return SimpleStringUtil.object2json(data);
        }

    }
    public static String sendJsonBody(String poolname,Object requestBody, String url) throws Exception {

        return  sendBody(   poolname, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON);
    }

    public static String sendStringBody(String poolname,String requestBody, String url) throws Exception {
        return  sendBody(  poolname,  requestBody,   url,   null,ContentType.create(
                "text/plain", Consts.UTF_8));
    }


    public static String sendJsonBody(String poolname, Object requestBody, String url, Map headers) throws Exception {

        return  sendBody(  poolname, object2json(requestBody),   url,   headers,ContentType.APPLICATION_JSON);
    }
    public static String sendJsonBody(String poolname, String requestBody, String url, Map headers) throws Exception {

        return  sendBody(  poolname, requestBody,   url,   headers,ContentType.APPLICATION_JSON);
    }
    public static String sendStringBody(String requestBody, String url, Map headers) throws Exception {
        return  sendBody("default",  requestBody,   url,   headers,ContentType.create(
                "text/plain", Consts.UTF_8));
    }

    public static String sendJsonBody(String requestBody, String url) throws Exception {

        return  sendBody( "default", requestBody,   url,   null,ContentType.APPLICATION_JSON);
    }

    public static String sendJsonBody(Object requestBody, String url) throws Exception {

        return  sendBody( "default", object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON);
    }

    public static String sendStringBody(String requestBody, String url) throws Exception {
        return  sendBody("default",  requestBody,   url,   null,ContentType.create(
                "text/plain", Consts.UTF_8));
    }

    public static String sendStringBody(String requestBody, String url, String mimeType, Charset charSet) throws Exception {
        return  sendBody("default",  requestBody,   url,   null,ContentType.create(
                mimeType, charSet));
    }
   
    public static String sendJsonBody(String requestBody, String url, Map headers ) throws Exception {

        return  sendBody( "default", requestBody,   url,   headers,ContentType.APPLICATION_JSON);
    }
    public static <T> T sendJsonBody(String requestBody, String url, Map headers  ,ResponseHandler<T> responseHandler) throws Exception {

        return  sendBody( "default", requestBody,   url,   headers,ContentType.APPLICATION_JSON, responseHandler);
    }

    public static <T> T sendJsonBody(String poolname,String requestBody, String url, Map headers  ,ResponseHandler<T> responseHandler) throws Exception {

        return  sendBody( poolname, requestBody,   url,   headers,ContentType.APPLICATION_JSON, responseHandler);
    }
    public static <T> T sendBody(String poolname,String requestBody, String url, Map headers,ContentType contentType, ResponseHandler<T> responseHandler) throws Exception {
        HttpClient httpClient = null;
        HttpPost httpPost = null;


        HttpEntity httpEntity = new StringEntity(
                requestBody,
                contentType);
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
        T responseBody = null;
            try {
                httpClient = getHttpClient(config);
                httpPost = getHttpPost(config, url, "", "", headers);
                if (httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpPost,responseHandler);
            }
            catch (Exception e) {               
                throw   e;
            }  finally {
                // 释放连接
            	if(httpPost != null)
            		httpPost.releaseConnection();
                httpClient = null;
            }

        return responseBody;
    }
    private static HttpRequestBase getHttpEntityEnclosingRequestBase(String action,ClientConfiguration config, String url,  Map headers){
        if(action.equals(HTTP_POST)){
            return getHttpPost(config, url, null, null, headers);
        }
        else if(action.equals(HTTP_GET)){
            return getHttpGet(config, url, null, null, headers);
        }
        else if(action.equals(HTTP_PUT)){
            return getHttpPut(config, url, null, null, headers);
        }
        else if(action.equals(HTTP_DELETE)){
            return getHttpDelete(config, url, null, null, headers);
        }
        else if(action.equals(HTTP_HEAD)){
            return getHttpHead(config, url, null, null, headers);
        }
        else if(action.equals(HTTP_TRACE)){
            return getHttpTrace(config, url, null, null, headers);
        }
        else if(action.equals(HTTP_OPTIONS)){
            return getHttpOptions(config, url, null, null, headers);
        }
        else if(action.equals(HTTP_PATCH)){
            return getHttpPatch(config, url, null, null, headers);
        }
        throw new java.lang.IllegalArgumentException("not support http action:"+action);
    }
    public static <T> T sendBody(String poolname,String requestBody, String url, Map headers,ContentType contentType, ResponseHandler<T> responseHandler,String action) throws Exception {
        HttpClient httpClient = null;
        HttpEntityEnclosingRequestBase httpPost = null;


        HttpEntity httpEntity = requestBody != null?new StringEntity(
                requestBody,
                contentType):null;
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
        T responseBody = null;
            try {
                httpClient = getHttpClient(config);
                httpPost = (HttpEntityEnclosingRequestBase)getHttpEntityEnclosingRequestBase(action,config, url,headers);
                if (httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpPost,responseHandler);
            }
            catch (Exception e) {
                throw   e;
            }  finally {
                // 释放连接
                if(httpPost != null)
                    httpPost.releaseConnection();
                httpClient = null;
            }

        return responseBody;
    }
    
    public static String sendBody(String poolname,String requestBody, String url, Map headers,ContentType contentType) throws Exception {
    	return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<String>() {

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
                            logger.debug(new StringBuilder().append("SendBody Request url:").append(url).append(",status:").append(status).toString());
                        }
                        throw new HttpProxyRequestException(EntityUtils.toString(entity));
                    }
                    else
                        throw new HttpProxyRequestException(new StringBuilder().append("SendBody request url:").append(url)
                                        .append(",Unexpected response status: " ).append( status).toString());
                }
            }

        });
        
    }

    public static <T> T putBody(String poolname,String requestBody, String url, Map headers,ContentType contentType, ResponseHandler<T> responseHandler) throws Exception {
        HttpClient httpClient = null;
        HttpPut httpPost = null;


        HttpEntity httpEntity = new StringEntity(
                requestBody,
                contentType);
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);
        T responseBody = null;
            try {
                httpClient = getHttpClient(config);
                httpPost = getHttpPut(config, url, "", "", headers);
                if (httpEntity != null) {
                    httpPost.setEntity(httpEntity);
                }
                if(responseHandler != null && responseHandler instanceof URLResponseHandler){
                    ((URLResponseHandler)responseHandler).setUrl(url);
                }
                responseBody = httpClient.execute(httpPost,responseHandler);
            }
            catch (Exception e) {               
                throw   e;
            } finally {
                // 释放连接
            	if(httpPost != null)
            		httpPost.releaseConnection();
                httpClient = null;
            }

//        } while (time < RETRY_TIME);
        return responseBody;
    }
    
    public static String putBody(String poolname,String requestBody, String url, Map headers,ContentType contentType) throws Exception {
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
                            logger.debug(new StringBuilder().append("Put Body Request url:").append(url).append(",status:").append(status).toString());
                        }
                        throw new HttpProxyRequestException(EntityUtils.toString(entity));
                    }
                    else
                        throw new HttpProxyRequestException(new StringBuilder().append("PutBody request url:").append(url)
                                .append(",Unexpected response status: " ).append( status).toString());
                }
            }

        });
        
    }
    
    public static <T> T putBody(String requestBody, String url, Map headers,ContentType contentType, ResponseHandler<T> responseHandler) throws Exception {
        return putBody( "default", requestBody,   url,  headers,  contentType,  responseHandler) ;
    }
    
    public static String putBody(String requestBody, String url, Map headers,ContentType contentType) throws Exception {
    	return putBody( "default",requestBody,   url,   headers,  contentType) ;
        
    }
    
    

    public static <T> T putJson(String poolname,String requestBody, String url, Map headers,ContentType contentType, ResponseHandler<T> responseHandler) throws Exception {
       return putJson(  poolname,  requestBody,   url,   headers,ContentType.APPLICATION_JSON,  responseHandler);
    }
    
    public static String putJson(String poolname,String requestBody, String url, Map headers,ContentType contentType) throws Exception {
    	return putJson(  poolname,  requestBody,   url, headers, ContentType.APPLICATION_JSON);
        
    }
    
    public static <T> T putJson(String requestBody, String url, Map headers, ResponseHandler<T> responseHandler) throws Exception {
        return putBody( "default", requestBody,   url,  headers,   ContentType.APPLICATION_JSON,  responseHandler) ;
    }

    public static <T> T putJson(String poolName,String requestBody, String url, Map headers, ResponseHandler<T> responseHandler) throws Exception {
        return putBody( poolName, requestBody,   url,  headers,   ContentType.APPLICATION_JSON,  responseHandler) ;
    }


    public static String putJson(String requestBody, String url, Map headers) throws Exception {
    	return putBody( "default",requestBody,   url,   headers,  ContentType.APPLICATION_JSON) ;
        
    }

    public static String putJson(String poolName,String requestBody, String url, Map headers) throws Exception {
        return putBody(poolName,requestBody,   url,   headers,  ContentType.APPLICATION_JSON) ;

    }
    
    

}
