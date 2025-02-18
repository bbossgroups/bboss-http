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
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.frameworkset.spi.remote.http.callback.ExecuteIntercepter;
import org.frameworkset.spi.remote.http.kerberos.BaseRequestKerberosUrlUtils;
import org.frameworkset.spi.remote.http.kerberos.KerberosCallback;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static org.frameworkset.spi.remote.http.HttpRequestUtil.object2json;

/**
 * @author yinbp
 * @Date:2016-11-20 11:39:59
 */
public class HttpRequestProxy {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestProxy.class);
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


    public static ResourceStartResult startHttpPoolsFromNacos(String namespace, String serverAddr, String dataId, String group, long timeOut,Map<String,String> pros){
        return HttpRequestUtil.startHttpPoolsFromNacos(  namespace,   serverAddr,   dataId,   group,   timeOut,  pros);
    }
    public static ResourceStartResult startHttpPoolsFromNacos(String namespace, String serverAddr, String dataId, String group,
                                                              long timeOut,String configChangeListener,Map<String,String> pros){
        return HttpRequestUtil.startHttpPoolsFromNacos( namespace,   serverAddr,   dataId,   group,
                timeOut,  configChangeListener,  pros);
    }

    public static ResourceStartResult startHttpPoolsFromNacosAwaredChange(String namespace, String serverAddr, String dataId, String group, long timeOut,
                                                                          Map<String,String> pros){
        return HttpRequestUtil.startHttpPoolsFromNacosAwaredChange( namespace,   serverAddr,   dataId,   group,   timeOut,  pros);
    }


    public static String httpGetforString(String url) throws HttpProxyRequestException {
        return httpGetforString(url, (String) null, (String) null, (Map) null);
    }
    public static <T> T httpGetforObject(String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString("default",url, (String) null, (String) null, (Map) null, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse(url,  response, resultType);
            }

        });
    }
    public static String httpGetforString(String poolname, String url) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, (Map) null);
    }

    public static String httpGetforString(ClientConfiguration clientConfiguration, String url) throws HttpProxyRequestException {
        return httpGetforString(clientConfiguration, url, (String) null, (String) null, (Map) null);
    }

    public static <T> T httpGetforObject(String poolname, String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, (Map) null, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse(  url,response, resultType);
            }

        });
    }

    public static <T> T httpGetforObjectWithParams(String poolname, String url, Object params,Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null,params,(Map)null, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse(  url,response, resultType);
            }

        });
    }

    public static <T> T httpGetforObjectWithParams( String url, Object params,Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString((String)null, url, (String) null, (String) null,params,(Map)null, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse(  url,response, resultType);
            }

        });
    }

    public static <T> T httpGetforObjectWithParamsHeaders(String poolname, String url, Object params,Map headers,Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null,params,headers, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse(  url,response, resultType);
            }

        });
    }

    public static <T> T httpGetforObjectWithParamsHeaders( String url, Object params,Map headers,Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString((String)null, url, (String) null, (String) null,params,headers, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse(  url,response, resultType);
            }

        });
    }

    public static <T> T httpGetforObject(String poolname, String url,Map<String,String> headers,final Class<T> resultType) throws HttpProxyRequestException {
		return httpGetforString(poolname, url, (String) null, (String) null, headers, new BaseURLResponseHandler<T>() {

			@Override
			public T handleResponse(final HttpResponse response)
					throws IOException {
				return ResponseUtil.handleResponse(  url,response, resultType);
			}

		});
	}
    public static <T> List<T> httpGetforList(String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString("default",url, (String) null, (String) null, (Map) null, new BaseURLResponseHandler<List<T>>() {

            @Override
            public List<T> handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleListResponse(  url,response, resultType);
            }

        });
    }

    public static <K,T> Map<K,T> httpGetforMap(String url,final Class<K> keyType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString("default",url, (String) null, (String) null, (Map) null, new BaseURLResponseHandler<Map<K,T> >() {

            @Override
            public Map<K,T>  handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleMapResponse(  url,response,keyType, resultType);
            }

        });
    }

    public static <T> Set<T> httpGetforSet(String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString("default",url, (String) null, (String) null, (Map) null, new BaseURLResponseHandler<Set<T>>() {

            @Override
            public Set<T> handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleSetResponse(url,  response, resultType);
            }

        });
    }

    public static <T> List<T> httpGetforList(String poolName,String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, (Map) null, new BaseURLResponseHandler<List<T>>() {

            @Override
            public List<T> handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleListResponse( url, response, resultType);
            }

        });
    }
    public static <T> List<T> httpGetforList(String poolName,String url,Object params,Map headers,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null,params, headers, new BaseURLResponseHandler<List<T>>() {

            @Override
            public List<T> handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleListResponse( url, response, resultType);
            }

        });
    }

    public static <T> List<T> httpGetforList(String poolName,String url,Object params,Map headers,final Class<T> resultType,BaseURLResponseHandler<List<T>> baseURLResponseHandler) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, params,headers,baseURLResponseHandler);
    }
	public static <T> List<T> httpGetforList(String poolName,String url,Map headers,final Class<T> resultType,BaseURLResponseHandler<List<T>> baseURLResponseHandler) throws HttpProxyRequestException {
		return httpGetforString(  poolName,url, (String) null, (String) null, headers, baseURLResponseHandler);
	}
    public static <T> List<T> httpGetforList(String poolName,String url,Map headers,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, headers, new BaseURLResponseHandler<List<T>>() {

            @Override
            public List<T> handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleListResponse( url, response, resultType);
            }

        });
    }

    public static <D,T> D httpGetforTypeObject(String url,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforTypeObject("default", url, containType, resultType);
    }
    public static <D,T> D httpGetforTypeObject(String poolName,String url,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, (Map) null, new BaseURLResponseHandler<D>() {

            @Override
            public D handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse( url, response,containType, resultType);
            }

        });
    }


    public static <D,T> D httpGetforTypeObjectWithParams(String url,Object params,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforTypeObject("default", url, params,containType, resultType);
    }
    public static <D,T> D httpGetforTypeObject(String poolName,String url,Object params,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, params,(Map) null, new BaseURLResponseHandler<D>() {

            @Override
            public D handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse( url, response,containType, resultType);
            }

        });
    }

    public static <D,T> D httpGetforTypeObjectWithHeader(String url,Object params,Map headers,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforTypeObject("default", url, params,headers,containType, resultType);
    }
    public static <D,T> D httpGetforTypeObject(String poolName,String url,Object params,Map headers,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, params,headers, new BaseURLResponseHandler<D>() {

            @Override
            public D handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse( url, response,containType, resultType);
            }

        });
    }


    public static <K,T> Map<K,T> httpGetforMap(String poolName,String url,final Class<K> keyType,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, (Map) null, new BaseURLResponseHandler<Map<K,T> >() {

            @Override
            public Map<K,T>  handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleMapResponse( url, response,keyType, resultType);
            }

        });
    }

	public static <K,T> Map<K,T> httpGetforMap(String poolName,String url,Map headers,final Class<K> keyType,final Class<T> resultType) throws HttpProxyRequestException {
		return httpGetforString(  poolName,url, (String) null, (String) null,  headers, new BaseURLResponseHandler<Map<K,T> >() {

			@Override
			public Map<K,T>  handleResponse(final HttpResponse response)
					throws IOException {
				return ResponseUtil.handleMapResponse( url, response,keyType, resultType);
			}

		});
	}


	public static <T> Set<T> httpGetforSet(String poolName,String url,Map headers,final Class<T> resultType) throws HttpProxyRequestException {
		return httpGetforString(  poolName,url, (String) null, (String) null, headers, new BaseURLResponseHandler<Set<T>>() {

			@Override
			public Set<T> handleResponse(final HttpResponse response)
					throws IOException {
				return ResponseUtil.handleSetResponse( url, response, resultType);
			}

		});
	}

    public static <T> Set<T> httpGetforSet(String poolName,String url,final Class<T> resultType) throws HttpProxyRequestException {
        return httpGetforString(  poolName,url, (String) null, (String) null, (Map) null, new BaseURLResponseHandler<Set<T>>() {

            @Override
            public Set<T> handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleSetResponse( url, response, resultType);
            }

        });
    }
    public static <T> T httpGet(String poolname, String url,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, (Map) null,responseHandler);
    }

    public static <T> T httpGet(String poolname, String url,Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, headers,responseHandler);
    }

    public static <T> T httpGet(String url,Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString("default", url, (String) null, (String) null, headers,responseHandler);
    }

    public static String httpGetforString(String url, Map headers) throws HttpProxyRequestException {
        return httpGetforString(url, (String) null, (String) null, headers);
    }

    public static String httpGetforString(String poolname, String url, Map headers) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, headers);
    }

    public static String httpGetforStringWithParams(String poolname, String url, Object params) throws HttpProxyRequestException {
//        return httpGetforString(poolname, url, (String) null, (String) null, (Map)null);
        return httpGet(  poolname,   url,   (String) null, (String) null,  params,(Map)null, new StringResponseHandler());
    }

    public static String httpGetforStringWithParams( String url, Object params) throws HttpProxyRequestException {
//        return httpGetforString(poolname, url, (String) null, (String) null, (Map)null);
        return httpGet(  (String)null,   url,   (String) null, (String) null,  params,(Map)null, new StringResponseHandler());
    }

    public static String httpGetforStringWithParamsHeaders(String poolname, String url, Object params,Map headers) throws HttpProxyRequestException {
//        return httpGetforString(poolname, url, (String) null, (String) null, (Map)null);
        return httpGet(  poolname,   url,   (String) null, (String) null,  params,headers, new StringResponseHandler());
    }

    public static String httpGetforStringWithParamsHeaders( String url, Object params,Map headers) throws HttpProxyRequestException {
//        return httpGetforString(poolname, url, (String) null, (String) null, (Map)null);
        return httpGet(  (String)null,   url,   (String) null, (String) null,  params,headers, new StringResponseHandler());
    }



    public static <T> T httpGetforString(String poolname, String url, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString(poolname, url, (String) null, (String) null, headers,responseHandler);
    }

    public static <T> T httpGetforString( String url, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGetforString("default",  url, (String) null, (String) null, headers,responseHandler);
    }

    public static String httpGetforString(String url, String cookie, String userAgent, Map headers) throws HttpProxyRequestException {
        return httpGetforString("default", url, cookie, userAgent, headers);
    }
    public static String httpGetforString(String poolname, String url, String cookie, String userAgent, Map headers) throws HttpProxyRequestException{
        return  httpGetforString(poolname, url, cookie, userAgent, headers,new StringResponseHandler()) ;
    }

    public static String httpGetforString(ClientConfiguration clientConfiguration, String url, String cookie, String userAgent, Map headers) throws HttpProxyRequestException{
        return  httpGetforString(clientConfiguration, url, cookie, userAgent, headers,new StringResponseHandler()) ;
    }
    /**
     * get请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpGetforString(String poolname, String url, String cookie, String userAgent, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
       return httpGet(  poolname,   url,   cookie,   userAgent,  (Map)null, headers, responseHandler);
    }

    public static <T> T httpGetforString(ClientConfiguration clientConfiguration, String url, String cookie, String userAgent, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGet(  clientConfiguration,   url,   cookie,   userAgent,  (Map)null, headers, responseHandler);
    }

    /**
     * get请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpGetforString(String poolname, String url, String cookie, String userAgent,Object params, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpGet(  poolname,   url,   cookie,   userAgent,  params, headers, responseHandler);
    }
   /**
     * get请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpGet(String poolname, String url, final String cookie,final  String userAgent,final  Object params,final  Map headers,final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        //拼接get请求参数
        url = HttpParamsHandler.appendParams(url,params);
        return _handleRequest( poolname,  url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpGet httpGet = null;
                        try {
                            httpGet = HttpRequestUtil.getHttpGet(config, url, cookie, userAgent, headers);

                            return httpClient.execute(httpGet, responseHandler);
                        }
                        finally {
                            // 释放连接
                            if (httpGet != null)
                                httpGet.releaseConnection();
                        }
                    }
                } );


    }

    /**
     * get请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpGet(ClientConfiguration clientConfiguration, String url, final String cookie,final  String userAgent,final  Object params,final  Map headers,final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        //拼接get请求参数
        url = HttpParamsHandler.appendParams(url,params);
        return _handleRequest( clientConfiguration,  url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpGet httpGet = null;
                        try {
                            httpGet = HttpRequestUtil.getHttpGet(config, url, cookie, userAgent, headers);

                            return httpClient.execute(httpGet, responseHandler);
                        }
                        finally {
                            // 释放连接
                            if (httpGet != null)
                                httpGet.releaseConnection();
                        }
                    }
                } );


    }

    /**
     * head请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String poolname, String url,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpHead(  poolname,   url,   null, null, (Map) null,responseHandler);

    }

    /**
     * get请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String url,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpHead(  "default",   url,   null, null, (Map) null,responseHandler);

    }

    /**
     * head请求URL
     *
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String poolname, String url,Object params,Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpHead(  poolname,   url,   null, null,params, (Map) headers,responseHandler);

    }

    /**
     * get请求URL
     * ,Object params,Map headers,
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String poolname, String url, String cookie, String userAgent, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
       return httpHead(  poolname,   url,   cookie,   userAgent,(Map )null, headers,responseHandler);

    }

    /**
     * get请求URL
     * ,Object params,Map headers,
     * @param url
     * @throws HttpProxyRequestException
     */
    public static <T> T httpHead(String poolname, String url, final String cookie, final String userAgent,final Object params, final Map headers,
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

                            if (params != null) {
                                HttpParams httpParams = HttpParamsHandler.httpParams( params);
//                                Iterator<Entry> it = params.entrySet().iterator();
//                                for (int i = 0; it.hasNext(); i++) {
//                                    Entry entry = it.next();
//                                    httpParams.setParameter(String.valueOf(entry.getKey()), entry.getValue());
//                                }
                                if(httpParams != null)
                                    httpHead.setParams(httpParams);
                            }

                            return httpClient.execute(httpHead, responseHandler);
                        }
                        finally {
                            // 释放连接
                            if (httpHead != null)
                                httpHead.releaseConnection();
                        }
                    }
                } );
      

    }


    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param files
     * @throws HttpProxyRequestException
     */
    public static String httpPostFileforString(String url, Object params, Map<String, File> files)
            throws HttpProxyRequestException {
        return httpPostFileforString("default", url, (String) null, (String) null, params, files);
    }

    public static String httpPostFileforString(String poolname, String url, Object params, Map<String, File> files)
            throws HttpProxyRequestException {
        return httpPostFileforString(poolname, url, (String) null, (String) null, params, files);
    }

    public static String httpPostforStringWithParams(String url, Object params) throws HttpProxyRequestException {
        return httpPostforString((String)null,url, params);
    }

    public static <T> T httpPostWithParams(String url, Object params,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map) null, responseHandler);
    }

    public static <T> T httpPostForObject(String url, Object params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map) null, new BaseURLResponseHandler<T>() {
            @Override
            public T handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleResponse(url,response,resultType);
            }
        });
    }

    public static <T> List<T> httpPostForListWithParams(String url, Object params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map) null, new BaseURLResponseHandler<List<T>>() {
            @Override
            public List<T>  handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
    }
    public static <T> Set<T> httpPostForSet(String url, Object params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map) null, new BaseURLResponseHandler<Set<T>>() {
            @Override
            public Set<T>  handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleSetResponse(url,response,resultType);
            }
        });
    }

    public static <K,T> Map<K,T> httpPostForMap(String url, Object params, final Class<K> keyType, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(url, params, (Map) null, new BaseURLResponseHandler<Map<K,T>>() {
            @Override
            public Map<K,T>  handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
            }
        });
    }

    public static <T> T httpPostForObject(String poolName,String url, Object params, final Class<T> resultType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;

		return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<T>() {
			@Override
			public T handleResponse(HttpResponse response) throws IOException {
				return ResponseUtil.handleResponse(url,response,resultType);
			}
		});
//        return httpPostforString(  poolName,url, params, (Map) null);
    }

	public static <T> T httpPostForObject(String poolName,String url, Object params, final Class<T> resultType,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;

		return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<T>() {
			@Override
			public T handleResponse(HttpResponse response) throws IOException {
				return ResponseUtil.handleResponse(url,response,resultType);
			}
		});
//        return httpPostforString(  poolName,url, params, (Map) null);
	}

	public static <T> List<T> httpPostForList(String poolName,String url, Object params, final Class<T> resultType,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;

		return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<List<T>>() {
			@Override
			public List<T>  handleResponse(HttpResponse response) throws IOException {
				return ResponseUtil.handleListResponse(url,response,resultType);
			}
		});

	}
	public static <T> Set<T> httpPostForSet(String poolName,String url, Object params, final Class<T> resultType,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;

		return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<Set<T>>() {
			@Override
			public Set<T>  handleResponse(HttpResponse response) throws IOException {
				return ResponseUtil.handleSetResponse(url,response,resultType);
			}
		});
//		return httpPostforString(  poolName,url, params, (Map) null, new ResponseHandler<Set<T>>() {
//			@Override
//			public Set<T>  handleResponse(HttpResponse response) throws IOException {
//				return ResponseUtil.handleSetResponse(response,resultType);
//			}
//		});
	}

	public static <K,T> Map<K,T> httpPostForMap(String poolName,String url, Object params, final Class<K> keyType, final Class<T> resultType,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;

		return httpPost(  poolName,   url,  httpOption,new BaseURLResponseHandler<Map<K,T>>() {
			@Override
			public Map<K,T>  handleResponse(HttpResponse response) throws IOException {
				return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
			}
		});
//    	return httpPostforString(poolName,url, params, (Map) null, new ResponseHandler<Map<K,T>>() {
//			@Override
//			public Map<K,T>  handleResponse(HttpResponse response) throws IOException {
//				return ResponseUtil.handleMapResponse(response,keyType,resultType);
//			}
//		});
	}

    public static <T> List<T> httpPostForList(String poolName,String url, Object params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(  poolName,url, params, (Map) null, new BaseURLResponseHandler<List<T>>() {
            @Override
            public List<T>  handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
    }
    public static <T> List<T> httpPostForList(String poolName,String url, Object params, Map headers, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(  poolName,url, params, headers, new BaseURLResponseHandler<List<T>>() {
            @Override
            public List<T>  handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
    }
    public static <T> List<T> httpPostForList(String poolName,String url, Object params, Map headers, final Class<T> resultType,BaseURLResponseHandler<List<T>> baseURLResponseHandler) throws HttpProxyRequestException {
        return httpPostforString(  poolName,url, params, headers, baseURLResponseHandler);
    }
    public static <T> List<T> httpPostForList(String poolName,String url , final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(  poolName,url, (Map) null, (Map) null, new BaseURLResponseHandler<List<T>>() {
            @Override
            public List<T>  handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
    }
    public static <T> List<T> httpPostForList(String url , final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(  (String)null,url, (Map) null, (Map) null, new BaseURLResponseHandler<List<T>>() {
            @Override
            public List<T>  handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
    }
    public static <T> Set<T> httpPostForSet(String poolName,String url, Object params, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(  poolName,url, params, (Map) null, new BaseURLResponseHandler<Set<T>>() {
            @Override
            public Set<T>  handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleSetResponse(url,response,resultType);
            }
        });
    }

    public static <K,T> Map<K,T> httpPostForMap(String poolName,String url, Object params, final Class<K> keyType, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostforString(poolName,url, params, (Map) null, new BaseURLResponseHandler<Map<K,T>>() {
            @Override
            public Map<K,T>  handleResponse(HttpResponse response) throws IOException {
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
    public static String httpPostforStringWithHeaders(String url, Object params, Map headers) throws HttpProxyRequestException {
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
    public static String httpPostforString(String poolName,String url, Object params, Map headers) throws HttpProxyRequestException {
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
    public static  <T> T  httpPost(String url, Object params, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
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
    public static <T> T httpPostforString(String url, Object params, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
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
    public static <T> T httpPostforString(String poolName,String url, Object params, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return httpPost(poolName, url, (String) null, (String) null, params, (Map<String, File>) null, headers,responseHandler);
    }

    public static String httpPostforString(String poolname, String url, Object params) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;

		return httpPost(  poolname,   url,  httpOption,new StringResponseHandler());
//        return httpPostFileforString(poolname, url, (String) null, (String) null, params, (Map<String, File>) null);
    }

	public static String httpPostforString(String poolname, String url, Object params,DataSerialType dataSerialType) throws HttpProxyRequestException {
		HttpOption httpOption = new HttpOption();

		httpOption.params = params;
		httpOption.dataSerialType = dataSerialType;
		return httpPost(  poolname,   url,  httpOption,new StringResponseHandler());
//        return httpPostFileforString(poolname, url, (String) null, (String) null, params, (Map<String, File>) null);
	}

    public static String httpPostforString(String url) throws HttpProxyRequestException {
        return httpPostforString("default", url,(Object)null);
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
        return httpPostFileforString(poolname, url, (String) null, (String) null, (Map) null,
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
    	return httpPost(  poolname,   url, (String) null, (String) null, (Map) null,
    			 (Map<String, File>) null, (Map)null,responseHandler) ;

    }

    public static String httpPostforStringWithFiles(String url, String cookie, String userAgent,
                                           Map<String, File> files) throws HttpProxyRequestException {
        return httpPostforString("default", url, cookie, userAgent,
                files);
    }

    public static String httpPostforString(String poolname, String url, String cookie, String userAgent,
                                           Map<String, File> files) throws HttpProxyRequestException {
        return httpPostFileforString(poolname, url, cookie, userAgent, null,
                files);
    }

    public static String httpPostforStringWithParams(String url, String cookie, String userAgent, Object params,
                                           Map<String, File> files) throws HttpProxyRequestException {
        return httpPostFileforString("default", url, cookie, userAgent, params,
                files);
    }

    public static String httpPostFileforString(String poolname, String url, String cookie, String userAgent, Object params,
                                               Map<String, File> files) throws HttpProxyRequestException {
        return httpPostFileforString(poolname, url, cookie, userAgent, params,
                files, null);
    }



	public static class HttpOption{
        public String cookie;
        public String userAgent;
        public Object params;//只能是map或者PO对象
        public Map<String, File> files;
        public Map headers;
        public DataSerialType dataSerialType = DataSerialType.TEXT;
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
//            int length = (httpOption.params == null ? 0 : httpOption.params.size()) + (httpOption.files == null ? 0 : httpOption.files.size());

//            int i = 0;
            boolean hasdata = HttpParamsHandler.paramsHandle( multipartEntityBuilder,httpOption);

//            if (httpOption.params != null) {
//                Iterator<Entry> it = httpOption.params.entrySet().iterator();
//                while (it.hasNext()) {
//                    Entry entry = it.next();
//                    if(entry.getValue() == null)
//                        continue;
//                    if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String) {
//                        multipartEntityBuilder.addTextBody(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                    }
//                    else{
//
//                        multipartEntityBuilder.addTextBody(String.valueOf(entry.getKey()), SimpleStringUtil.object2json(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                    }
//                    hasdata = true;
//                }
//            }
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
                    }

                    // System.out.println("post_key_file==> "+file);
                }
            }
            if (hasdata)
                httpEntity = multipartEntityBuilder.build();
        } else if (httpOption.params != null ) {
            paramPair = HttpParamsHandler.paramsPaires(  httpOption) ;
//            paramPair = new ArrayList<NameValuePair>();
//            Iterator<Entry> it = httpOption.params.entrySet().iterator();
//            NameValuePair paramPair_ = null;
//            for (int i = 0; it.hasNext(); i++) {
//                Entry entry = it.next();
//                if(entry.getValue() == null)
//                    continue;
//                if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String) {
//                    paramPair_ = new BasicNameValuePair(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
//                }
//                else{
//                    paramPair_ = new BasicNameValuePair(String.valueOf(entry.getKey()), SimpleStringUtil.object2json(entry.getValue()));
//                }
//                paramPair.add(paramPair_);
//            }
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

                            return httpClient.execute(httpPost, responseHandler);
                        }
                        finally {
                            // 释放连接
                            if (httpPost != null)
                                httpPost.releaseConnection();
                        }
                    }
                } );
       

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
    public static <T> T httpPost(String poolname, String url, String cookie, String userAgent, Object params,
                                               Map<String, File> files, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
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
    public static String httpPutforString(String poolname, String url, String cookie, String userAgent, Object params,
                                               Map<String, File> files, Map headers) throws HttpProxyRequestException{
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

    public static String httpPutforString(  String url,Object params, Map headers ) throws HttpProxyRequestException{
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

    public static <T> T httpPutforObject(String url, Object params, Map headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut(  "default",   url,   httpOption,  new BaseURLResponseHandler<T>(){

            @Override
            public T handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleResponse(url,response,resultType);
            }
        });
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<T>(){
//
//                    @Override
//                    public T handleResponse(HttpResponse response) throws IOException {
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

    public static <T> List<T> httpPutforList(String url, Object params, Map headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( "default",   url,   httpOption, new BaseURLResponseHandler<List<T>>(){

            @Override
            public List<T> handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<List<T>>(){
//
//                    @Override
//                    public List<T> handleResponse(HttpResponse response) throws IOException {
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

    public static <T> Set<T> httpPutforSet(String url, Object params, Map headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( "default",   url,   httpOption, new BaseURLResponseHandler<Set<T>>(){

            @Override
            public Set<T> handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleSetResponse(url,response,resultType);
            }
        });
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<Set<T>>(){
//
//                    @Override
//                    public Set<T> handleResponse(HttpResponse response) throws IOException {
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

    public static <K,T> Map<K,T> httpPutforObject(String url, Object params, Map headers, final Class<K> keyType , final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut(  "default",   url,   httpOption,  new BaseURLResponseHandler<Map<K,T>>(){

            @Override
            public Map<K,T> handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
            }
        });
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<Map<K,T>>(){
//
//                    @Override
//                    public Map<K,T> handleResponse(HttpResponse response) throws IOException {
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

    public static <T> T httpPutforObject(String poolName,String url, Object params, Map headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolName,   url,   httpOption, new BaseURLResponseHandler<T>(){

            @Override
            public T handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleResponse(url,response,resultType);
            }
        });
//        return httpPut(  poolName,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<T>(){
//
//                    @Override
//                    public T handleResponse(HttpResponse response) throws IOException {
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

    public static <T> List<T> httpPutforList(String poolName,String url, Object params, Map headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolName,   url,   httpOption, new BaseURLResponseHandler<List<T>>(){

            @Override
            public List<T> handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleListResponse(url,response,resultType);
            }
        });
//        return httpPut(  poolName,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<List<T>>(){
//
//                    @Override
//                    public List<T> handleResponse(HttpResponse response) throws IOException {
//                        return ResponseUtil.handleListResponse(response,resultType);
//                    }
//                });
    }

    public static <T> List<T> httpPutforList(String poolName,String url, Object params, Map headers, final Class<T> resultType ,BaseURLResponseHandler<List<T>> baseURLResponseHandler) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolName,   url,   httpOption, baseURLResponseHandler);

    }

    /**
     *
     * @param url
     * @param params
     * @param headers
     * @return
     * @throws HttpProxyRequestException
     */

    public static <T> Set<T> httpPutforSet(String poolName,String url, Object params, Map headers, final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolName,   url,   httpOption, new BaseURLResponseHandler<Set<T>>(){

            @Override
            public Set<T> handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleSetResponse(url,response,resultType);
            }
        });
//        return httpPut(  poolName,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<Set<T>>(){
//
//                    @Override
//                    public Set<T> handleResponse(HttpResponse response) throws IOException {
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

    public static <K,T> Map<K,T> httpPutforObject(String poolName,String url, Object params, Map headers, final Class<K> keyType , final Class<T> resultType ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolName,   url,   httpOption, new BaseURLResponseHandler<Map<K,T>>(){

            @Override
            public Map<K,T> handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleMapResponse(url,response,keyType,resultType);
            }
        });
//        return httpPut(   poolName,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new ResponseHandler<Map<K,T>>(){
//
//                    @Override
//                    public Map<K,T> handleResponse(HttpResponse response) throws IOException {
//                        return ResponseUtil.handleMapResponse(response,keyType,resultType);
//                    }
//                });
    }

    public static <T> T httpPutforString(  String url,Object params, Map headers ,ResponseHandler<T> responseHandler ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( "default",   url,   httpOption, responseHandler);
//        return httpPut(  "default",   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers ,responseHandler);
    }

    public static String httpPutforString(String poolname,  String url,Object params, Map headers ) throws HttpProxyRequestException{
        HttpOption httpOption = new HttpOption();
        httpOption.params = params;
        httpOption.headers = headers;
        return httpPut( poolname,   url,   httpOption,new StringResponseHandler());
//        return httpPut(  poolname,   url,   (String)null,   (String)null,  params,
//                ( Map<String, File> )null,   headers,new StringResponseHandler());
    }

    public static <T> T httpPutforString(String poolname,  String url,Object params, Map headers ,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
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
    public static <T> T httpPut(String url, String cookie, String userAgent, Object params,
                                               Map<String, File> files, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
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
    public static <T> T httpPut(String url, Object params,  Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
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
     * @throws HttpProxyRequestException
     */
    public static <T> T httpPut(String url, Object params,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
    	return httpPut( url, (String)null, (String)null, (Map)params,
    							(Map<String, File>)null, (Map)null, responseHandler) ;
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
    	return httpPut( url, (String)null, (String)null, (Map)null,
    							(Map<String, File>)null, (Map)null, responseHandler) ;
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
        return httpPut(url, (String) null, (String) null, (Map) null,
                (Map<String, File>) null, (Map) null, new BaseURLResponseHandler<T>() {
                    @Override
                    public T handleResponse(HttpResponse response) throws IOException {
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
        return httpPut(url, (String) null, (String) null, (Map) null,
                (Map<String, File>) null, (Map) null, new BaseURLResponseHandler<List<T>>() {
                    @Override
                    public List<T> handleResponse(HttpResponse response) throws IOException {
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
        return httpPut(url, (String) null, (String) null, (Map) null,
                (Map<String, File>) null, (Map) null, new BaseURLResponseHandler<Set<T>>() {
                    @Override
                    public Set<T> handleResponse(HttpResponse response) throws IOException {
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
        return httpPut(url, (String) null, (String) null, (Map) null,
                (Map<String, File>) null, (Map) null, new BaseURLResponseHandler<Map<K,T>>() {
                    @Override
                    public Map<K,T> handleResponse(HttpResponse response) throws IOException {
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
                    public T handleResponse(HttpResponse response) throws IOException {
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
                    public List<T> handleResponse(HttpResponse response) throws IOException {
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
                    public Set<T> handleResponse(HttpResponse response) throws IOException {
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
                    public Map<K,T> handleResponse(HttpResponse response) throws IOException {
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
    public static <T> T httpPut(String poolname, String url, String cookie, String userAgent, Object params,
                                               Map<String, File> files, Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
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
//            int length = (httpOption.params == null ? 0 : httpOption.params.size()) + (httpOption.files == null ? 0 : httpOption.files.size());
//
//            int i = 0;
            boolean hasdata = HttpParamsHandler.paramsHandle(multipartEntityBuilder,httpOption);

//            if (httpOption.params != null) {
//                Iterator<Entry> it = httpOption.params.entrySet().iterator();
//                while (it.hasNext()) {
//                    Entry entry = it.next();
//                    if(entry.getValue() == null){
//                        continue;
//                    }
//                    if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String)
//                        multipartEntityBuilder.addTextBody(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                    else {
//                        multipartEntityBuilder.addTextBody(String.valueOf(entry.getKey()), SimpleStringUtil.object2json(entry.getValue()), ClientConfiguration.TEXT_PLAIN_UTF_8);
//                    }
//                    hasdata = true;
//                }
//            }
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
                    }

                    // System.out.println("post_key_file==> "+file);
                }
            }
            if (hasdata)
                httpEntity = multipartEntityBuilder.build();
        } else if (httpOption.params != null ) {
            paramPair = HttpParamsHandler.paramsPaires(  httpOption) ;
//            paramPair = new ArrayList<NameValuePair>();
//            Iterator<Entry> it = httpOption.params.entrySet().iterator();
//            NameValuePair paramPair_ = null;
//            for (int i = 0; it.hasNext(); i++) {
//                Entry entry = it.next();
//                if(entry.getValue() == null){
//                    continue;
//                }
//                if(httpOption.dataSerialType != DataSerialType.JSON || entry.getValue() instanceof String) {
//                    paramPair_ = new BasicNameValuePair(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
//                }
//                else{
//                    paramPair_ = new BasicNameValuePair(String.valueOf(entry.getKey()), SimpleStringUtil.object2json(entry.getValue()));
//                }
//                paramPair.add(paramPair_);
//            }
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

                            return httpClient.execute(httpPut, responseHandler);
                        }
                        finally {
                            // 释放连接
                            if (httpPut != null)
                                httpPut.releaseConnection();
                        }
                    }
                } );
       

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
    public static String httpPostFileforString(String poolname, String url, String cookie, String userAgent, Object params,
                                               Map<String, File> files, Map headers) throws HttpProxyRequestException {

    	return httpPost(  poolname,   url,   cookie,   userAgent,   params,
                  files,  headers,new StringResponseHandler() );


    }

    public static String httpPostFileforString(String poolname, String url,  Object params,
                                               Map<String, File> files, Map headers) throws HttpProxyRequestException {

        return httpPost(  poolname,   url,   (String)null,   (String)null,   params,
                files,  headers,new StringResponseHandler() );


    }


    public static String httpPostFileforString(String url,  Object params,
                                               Map<String, File> files, Map headers) throws HttpProxyRequestException {

        return httpPost(  "default",   url,   (String)null,   (String)null,   params,
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
       return httpDelete(  poolname,   url, (String) null, (String) null, (Map) null,
               (Map) null);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDelete( String url) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, (String) null, (String) null, (Map) null,
                (Map) null);

    }
    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDeleteWithbody( String url,String requestBody) throws HttpProxyRequestException{
        return httpDelete(  "default",   url,requestBody, (String) null, (String) null, (Map) null,
                (Map) null);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDelete( String url,Map headers) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, (String) null, (String) null, (Map) null,
                headers);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDelete( String url,String requestBody,Map headers) throws HttpProxyRequestException{
        return httpDelete(  "default",   url,  requestBody, (String) null, (String) null, (Map) null,
                headers);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDeleteWithbody( String url,String requestBody,Object params,Map headers) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, requestBody, (String) null, (String) null, params,
                headers);

    }

    /**
     * 公用delete方法
     *
     * @param url

     * @throws HttpProxyRequestException
     */
    public static String httpDeleteWithParams( String url,Object params,Map headers) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, (String) null, (String) null, params,
                headers);

    }



    public static <T> T httpDelete( String url,Object params,Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, (String)null,(String) null, (String) null, params,
                headers, responseHandler);

    }

    public static <T> T httpDeleteWithBody (String url,String requestBody,Object params,Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
        return httpDelete(  "default",   url, requestBody,(String) null, (String) null, params,
                headers, responseHandler);

    }

    public static String httpDelete( String poolname,String url,Object params,Map headers) throws HttpProxyRequestException{
        return httpDelete(  poolname,   url, (String) null, (String) null, params,
                headers);

    }

    public static String httpDelete ( String poolname,String url,String requestBody,Object params,Map headers) throws HttpProxyRequestException{
        return httpDelete(  poolname,   url,  requestBody,(String)null, (String) null, params,
                headers);

    }

    public static <T> T httpDelete( String poolname,String url,Object params,Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
        return httpDelete(  poolname,   url,(String)null, (String) null, (String) null, params,
                headers,responseHandler);

    }

    public static <T> T httpDelete( String poolname,String url,String requestBody,Object params,Map headers,ResponseHandler<T> responseHandler) throws HttpProxyRequestException{
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
    public static String httpDelete(String poolname, String url, String cookie, String userAgent, Object params,
                                                Map headers) throws HttpProxyRequestException {
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
    public static String httpDelete(String poolname,String url,String requestBody,  String cookie, String userAgent, Object params,
                                    Map headers) throws HttpProxyRequestException {
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
    public static <T> T httpDelete(String poolname, String url, String requestBody, final String cookie,final  String userAgent, Object params,
                                   final Map headers,final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        final HttpEntity httpEntity = requestBody == null?null:new StringEntity(
                requestBody,
                ContentType.APPLICATION_JSON);
        HttpParams httpParams = null;
        if (params != null) {
            httpParams = HttpParamsHandler.httpParams( params);
//            Iterator<Entry> it = params.entrySet().iterator();
//            for (int i = 0; it.hasNext(); i++) {
//                Entry entry = it.next();
//                httpParams.setParameter(String.valueOf(entry.getKey()), entry.getValue());
//            }
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
      

    }


    public static String sendStringBody(String poolname,String requestBody, String url, Map headers) throws HttpProxyRequestException {
        return  sendBody(poolname,  requestBody,   url,   headers,ContentType.create(
                "text/plain", Consts.UTF_8));
    }

    public static String sendJsonBody(String poolname,Object requestBody, String url) throws HttpProxyRequestException {

        return  sendBody(   poolname, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON);
    }

    public static <T> T sendJsonBody(String poolname,Object requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBody(   poolname, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,  resultType);
    }
    public static <T> T sendJsonBody(String poolname,Object requestBody,Map<String,String> headers, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBody(   poolname, object2json(requestBody),   url,   headers,ContentType.APPLICATION_JSON,  resultType);
    }
    public static <T> List<T> sendJsonBodyForList(String poolname,Object requestBody, String url,Map headers,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForList(   poolname, object2json(requestBody),   url,   headers,ContentType.APPLICATION_JSON,  resultType);
    }
    public static <T> List<T> sendJsonBodyForList(String poolname,Object requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForList(   poolname, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,  resultType);
    }
	public static <T> List<T> sendJsonBodyForList(Object requestBody, String url,Class<T> resultType) throws HttpProxyRequestException {

		return  sendBodyForList(   (String) null, object2json(requestBody),   url,   null,ContentType.APPLICATION_JSON,  resultType);
	}


    public static <D,T> D sendJsonBodyTypeObject(String poolname, String url,Object requestBody,Map headers,Class<D> containerType,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForTypeObject(   poolname,    url, object2json(requestBody),  headers,ContentType.APPLICATION_JSON, containerType, resultType);
    }
    public static <D,T> D sendJsonBodyTypeObject(String poolname, String url,Object requestBody,Class<D> containerType,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForTypeObject(   poolname,    url,  object2json(requestBody), null,ContentType.APPLICATION_JSON,containerType,  resultType);
    }
    public static <D,T> D sendJsonBodyTypeObject( String url,Object requestBody,Class<D> containerType,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBodyForTypeObject(   (String) null,   url, object2json(requestBody), null,ContentType.APPLICATION_JSON,containerType,  resultType);
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

    public static <T> T sendJsonBody(String poolname, Object requestBody, String url, Map headers,Class<T> type) throws HttpProxyRequestException {

        return  sendBody(  poolname, object2json(requestBody),   url,   headers,ContentType.APPLICATION_JSON,type);
    }
    public static String sendJsonBody(String poolname, Object requestBody, String url, Map headers) throws HttpProxyRequestException {

        return  sendBody(  poolname, object2json(requestBody),   url,   headers,ContentType.APPLICATION_JSON);
    }

    public static String sendStringBody(String requestBody, String url, Map headers) throws HttpProxyRequestException {
        return  sendBody("default",  requestBody,   url,   headers,ContentType.create(
                "text/plain", Consts.UTF_8));
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

    public static <T> T sendJsonBody(Object requestBody, String url,Map headers,Class<T> resultType) throws HttpProxyRequestException {

        return  sendBody( "default", object2json(requestBody),   url,   headers,ContentType.APPLICATION_JSON,resultType);
    }

    public static <T> T sendJsonBody(Object requestBody, String url,InvokeContext invokeContext,Class<T> resultType) throws HttpProxyRequestException {
        if(invokeContext != null && invokeContext.getRequestContentType() == null){
            invokeContext.setRequestContentType(ContentType.APPLICATION_JSON);
        }
        return  sendBody( "default", object2json(requestBody),   url,   invokeContext,resultType);
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

    public static String sendJsonBody(String requestBody, String url, Map headers ) throws HttpProxyRequestException {

        return  sendBody( "default", requestBody,   url,   headers,ContentType.APPLICATION_JSON);
    }
    public static String sendJsonBody(String requestBody, String url, InvokeContext invokeContext ) throws HttpProxyRequestException {
        if(invokeContext != null && invokeContext.getRequestContentType() == null){
            invokeContext.setRequestContentType(ContentType.APPLICATION_JSON);
        }
        return  sendBody( "default", requestBody,   url,  invokeContext);
    }
    public static <T> T sendJsonBody(String requestBody, String url, Map headers  ,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {

        return  sendBody( "default", requestBody,   url,   headers,ContentType.APPLICATION_JSON, responseHandler);
    }

    public static <T> T sendJsonBody(String poolname,String requestBody, String url, Map headers  ,ResponseHandler<T> responseHandler) throws HttpProxyRequestException {

        return  sendBody( poolname, requestBody,   url,   headers,ContentType.APPLICATION_JSON, responseHandler);
    }

    private static void injectBody(ResponseHandler responseHandler,String requestBody){
        if(requestBody == null || responseHandler == null)
            return;
        if(responseHandler instanceof BaseURLResponseHandler){
            BaseURLResponseHandler baseURLResponseHandler = (BaseURLResponseHandler)responseHandler;
            if(!baseURLResponseHandler.isEnableSetRequestBody() && baseURLResponseHandler.getRequestBody() == null){
                baseURLResponseHandler.setRequestBody(requestBody);
                baseURLResponseHandler.setEnableSetRequestBody(true);
                baseURLResponseHandler.setTruncateLogBody(true);
            }
        }
    }
    public static <T> T sendBody(final String poolname,  String requestBody, String url,
                                 final Map headers, ContentType contentType,
                                 final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        InvokeContext invokeContext = new InvokeContext();
        invokeContext.setHeaders(headers);
        invokeContext.setRequestContentType(contentType);
        return sendBody( poolname,   requestBody,  url,
                 invokeContext,         responseHandler);
       
     
    }

    public static <T> T sendBody(final String poolname,  String requestBody, String url,
                                 InvokeContext invokeContext,
                                 final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        final HttpEntity httpEntity = new StringEntity(
                requestBody,
                invokeContext.getRequestContentType());
        injectBody(responseHandler, requestBody);
        return _handleRequest( poolname, url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpPost httpPost = null;
                        try {
                            httpPost = HttpRequestUtil.getHttpPost(config, url, "", "", invokeContext.getHeaders());
                            httpPost.setEntity(httpEntity);

                            return httpClient.execute(httpPost, responseHandler);
                        }
                        finally {
                            // 释放连接
                            if (httpPost != null)
                                httpPost.releaseConnection();
                        }
                    }
                } );

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

    private static StringBuilder trucateData(ResponseHandler responseHandler){
        if(responseHandler instanceof BaseURLResponseHandler){
            BaseURLResponseHandler baseURLResponseHandler = (BaseURLResponseHandler)responseHandler;
            if(baseURLResponseHandler.isEnableSetRequestBody()) {
                StringBuilder builder = new StringBuilder();
               String requestBody = baseURLResponseHandler.getRequestBody();
                builder.append("RequestBody:");
                if(baseURLResponseHandler.isTruncateLogBody() && requestBody != null && requestBody.length() > 4096){
                    builder.append(requestBody.substring(0,4095)).append("......");
                }
                else {
                    builder.append(requestBody);
                }
                return builder;
            }
        }
        return null;

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

        ClientConfiguration config = ClientConfiguration.getClientConfiguration(poolname);

        return  _handleRequest(  config,   url,
                                          responseHandler,  executeRequest)  ;

    }

    /**
     * 公共处理请求方法
     * @param url
     * @param responseHandler
     * @param executeRequest
     * @param <T>
     * @return
     * @throws HttpProxyRequestException
     */
    private static <T> T _handleRequest(ClientConfiguration config, String url,

                                        ResponseHandler<T> responseHandler,ExecuteRequest executeRequest) throws HttpProxyRequestException {
        HttpClient httpClient = null;

        String poolname = config.getBeanName();
//        int RETRY_TIME = config.getRetryTime();
        T responseBody = null;
        String endpoint = null;
        Throwable e = null;
        Throwable httpResponseStatusException = null;
        int triesCount = 0;
        StringBuilder requestBody = trucateData( responseHandler);
        ExecuteIntercepter executeIntercepter = null;
        URLResponseHandler urlResponseHandler = null;
        if(responseHandler != null && responseHandler instanceof URLResponseHandler){
            urlResponseHandler =((URLResponseHandler)responseHandler);
            executeIntercepter = urlResponseHandler.getExecuteIntercepter();
        }
        if(!url.startsWith("http://") && !url.startsWith("https://")) {
            endpoint = url;
            HttpAddress httpAddress = null;
            HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
            assertCheck(  httpServiceHosts,endpoint ,config.getBeanName());
           
            
            do {

                try {

                    httpAddress = httpServiceHosts.getHttpAddress();

                    url = SimpleStringUtil.getPath(httpAddress.getAddress(), endpoint);
                    if(logger.isDebugEnabled()){
                        logger.debug("Send request {}",url);
                    }
                    if(urlResponseHandler != null){
                        urlResponseHandler.setUrl(url);
                    }

                    httpClient = HttpRequestUtil.getHttpClient(config);
                    BaseRequestKerberosUrlUtils baseRequestKerberosUrlUtils = config.getRequestKerberosUrlUtils();
                    if(executeIntercepter != null){
                        executeIntercepter.before(url,urlResponseHandler,triesCount);
                    }
                    if(baseRequestKerberosUrlUtils == null) {
                        responseBody = (T) executeRequest.execute(config, httpClient, url, triesCount);
                    }
                    else{
//                        requestBody = baseRequestKerberosUrlUtils.callRestUrl(config,httpClient,url,executeRequest,triesCount);
                        final String tempUrl = url;
                        final HttpClient tempHttpClient = httpClient;
                        final int tempTriesCount = triesCount;
                        responseBody = baseRequestKerberosUrlUtils.callRestUrl(new KerberosCallback<T>() {
                            @Override
                            public T call() throws Exception{
                                return (T) executeRequest.execute(config, tempHttpClient, tempUrl, tempTriesCount);
                            }
                        });
                    }
                    httpAddress.recover();
                    httpResponseStatusException = HttpParamsHandler.getException(  responseHandler,httpServiceHosts );
                    
                    break;
                } catch (HttpHostConnectException ex) { // 1
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                } catch (UnknownHostException ex) { // 2
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoRouteToHostException ex) { // 3
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (NoHttpResponseException ex) { // 4
                    httpAddress.setStatus(1);
                    e = new NoHttpServerException(ex);
                    if (!httpServiceHosts.reachEnd(triesCount ))  {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }

                }
                catch (ConnectionPoolTimeoutException ex){//连接池获取connection超时，直接抛出  // 5

                    e = handleConnectionPoolTimeOutException(poolname,url, config,ex);
                    break;
                }
                catch (ConnectTimeoutException connectTimeoutException){  // 6
                    httpAddress.setStatus(1);
                    e = handleConnectionTimeOutException(poolname,url,config,connectTimeoutException);
                    if (!httpServiceHosts.reachEnd(triesCount )) {//失败尝试下一个地址
                        triesCount++;
                        continue;
                    } else {
                        break;
                    }
                }

                catch (SocketTimeoutException ex) {  // 7
                    e = handleSocketTimeoutException(poolname,url,config, ex);
                    break;
                }
                catch (NoHttpServerException ex){  // 8
                    e = ex;

                    break;
                }
                catch (ClientProtocolException ex){  // 9
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

                catch (Exception ex) {  // 10
                    e = ex;
                    break;
                }
                catch (Throwable ex) { //11
                    e = ex;
                    break;
                }  finally {
                    // 释放连接
                    if(executeIntercepter != null){
                        executeIntercepter.after(url,urlResponseHandler,triesCount,responseBody,e);
                    }
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
                    urlResponseHandler.setUrl(url);
                }
                if(executeIntercepter != null){
                    executeIntercepter.before(url,urlResponseHandler,triesCount);
                }
                responseBody = (T)executeRequest.execute( config,httpClient,url,triesCount);
                if(executeIntercepter != null){
                    executeIntercepter.after(url,urlResponseHandler,triesCount,responseBody,null);
                }
//                httpPost = HttpRequestUtil.getHttpPost(config, url, "", "", headers);
//                if (httpEntity != null) {
//                    httpPost.setEntity(httpEntity);
//                }
//
//                responseBody = httpClient.execute(httpPost, responseHandler);

            } catch (Exception ex) {
                e = ex;
                if(executeIntercepter != null){
                    executeIntercepter.after(url,urlResponseHandler,triesCount,responseBody,e);
                }
                 
            } finally {
                // 释放连接

                httpClient = null;
            }
        }
        HttpProxyRequestException httpProxyRequestException = null;
        if(httpResponseStatusException != null){
            if(requestBody == null) {
                if (httpResponseStatusException instanceof HttpProxyRequestException)
                    httpProxyRequestException = (HttpProxyRequestException) httpResponseStatusException;
                else {
                    httpProxyRequestException = new HttpProxyRequestException("Send request Url:" + url, httpResponseStatusException);
                    httpProxyRequestException.setHttpResponseStatusException(httpResponseStatusException);
                }
                
            }
            else{

                if (httpResponseStatusException instanceof HttpProxyRequestException) {
                    httpProxyRequestException = new HttpProxyRequestException(requestBody.toString(),httpResponseStatusException);
                }
                else {
                    httpProxyRequestException = new HttpProxyRequestException(requestBody.append("\r\nSend request Url:").append(url).toString(), httpResponseStatusException);
                    httpProxyRequestException.setHttpResponseStatusException(httpResponseStatusException);
                }
            }
            throw httpProxyRequestException;
        }
        if (e != null){
            
            if(requestBody == null) {
                if (e instanceof HttpProxyRequestException)
                    httpProxyRequestException = (HttpProxyRequestException) e;
                else
                    httpProxyRequestException = new HttpProxyRequestException("Send request Url:" + url, e);
            }
            else{

                if (e instanceof HttpProxyRequestException) {
                    httpProxyRequestException = new HttpProxyRequestException(requestBody.toString(),e);
                }
                else {
                    httpProxyRequestException = new HttpProxyRequestException(requestBody.append("\r\nSend request Url:").append(url).toString(), e);
                }
            }
            throw httpProxyRequestException;
        }
        return responseBody;
    }
    private static HttpRequestBase getHttpEntityEnclosingRequestBase(String action,ClientConfiguration config, String url,  Map headers){
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

    public static <T> T sendBody(String poolname,String requestBody, String url,final Map headers,ContentType contentType,
                                 final ResponseHandler<T> responseHandler,final String action) throws HttpProxyRequestException {




        final HttpEntity httpEntity = requestBody != null?new StringEntity(
                requestBody,
                contentType):null;
        injectBody(responseHandler, requestBody);
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

                            return httpClient.execute(httpPost, responseHandler);
                        }
                        finally {
                            // 释放连接
                            if (httpPost != null)
                                httpPost.releaseConnection();
                        }
                    }
                } );
        
    }

    public static String sendBody(String poolname,String requestBody, String url, Map headers,ContentType contentType) throws HttpProxyRequestException {
    	return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<String>() {

            @Override
            public String handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleStringResponse(url, response);
            }

        });
        
    }

    public static String sendBody(String poolname, String requestBody, String url, final InvokeContext invokeContext) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, invokeContext, new BaseURLResponseHandler<String>() {

            @Override
            public String handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleStringResponse(url, response,invokeContext);
            }

        });

    }
    public static <T> T sendBody(String poolname,String requestBody, String url, InvokeContext invokeContext,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, invokeContext, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse( url, response, resultType);
            }

        });

    }
   
    public static <T> T sendBody(String poolname,String requestBody, String url, Map headers,ContentType contentType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<T>() {

            @Override
            public T handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse( url, response, resultType);
            }

        });

    }

    public static <D,T> D sendBody(String poolname,String requestBody, String url, Map headers,ContentType contentType,final Class<D> containType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<D>() {

            @Override
            public D handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleResponse( url, response, containType,resultType);
            }

        });

    }
    public static <D,T> D httpPostForTypeObject(String url, Object params, final Class<D> containType, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostForTypeObject("default", url, params, (Map)null, containType, resultType);
//        return httpPostforString(  poolName,url, params, (Map) null);
    }
    public static <D,T> D httpPostForTypeObject(String poolName,String url, Object params, final Class<D> containType, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostForTypeObject(  poolName,  url,   params, (Map)null,  containType,  resultType);
    }
    public static <D,T> D httpPostForTypeObjectWithHeader(String url, Object params,Map header, final Class<D> containType, final Class<T> resultType) throws HttpProxyRequestException {
        return httpPostForTypeObject("default", url, params, header, containType, resultType);
//        return httpPostforString(  poolName,url, params, (Map) null);
    }
    public static <D,T> D httpPostForTypeObject(String poolName,String url, Object params, Map headers,final Class<D> containType, final Class<T> resultType) throws HttpProxyRequestException {
        HttpOption httpOption = new HttpOption();

        httpOption.params = params;
        httpOption.headers = headers;
        return httpPost(  poolName,   url,  httpOption, new BaseURLResponseHandler<D>() {
            @Override
            public D handleResponse(HttpResponse response) throws IOException {
                return ResponseUtil.handleResponse(url,response,containType,resultType);
            }
        });
//        return httpPostforString(  poolName,url, params, (Map) null);
    }



    public static <T> List<T> sendBodyForList(String poolname,String requestBody, String url, Map headers,ContentType contentType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<List<T>>() {

            @Override
            public List<T> handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleListResponse( url, response, resultType);
            }

        });

    }


    public static <D,T> D sendBodyForTypeObject(String poolname,String url,String requestBody,  Map headers,ContentType contentType,final Class<D> containerType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<D>() {

            @Override
            public D handleResponse(final HttpResponse response)
                    throws IOException {
//                return ResponseUtil.handleListResponse( url, response, resultType);
                return ResponseUtil.handleResponse(url,response,containerType,resultType);
            }

        });

    }

    public static <T> Set<T> sendBodyForSet(String poolname,String requestBody, String url, Map headers,ContentType contentType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<Set<T>>() {

            @Override
            public Set<T> handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleSetResponse( url, response, resultType);
            }

        });

    }

    public static <K,T> Map<K,T> sendBodyForMap(String poolname,String requestBody, String url, Map headers,ContentType contentType,final Class<K> keyType,final Class<T> resultType) throws HttpProxyRequestException {
        return sendBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<Map<K,T> >() {

            @Override
            public Map<K,T>  handleResponse(final HttpResponse response)
                    throws IOException {
                return ResponseUtil.handleMapResponse( url, response, keyType,resultType);
            }

        });

    }
    private static void assertCheck(HttpServiceHosts httpServiceHosts,String endpoint,String poolName ){


        if(httpServiceHosts == null){
            StringBuilder error = new StringBuilder();
            error.append("RequestInfo[").append(endpoint)
                    .append("], poolName [").append(poolName)
                    .append("], HttpServiceHosts is null,Http Request Proxy is not properly initialized, please refer to the document: https://esdoc.bbossgroups.com/#/httpproxy?id=_32-http负载均衡器配置和启动");
            String msg = error.toString();
            if(logger.isWarnEnabled()){
                logger.warn(msg);
            }
            throw new HttpProxyRequestException(msg);
        }
    }
    public static <T> T putBody(String poolname,String requestBody, String url, final Map headers,ContentType contentType,final ResponseHandler<T> responseHandler) throws HttpProxyRequestException {



        final HttpEntity httpEntity = new StringEntity(
                requestBody,
                contentType);
        injectBody(responseHandler, requestBody);
        return _handleRequest( poolname,  url ,
                responseHandler,new ExecuteRequest(){
                    @Override
                    public Object execute(ClientConfiguration config, HttpClient httpClient,String url, int triesCount) throws Exception {
                        HttpPut httpPost = null;
                        try {
                            httpPost = HttpRequestUtil.getHttpPut(config, url, "", "", headers);
                            httpPost.setEntity(httpEntity);

                            return httpClient.execute(httpPost, responseHandler);
                        }
                        finally {
                            // 释放连接
                            if (httpPost != null)
                                httpPost.releaseConnection();
                        }
                    }
                } );
        
    }
    
    public static String putBody(String poolname,String requestBody, String url, Map headers,ContentType contentType) throws HttpProxyRequestException {
    	return putBody(  poolname,  requestBody,   url, headers,  contentType, new BaseURLResponseHandler<String>() {

            @Override
            public String handleResponse(final HttpResponse response)
                    throws IOException {
                int status = response.getStatusLine().getStatusCode();

                if (org.frameworkset.spi.remote.http.ResponseUtil.isHttpStatusOK( status)) {
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
    
    public static <T> T putBody(String requestBody, String url, Map headers,ContentType contentType, ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return putBody( "default", requestBody,   url,  headers,  contentType,  responseHandler) ;
    }
    
    public static String putBody(String requestBody, String url, Map headers,ContentType contentType) throws HttpProxyRequestException {
    	return putBody( "default",requestBody,   url,   headers,  contentType) ;
        
    }
    
    



    
    public static <T> T putJson(String requestBody, String url, Map headers, ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return putBody( "default", requestBody,   url,  headers,   ContentType.APPLICATION_JSON,  responseHandler) ;
    }

    public static <T> T putJson(String poolName,String requestBody, String url, Map headers, ResponseHandler<T> responseHandler) throws HttpProxyRequestException {
        return putBody( poolName, requestBody,   url,  headers,   ContentType.APPLICATION_JSON,  responseHandler) ;
    }


    public static String putJson(String requestBody, String url, Map headers) throws HttpProxyRequestException {
    	return putBody( "default",requestBody,   url,   headers,  ContentType.APPLICATION_JSON) ;
        
    }

    public static String putJson(String poolName,String requestBody, String url, Map headers) throws HttpProxyRequestException {
        return putBody(poolName,requestBody,   url,   headers,  ContentType.APPLICATION_JSON) ;

    }

    public static String putJson(String poolName,String requestBody, String url) throws HttpProxyRequestException {
        return putBody(poolName,requestBody,   url,   (Map )null,  ContentType.APPLICATION_JSON) ;

    }

    public static interface ExecuteRequest{
        Object execute(ClientConfiguration config,HttpClient httpClient ,String url, int triesCount) throws Exception;
    }


}
