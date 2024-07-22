/**
 *
 */
package org.frameworkset.spi.remote.http;

import java.util.concurrent.ConcurrentHashMap;
import com.frameworkset.util.SimpleStringUtil;
import com.frameworkset.util.ValueCastUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.frameworkset.spi.*;
import org.frameworkset.spi.assemble.GetProperties;
import org.frameworkset.spi.assemble.MapGetProperties;
import org.frameworkset.spi.assemble.PropertiesContainer;
import org.frameworkset.spi.remote.http.callback.HttpClientBuilderCallback;
import org.frameworkset.spi.remote.http.proxy.ExceptionWare;
import org.frameworkset.spi.remote.http.proxy.HttpHostDiscover;
import org.frameworkset.spi.remote.http.proxy.HttpServiceHosts;
import org.frameworkset.spi.remote.http.ssl.SSLHelper;
import org.frameworkset.util.ClassUtil;
import org.frameworkset.util.ResourceStartResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This example demonstrates how to customize and configure the most common aspects
 * of HTTP request execution and connection management.
 */

/**
 * @author yinbp
 *
 * @Date:2016-11-20 11:50:36
 */
public class ClientConfiguration implements InitializingBean, BeanNameAware {
	public static final ContentType TEXT_PLAIN_UTF_8 = ContentType.create(
			"text/plain", Consts.UTF_8);

	public static final ContentType TEXT_HTML_UTF_8 = ContentType.create(
			"text/html", Consts.UTF_8);
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int DEFAULT_validateAfterInactivity = -1;
	public static final String http_authAccount = "http.authAccount";
	public static final String http_authPassword = "http.authPassword";
	public static final String http_healthCheck_prex = "__healthCheck_";


	private final static int RETRY_TIME = 3;
	private boolean automaticRetriesDisabled = false;
	private static final DefaultHttpRequestRetryHandler defaultHttpRequestRetryHandler = new ConnectionResetHttpRequestRetryHandler();
	private static final Logger logger = LoggerFactory.getLogger(ClientConfiguration.class);
	private static RequestConfig defaultRequestConfig;
	private static final Map<String, ClientConfiguration> clientConfigs = new ConcurrentHashMap();
	private static BaseApplicationContext context;
	private static boolean emptyContext;
	private static ClientConfiguration defaultClientConfiguration;
	private transient CloseableHttpClient httpclient;
	private transient RequestConfig requestConfig;
	private int timeoutConnection = TIMEOUT_CONNECTION;
	private int timeoutSocket ;
	private int connectionRequestTimeout = TIMEOUT_SOCKET;
	private int retryTime = RETRY_TIME;
	private int maxLineLength = 2000;
	private int maxHeaderCount = 200;
	private int maxTotal = 200;
	private int defaultMaxPerRoute = 10;
	private long retryInterval = -1;
	private Boolean soKeepAlive = false;
	private Boolean soReuseAddress = false;
	private String hostnameVerifierString;
	private GetProperties contextProperties;

	public String getEncodedAuthCharset() {
		return encodedAuthCharset;
	}

	public void setEncodedAuthCharset(String encodedAuthCharset) {
		this.encodedAuthCharset = encodedAuthCharset;
	}

	private String encodedAuthCharset = "US-ASCII";
	/**
	 * 向后兼容的basic安全签名机制，v6.1.2以及之后的版本采用http组件内置的basic签名认证机制，但是有些http服务端对安全认证
	 * 的实现不是很规范，会导致http basic security机制不能正常工作，因此设置这个向老版本兼容的配置
	 * true:向老版本兼容，false，不向老版本兼容
	 */
	private boolean backoffAuth = false;

	public Object getHttpClientBuilderCallback() {
		return httpClientBuilderCallback;
	}

	public void setHttpClientBuilderCallback(Object httpClientBuilderCallback) {
		this.httpClientBuilderCallback = httpClientBuilderCallback;
	}

	private Object httpClientBuilderCallback;

    public Object getHttpRequestInterceptors() {
        return httpRequestInterceptors;
    }
    /**
     * http接口org.apache.http.HttpRequestInterceptor清单，多个用逗号分隔
     */
    public void setHttpRequestInterceptors(Object httpRequestInterceptors) {
        this.httpRequestInterceptors = httpRequestInterceptors;
    }
    /**
     * http接口org.apache.http.HttpRequestInterceptor清单，多个用逗号分隔
     */
    private Object httpRequestInterceptors;
	public String getAuthAccount() {
		return authAccount;
	}

	public void setAuthAccount(String authAccount) {
		this.authAccount = authAccount;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	private String authAccount;
	private String authPassword;
	/**
	 * 单位毫秒：
	 */
	private final int maxIdleTime = -1;

	public HttpServiceHosts getHttpServiceHosts() {
		return httpServiceHosts;
	}

	public void setHttpServiceHosts(HttpServiceHosts httpServiceHosts) {
		this.httpServiceHosts = httpServiceHosts;
	}

	private HttpServiceHosts httpServiceHosts;
	/**
	 * 每隔多少毫秒校验空闲connection，自动释放无效链接
	 * -1 或者0不检查
	 */
	private int validateAfterInactivity = DEFAULT_validateAfterInactivity;
	/**
	 * 每次获取connection时校验连接，true，校验，false不校验，有性能开销，推荐采用
	 * validateAfterInactivity来控制连接是否有效
	 * 默认值false
	 */
	private boolean staleConnectionCheckEnabled = false;
	/**
	 * 自定义重试控制接口，必须实现接口方法
	 * public interface CustomHttpRequestRetryHandler  {
	 * 	public boolean retryRequest(IOException exception, int executionCount, HttpContext context,ClientConfiguration configuration);
	 * }
	 * 方法返回true，进行重试，false不重试
	 */
	private String customHttpRequestRetryHandler;
	private int timeToLive = 3600000;

	/**
	 * Using the keystore- and truststore file
	 */
	private String keystore;
	private String keystoreAlias;
	private String keyPassword;

	private String truststore;
	private String trustAlias;
	private String trustPassword;

	/**
	 * Using PEM certificates
	 * pem证书配置
	 */
	private String pemCert;
	//可选项，如果pemCert为.crt类型文件，则无需配置以下参数
	private String pemtrustedCA;
	private String pemKey;
	private String pemkeyPassword;


	private String supportedProtocols;
	private String[] _supportedProtocols;
	private transient HostnameVerifier hostnameVerifier;

	private final String[] defaultSupportedProtocols = new String[]{"TLSv1.2", "TLSv1.1", "TLSv1"};
	/**
	 * 默认保活1小时

	 */
	private long keepAlive = 1000l * 60l * 60l;
	private String beanName;

	public boolean isEvictExpiredConnections() {
		return evictExpiredConnections;
	}

	public void setEvictExpiredConnections(boolean evictExpiredConnections) {
		this.evictExpiredConnections = evictExpiredConnections;
	}

	private boolean evictExpiredConnections = true;

	/**
	 *
	 */
	public ClientConfiguration() {
		// TODO Auto-generated constructor stub
	}

	public String getBeanName(){
		return this.beanName;
	}
	private static void loadClientConfiguration() {
		if (context == null) {
			context = DefaultApplicationContext.getApplicationContext("conf/httpclient.xml");
			emptyContext = context.isEmptyContext();
		}

	}

	public static RequestConfig getDefaultRequestConfig() {
		return defaultRequestConfig;
	}

	public static HttpClient getDefaultHttpclient() {
		loadClientConfiguration();
		return getDefaultClientConfiguration(null)._getHttpclient();
	}
	public static ClientConfiguration getDefaultClientConfiguration(){
		return getDefaultClientConfiguration((ResourceStartResult)null);
	}
	public static ClientConfiguration getDefaultClientConfiguration(ResourceStartResult resourceStartResult) {
		loadClientConfiguration();
		if (defaultClientConfiguration != null)
			return defaultClientConfiguration;

		if (defaultClientConfiguration == null) {

			try {
				defaultClientConfiguration = makeDefualtClientConfiguration(resourceStartResult,"default");
			} catch (Exception e) {
				throw new ConfigHttpRuntimeException("Get DefaultClientConfiguration[default] failed:", e);
			}
		}
		return defaultClientConfiguration;
	}

	private static ClientConfiguration _getDefaultClientConfiguration(ResourceStartResult resourceStartResult,String healthPoolname,GetProperties context) {
		if(healthPoolname != null){
			try {
				return makeDefualtClientConfiguration(  resourceStartResult,healthPoolname,"default", context);
			} catch (Exception e) {
				throw new ConfigHttpRuntimeException("Get Default healthcheck ClientConfiguration["+healthPoolname+"] failed:", e);
			}
		}
		else if (defaultClientConfiguration != null) {
			return defaultClientConfiguration;
		}
		else{

			try {
				defaultClientConfiguration = makeDefualtClientConfiguration(  resourceStartResult,healthPoolname,"default", context);
				return defaultClientConfiguration;
			} catch (Exception e) {
				throw new ConfigHttpRuntimeException("Get DefaultClientConfiguration[default] failed:", e);
			}
		}

	}

	private static ClientConfiguration makeDefualtClientConfiguration(ResourceStartResult resourceStartResult,String name) throws Exception {

		ClientConfiguration clientConfiguration = clientConfigs.get(name);
		if (clientConfiguration != null) {
			return clientConfiguration;
		}
		synchronized (ClientConfiguration.class) {
			clientConfiguration = clientConfigs.get(name);
			if (clientConfiguration != null) {
				return clientConfiguration;
			}
			if (!emptyContext) {
				try {
					clientConfiguration = context.getTBeanObject(name, ClientConfiguration.class);
				} catch (SPIException e) {
					if(logger.isWarnEnabled()) {
						if(!name.startsWith(http_healthCheck_prex)) {
							logger.warn(new StringBuilder().append("Make ClientConfiguration [").append(name).append("] failed,an internal http pool will been constructed:").append(e.getMessage()).toString());
						}
					}
				}

			}
			if (clientConfiguration == null) {
				if(!name.startsWith(http_healthCheck_prex)) {//Health check pool
					clientConfiguration = new ClientConfiguration();
					/**
					 * f:timeoutConnection = "20000"
					 f:timeoutSocket = "20000"
					 f:retryTime = "1"
					 f:maxLineLength = "2000"
					 f:maxHeaderCount = "200"
					 f:maxTotal = "200"
					 f:defaultMaxPerRoute = "10"
					 */
					clientConfiguration.setTimeoutConnection(50000);
					clientConfiguration.setTimeoutSocket(0);
					clientConfiguration.setConnectionRequestTimeout(50000);
					clientConfiguration.setRetryTime(-1);
					clientConfiguration.setRetryInterval(-1);
					clientConfiguration.setTimeToLive(3600000);
					clientConfiguration.setEvictExpiredConnections(true);
					clientConfiguration.setMaxLineLength(Integer.MAX_VALUE);
					clientConfiguration.setMaxHeaderCount(Integer.MAX_VALUE);
					clientConfiguration.setMaxTotal(500);
					clientConfiguration.setAutomaticRetriesDisabled(true);
					clientConfiguration.setDefaultMaxPerRoute(100);
					clientConfiguration.setStaleConnectionCheckEnabled(false);
					clientConfiguration.setValidateAfterInactivity(DEFAULT_validateAfterInactivity);
					clientConfiguration.setCustomHttpRequestRetryHandler(null);
					clientConfiguration.setBeanName(name);

					clientConfiguration.afterPropertiesSet();
					clientConfigs.put(name, clientConfiguration);
					if(resourceStartResult != null)
						resourceStartResult.addResourceStartResult(name);
					if(logger.isInfoEnabled()){
						logger.info("Make http pool[{}] use default config completed!",name);
					}
				}
				else{
					clientConfiguration = new ClientConfiguration();
					/**
					 * f:timeoutConnection = "20000"
					 f:timeoutSocket = "20000"
					 f:retryTime = "1"
					 f:maxLineLength = "2000"
					 f:maxHeaderCount = "200"
					 f:maxTotal = "200"
					 f:defaultMaxPerRoute = "10"
					 */
					clientConfiguration.setTimeoutConnection(5000);
					clientConfiguration.setTimeoutSocket(0);
					clientConfiguration.setConnectionRequestTimeout(5000);
					clientConfiguration.setTimeToLive(3600000);
					clientConfiguration.setEvictExpiredConnections(true);
					clientConfiguration.setRetryTime(3);
					clientConfiguration.setRetryInterval(-1);
					clientConfiguration.setMaxLineLength(Integer.MAX_VALUE);
					clientConfiguration.setMaxHeaderCount(Integer.MAX_VALUE);
					clientConfiguration.setMaxTotal(500);
					clientConfiguration.setDefaultMaxPerRoute(50);
					clientConfiguration.setStaleConnectionCheckEnabled(false);
					clientConfiguration.setValidateAfterInactivity(DEFAULT_validateAfterInactivity);
					clientConfiguration.setCustomHttpRequestRetryHandler(null);
					clientConfiguration.setBeanName(name);
					clientConfiguration.afterPropertiesSet();
					clientConfigs.put(name, clientConfiguration);
					if(resourceStartResult != null)
						resourceStartResult.addResourceStartResult(name);
					if(logger.isInfoEnabled()){
						logger.info("Make http pool[{}] use default config completed!",name);
					}
				}
			}
		}
		return clientConfiguration;

	}

	private static long _getLongValue(String poolName, String propertyName, GetProperties context, long defaultValue) throws Exception {
		Object _value = null;
		if (poolName.equals("default")) {
			_value =   context.getExternalObjectProperty(propertyName);
			if (_value == null)
				_value =   context.getExternalObjectProperty(poolName + "." + propertyName);

		} else {
			_value =  context.getExternalObjectProperty(poolName + "." + propertyName);
		}
		if (_value == null) {
			return defaultValue;
		}
		return ValueCastUtil.toLong(_value,defaultValue);
	}

	private static boolean _getBooleanValue(String poolName, String propertyName, GetProperties context, boolean defaultValue) throws Exception {
		Object _value = null;
		if (poolName.equals("default")) {
			_value =   context.getExternalObjectProperty(propertyName);
			if (_value == null)
				_value =  context.getExternalObjectProperty(poolName + "." + propertyName);

		} else {
			_value =   context.getExternalObjectProperty(poolName + "." + propertyName);
		}
		if (_value == null) {
			return defaultValue;
		}
		return ValueCastUtil.toBoolean(_value,defaultValue);
	}

	private static int _getIntValue(String poolName, String propertyName, GetProperties context, int defaultValue) throws Exception {
		Object _value = null;
		if (poolName.equals("default")) {
			_value =  context.getExternalObjectProperty(propertyName);
			if (_value == null)
				_value =  context.getExternalObjectProperty(poolName + "." + propertyName);

		} else {
			_value =  context.getExternalObjectProperty(poolName + "." + propertyName);
		}

		return ValueCastUtil.toInt(_value,defaultValue);

	}

	private static String _getStringValue(String poolName, String propertyName, GetProperties context, String defaultValue) throws Exception {
		Object _value = null;
		if (poolName.equals("default")) {
			_value = context.getExternalPropertyWithNS(poolName,propertyName);
			if (_value == null)
				_value =  context.getExternalPropertyWithNS(poolName,poolName + "." + propertyName);

		} else {
			_value =  context.getExternalPropertyWithNS(poolName,poolName + "." + propertyName);
		}
		return ValueCastUtil.toString(_value,defaultValue);
	}

	private static Object _getObjectValue(String poolName, String propertyName, GetProperties context, Object defaultValue) throws Exception {
		Object _value = null;
		if (poolName.equals("default")) {
			_value =  context.getExternalObjectProperty(propertyName);
			if (_value == null)
				_value = context.getExternalObjectProperty(poolName + "." + propertyName);

		} else {
			_value = context.getExternalObjectProperty(poolName + "." + propertyName);
		}
		if (_value == null) {
			return defaultValue;
		}
		return _value;
	}

	private static HostnameVerifier _getHostnameVerifier(String hostnameVerifier) throws Exception {

		if (hostnameVerifier == null) {
			return null;
		}
		if (hostnameVerifier.equals("defualt"))
			return org.apache.http.conn.ssl.SSLConnectionSocketFactory.getDefaultHostnameVerifier();
		else
			return org.apache.http.conn.ssl.SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
	}
	public static ResourceStartResult startHttpPoolsFromApolloAwaredChange(String namespaces){
		String apolloAwaredChangeListener = "org.frameworkset.apollo.HttpProxyConfigChangeListener";
		try {
			Class t = Class.forName(apolloAwaredChangeListener);
		} catch (ClassNotFoundException e) {
			StringBuilder msg = new StringBuilder();


			msg.append("Start HttpPools From Apollo by AwaredChange failed: Please add compile dependency to build.gradle in gralde project: \r\ncompile \"com.bbossgroups.plugins:bboss-plugin-httpproxy-apollo:5.7.7\"")
					.append(" \r\nor Add compile dependency to pom.xml in maven project: \r\n    " )
					.append( "    <dependency>\n"  )
					.append("            <groupId>com.bbossgroups.plugins</groupId>\n"  )
					.append("            <artifactId>bboss-plugin-httpproxy-apollo</artifactId>\n"  )
					.append("            <version>6.0.1</version>\n"  )
					.append("        </dependency>");
			logger.error(msg.toString(),e);
			throw new IllegalArgumentException(msg.toString(),e);
		}

		return startHttpPoolsFromApollo(namespaces,apolloAwaredChangeListener);
	}
	public static ResourceStartResult startHttpPoolsFromApollo(String namespaces){
		return startHttpPoolsFromApollo(namespaces, null);
	}
	public static ResourceStartResult startHttpPoolsFromApollo(String namespaces, String configChangeListener){
		ResourceStartResult resourceStartResult = new HttpResourceStartResult();
		if(namespaces == null || namespaces.equals(""))
		{
			if(logger.isWarnEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Ignore start HttpPools from Apollo: namespaces is empty!");
				logger.warn(message.toString());
			}
			return resourceStartResult;
		}
		PropertiesContainer propertiesContainer = new PropertiesContainer();
		propertiesContainer.addConfigPropertiesFromApollo(namespaces,configChangeListener);
		propertiesContainer.afterLoaded(propertiesContainer);
		//http.poolNames = scedule,elastisearch
		String poolNames = propertiesContainer.getProperty("http.poolNames");
		if(poolNames == null){
			//load default http pool config
			try {
				makeDefualtClientConfiguration(resourceStartResult,null,"default", propertiesContainer);
				String health = ClientConfiguration._getStringValue("default", "http.health", propertiesContainer, null);
				if(health != null && !health.equals("")){
					makeDefualtClientConfiguration(resourceStartResult,getHealthPoolName(null),"default", propertiesContainer);
				}
			}
			catch (Exception e){
				if(logger.isErrorEnabled()) {
					StringBuilder message = new StringBuilder();
					message.append("Start HttpPools from Apollo[").append(namespaces).append("] failed:");
					logger.error(message.toString(), e);
				}
			}
		}
		else{
			String[] poolNames_ = poolNames.split(",");
			for(String poolName:poolNames_){
				poolName = poolName.trim();
				if(poolName.equals("")){
					poolName = "default";
				}
				try {
					makeDefualtClientConfiguration(resourceStartResult,null,poolName, propertiesContainer);
					String health = ClientConfiguration._getStringValue(poolName, "http.health", propertiesContainer, null);
					if(health != null && !health.equals("")){
						makeDefualtClientConfiguration(resourceStartResult,getHealthPoolName(poolName),poolName, propertiesContainer);
					}
				}
				catch (Exception e){
					if(logger.isErrorEnabled()) {
						StringBuilder message = new StringBuilder();
						message.append("Start HttpPools from Apollo[").append(namespaces).append("] failed:");
						logger.error(message.toString(), e);
					}
				}
			}
		}
		return resourceStartResult;
	}
	public static ResourceStartResult startHttpPools(String configFile){
		ResourceStartResult resourceStartResult = new HttpResourceStartResult();
		if(configFile == null || configFile.equals(""))
		{
			if(logger.isWarnEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Ignore start HttpPools from configfile[").append(configFile).append("]: configFile path is empty!");
				logger.warn(message.toString());
			}
			return resourceStartResult;
		}
		PropertiesContainer propertiesContainer = new PropertiesContainer();
		propertiesContainer.addConfigPropertiesFile(configFile);
		//http.poolNames = scedule,elastisearch
		String poolNames = propertiesContainer.getProperty("http.poolNames");
		if(poolNames == null){
			//load default http pool config
			try {
				makeDefualtClientConfiguration(resourceStartResult,null,"default", propertiesContainer);
				String health = ClientConfiguration._getStringValue("default", "http.health", propertiesContainer, null);
				if(health != null && !health.equals("")){
					makeDefualtClientConfiguration(resourceStartResult,getHealthPoolName(null),"default", propertiesContainer);
				}
			}
			catch (Exception e){
				if(logger.isErrorEnabled()) {
					StringBuilder message = new StringBuilder();
					message.append("Start HttpPools from configfile[").append(configFile).append("] failed:");
					logger.error(message.toString(), e);
				}
			}
		}
		else{
			String[] poolNames_ = poolNames.split(",");
			for(String poolName:poolNames_){
				poolName = poolName.trim();
				if(poolName.equals("")){
					poolName = "default";
				}
				try {
					makeDefualtClientConfiguration(resourceStartResult,null,poolName, propertiesContainer);
					String health = ClientConfiguration._getStringValue(poolName, "http.health", propertiesContainer, null);
					if(health != null && !health.equals("")){
						makeDefualtClientConfiguration(resourceStartResult,getHealthPoolName(poolName),poolName, propertiesContainer);
					}
				}
				catch (Exception e){
					if(logger.isErrorEnabled()) {
						StringBuilder message = new StringBuilder();
						message.append("Start HttpPools from configfile[").append(configFile).append("] failed:");
						logger.error(message.toString(), e);
					}
				}
			}
		}
		return resourceStartResult;
	}

	public static ResourceStartResult startHttpPools(Map<String,Object>  configs){
		ResourceStartResult resourceStartResult = new HttpResourceStartResult();
		if(configs == null || configs.size() == 0)
		{
			if(logger.isWarnEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Ignore start HttpPools from configs: configs is null or empty!");
				logger.warn(message.toString());
			}
			return resourceStartResult;
		}
		GetProperties propertiesContainer = new MapGetProperties(configs);
		//http.poolNames = scedule,elastisearch
		String poolNames = propertiesContainer.getExternalProperty("http.poolNames");
		if(poolNames == null){
			try {
				makeDefualtClientConfiguration(resourceStartResult,null,"default", propertiesContainer);
				String health = ClientConfiguration._getStringValue("default", "http.health", propertiesContainer, null);
				if(health != null && !health.equals("")){
					makeDefualtClientConfiguration(resourceStartResult,getHealthPoolName("default"),"default", propertiesContainer);
				}
			}
			catch (Exception e){
				if(logger.isErrorEnabled()) {
					StringBuilder message = new StringBuilder();
					message.append("Start HttpPool[default] from configs failed:");
					logger.error(message.toString(), e);
				}
			}
		}
		else{
			String[] poolNames_ = poolNames.split(",");
			for(String poolName:poolNames_){
				poolName = poolName.trim();
				if(poolName.equals("")){
					poolName = "default";
				}
				try {
					makeDefualtClientConfiguration(resourceStartResult,null,poolName, propertiesContainer);
					String health = ClientConfiguration._getStringValue(poolName, "http.health", propertiesContainer, null);
					if(health != null && !health.equals("")){
						makeDefualtClientConfiguration(resourceStartResult,getHealthPoolName(poolName),poolName, propertiesContainer);
					}
				}
				catch (Exception e){
					if(logger.isErrorEnabled()) {
						StringBuilder message = new StringBuilder();
						message.append("Start HttpPool[").append(poolName).append("] from configs failed:");
						logger.error(message.toString(), e);
					}
				}
			}
		}
		return resourceStartResult;
	}

	static ClientConfiguration _get(String healthPoolname,String name){
		ClientConfiguration clientConfiguration = null;
		if(healthPoolname != null){
			clientConfiguration = clientConfigs.get(healthPoolname);
		}
		else{
			clientConfiguration = clientConfigs.get(name);
		}
		return clientConfiguration;
	}
	static String rname(String healthPoolname,String name){
		if(healthPoolname != null){
			return healthPoolname;
		}
		else{
			return name;
		}
	}
	static ClientConfiguration makeDefualtClientConfiguration(ResourceStartResult resourceStartResult,String healthPoolname,String name, GetProperties context) throws Exception {
		ClientConfiguration clientConfiguration = _get(  healthPoolname,  name);

		if (clientConfiguration != null) {
			if(logger.isInfoEnabled()) {
				logger.info("Ignore MakeDefualtClientConfiguration and return existed Http Pool[{}].", rname(  healthPoolname,  name));
			}
			return clientConfiguration;
		}
		synchronized (ClientConfiguration.class) {
			clientConfiguration = _get(  healthPoolname,  name);
			if (clientConfiguration != null) {
				return clientConfiguration;
			}
			clientConfiguration = new ClientConfiguration();
			clientConfiguration.setContextProperties(context);
			/**
			 *http.timeoutConnection = 400000
			 * http.timeoutSocket = 400000
			 * http.connectionRequestTimeout=400000
			 * http.retryTime = 1
			 * http.maxLineLength = -1
			 * http.maxHeaderCount = 200
			 * http.maxTotal = 400
			 * http.defaultMaxPerRoute = 200
			 * #http.keystore =
			 * #http.keyPassword =
			 * #http.hostnameVerifier =
			 * http.soReuseAddress = false
			 * http.soKeepAlive = false
			 * http.timeToLive = 3600000
			 * http.keepAlive = 3600000
			 */
			StringBuilder log = new StringBuilder();
			String authAccount = ClientConfiguration._getStringValue(name, "http.authAccount", context, null);
			if(authAccount != null && !authAccount.equals("")){
				clientConfiguration.setAuthAccount(authAccount);
			}

//			httpServiceHosts.setAuthAccount(authAccount);
			log.append("http.authAccount=").append(authAccount);

			String authPassword = ClientConfiguration._getStringValue(name, "http.authPassword", context, null);
			if(authPassword != null && !authPassword.equals("")){
				clientConfiguration.setAuthPassword(authPassword);
			}
			if(PropertiesContainer.showPassword)
				log.append(",http.authPassword=").append(authPassword);
			else
				log.append(",http.authPassword=******");

			String encodedAuthCharset = ClientConfiguration._getStringValue(name, "http.encodedAuthCharset", context, "US-ASCII");
			if(encodedAuthCharset != null && !encodedAuthCharset.equals("")){
				clientConfiguration.setEncodedAuthCharset(encodedAuthCharset);
			}
			log.append(",http.encodedAuthCharset=").append(encodedAuthCharset);
			int timeoutConnection = ClientConfiguration._getIntValue(name, "http.timeoutConnection", context, 50000);
			log.append(",http.timeoutConnection=").append(timeoutConnection);
			clientConfiguration.setTimeoutConnection(timeoutConnection);
			int timeoutSocket = ClientConfiguration._getIntValue(name, "http.timeoutSocket", context, 0);
			log.append(",http.timeoutSocket=").append(timeoutSocket);
			clientConfiguration.setTimeoutSocket(timeoutSocket);
			int connectionRequestTimeout = ClientConfiguration._getIntValue(name, "http.connectionRequestTimeout", context, 50000);
			log.append(",http.connectionRequestTimeout=").append(connectionRequestTimeout);
			clientConfiguration.setConnectionRequestTimeout(connectionRequestTimeout);
			int retryTime = ClientConfiguration._getIntValue(name, "http.retryTime", context, -1);
			log.append(",http.retryTime=").append(retryTime);
			clientConfiguration.setRetryTime(retryTime);
			boolean automaticRetriesDisabled = ClientConfiguration._getBooleanValue(name, "http.automaticRetriesDisabled", context, false);
			log.append(",http.automaticRetriesDisabled=").append(automaticRetriesDisabled);
			clientConfiguration.setAutomaticRetriesDisabled(automaticRetriesDisabled);
			boolean backoffAuth = ClientConfiguration._getBooleanValue(name, "http.backoffAuth", context, false);
			log.append(",http.backoffAuth=").append(backoffAuth);
			clientConfiguration.setBackoffAuth(backoffAuth);
			long retryInterval = ClientConfiguration._getLongValue(name, "http.retryInterval", context, -1);
			log.append(",http.retryInterval=").append(retryInterval);
			clientConfiguration.setRetryInterval(retryInterval);
			int maxLineLength = ClientConfiguration._getIntValue(name, "http.maxLineLength", context, -1);
			log.append(",http.maxLineLength=").append(maxLineLength);
			clientConfiguration.setMaxLineLength(maxLineLength);
			int maxHeaderCount = ClientConfiguration._getIntValue(name, "http.maxHeaderCount", context, 500);
			log.append(",http.maxHeaderCount=").append(maxHeaderCount);
			clientConfiguration.setMaxHeaderCount(maxHeaderCount);
			int maxTotal = ClientConfiguration._getIntValue(name, "http.maxTotal", context, 1000);
			log.append(",http.maxTotal=").append(maxTotal);
			clientConfiguration.setMaxTotal(maxTotal);

			boolean soReuseAddress = ClientConfiguration._getBooleanValue(name, "http.soReuseAddress", context, false);
			log.append(",http.soReuseAddress=").append(soReuseAddress);
			clientConfiguration.setSoReuseAddress(soReuseAddress);
			boolean soKeepAlive = ClientConfiguration._getBooleanValue(name, "http.soKeepAlive", context, false);
			log.append(",http.soKeepAlive=").append(soKeepAlive);
			clientConfiguration.setSoKeepAlive(soKeepAlive);
			int timeToLive = ClientConfiguration._getIntValue(name, "http.timeToLive", context, 3600000);
			log.append(",http.timeToLive=").append(timeToLive);
			clientConfiguration.setTimeToLive(timeToLive);
			int keepAlive = ClientConfiguration._getIntValue(name, "http.keepAlive", context, 3600000);
			log.append(",http.keepAlive=").append(keepAlive);
			clientConfiguration.setKeepAlive(keepAlive);

			int defaultMaxPerRoute = ClientConfiguration._getIntValue(name, "http.defaultMaxPerRoute", context, 200);
			log.append(",http.defaultMaxPerRoute=").append(defaultMaxPerRoute);
			clientConfiguration.setDefaultMaxPerRoute(defaultMaxPerRoute);

			int validateAfterInactivity = ClientConfiguration._getIntValue(name, "http.validateAfterInactivity", context, DEFAULT_validateAfterInactivity);
			log.append(",http.validateAfterInactivity=").append(validateAfterInactivity);
			clientConfiguration.setValidateAfterInactivity(validateAfterInactivity);

			boolean staleConnectionCheckEnabled = ClientConfiguration._getBooleanValue(name, "http.staleConnectionCheckEnabled", context, false);
			log.append(",http.staleConnectionCheckEnabled=").append(staleConnectionCheckEnabled);
			clientConfiguration.setStaleConnectionCheckEnabled(staleConnectionCheckEnabled);

			String customHttpRequestRetryHandler = ClientConfiguration._getStringValue(name, "http.customHttpRequestRetryHandler", context, null);
			log.append(",http.customHttpRequestRetryHandler=").append(customHttpRequestRetryHandler);
			clientConfiguration.setCustomHttpRequestRetryHandler(customHttpRequestRetryHandler);

			String keystore = ClientConfiguration._getStringValue(name, "http.keystore", context, null);
			log.append(",http.keystore=").append(keystore);

			clientConfiguration.setKeystore(keystore);
			String keystoreAlias = ClientConfiguration._getStringValue(name, "http.keystoreAlias", context, null);
			log.append(",http.keystoreAlias=").append(keystoreAlias);

			clientConfiguration.setKeystoreAlias(keystoreAlias);

			String keyPassword = ClientConfiguration._getStringValue(name, "http.keyPassword", context, null);
			log.append(",http.keyPassword=").append(keyPassword);
			clientConfiguration.setKeyPassword(keyPassword);

			String truststore = ClientConfiguration._getStringValue(name, "http.truststore", context, null);
			log.append(",http.truststore=").append(truststore);
			clientConfiguration.setTruststore(truststore);
			String truststoreAlias = ClientConfiguration._getStringValue(name, "http.truststoreAlias", context, null);
			log.append(",http.truststoreAlias=").append(truststoreAlias);

			clientConfiguration.setTrustAlias(truststoreAlias);

			String trustPassword = ClientConfiguration._getStringValue(name, "http.trustPassword", context, null);
			log.append(",http.trustPassword=").append(trustPassword);
			clientConfiguration.setTrustPassword(trustPassword);

			/**
			 * 	private String pemCert;
			 * 	private String pemtrustedCA;
			 * 	private String pemKey;
			 * 	private String pemkeyPassword;
			 */

			String pemCert = ClientConfiguration._getStringValue(name, "http.pemCert", context, null);
			log.append(",http.pemCert=").append(pemCert);
			clientConfiguration.setPemCert(pemCert);

			String pemtrustedCA = ClientConfiguration._getStringValue(name, "http.pemtrustedCA", context, null);
			log.append(",http.pemtrustedCA=").append(pemtrustedCA);
			clientConfiguration.setPemtrustedCA(pemtrustedCA);
			String pemKey = ClientConfiguration._getStringValue(name, "http.pemKey", context, null);
			log.append(",http.pemKey=").append(pemKey);

			clientConfiguration.setPemKey(pemKey);

			String pemkeyPassword = ClientConfiguration._getStringValue(name, "http.pemkeyPassword", context, null);
			log.append(",http.pemkeyPassword=").append(pemkeyPassword);
			clientConfiguration.setPemkeyPassword(pemkeyPassword);

			String hostnameVerifier = ClientConfiguration._getStringValue(name, "http.hostnameVerifier", context, null);
			log.append(",http.hostnameVerifier=").append(hostnameVerifier);

			clientConfiguration.setHostnameVerifierString( hostnameVerifier);
			clientConfiguration.setHostnameVerifier(_getHostnameVerifier(hostnameVerifier));

			String supportedProtocols = ClientConfiguration._getStringValue(name, "http.supportedProtocols", context, "TLSv1.2,TLSv1.1,TLSv1");
			log.append(",http.supportedProtocols=").append(supportedProtocols);
			Object httpClientBuilderCallback = ClientConfiguration._getObjectValue(name,"http.httpClientBuilderCallback",context,null);
			log.append(",http.httpClientBuilderCallback=").append(httpClientBuilderCallback);
			if(httpClientBuilderCallback != null){
				clientConfiguration.setHttpClientBuilderCallback(httpClientBuilderCallback);
			}
            Object httpRequestInterceptors = ClientConfiguration._getObjectValue(name,"http.httpRequestInterceptors",context,null);
            log.append(",http.httpRequestInterceptors=").append(httpRequestInterceptors);
            if(httpRequestInterceptors != null){
                clientConfiguration.setHttpRequestInterceptors(httpRequestInterceptors);
            }

            
			clientConfiguration.setSupportedProtocols(supportedProtocols);
			boolean evictExpiredConnections = ClientConfiguration._getBooleanValue(name, "http.evictExpiredConnections", context, true);
			clientConfiguration.setEvictExpiredConnections(evictExpiredConnections);
			log.append(",http.evictExpiredConnections=").append(evictExpiredConnections);
			clientConfiguration.setBeanName(rname(healthPoolname,name));
			HttpServiceHosts httpServiceHosts = null;
			if(healthPoolname == null) {
				httpServiceHosts = new HttpServiceHosts();
				httpServiceHosts.setClientConfiguration(clientConfiguration);


//			httpServiceHosts.setAuthPassword(authPassword);

				String routing = ClientConfiguration._getStringValue(name, "http.routing", context, null);
				log.append(",http.routing=").append(routing);
				httpServiceHosts.setRouting(routing);

				String health = ClientConfiguration._getStringValue(name, "http.health", context, null);
				log.append(",http.health=").append(health);
				httpServiceHosts.setHealth(health);
				Object discoverService = ClientConfiguration._getObjectValue(name, "http.discoverService", context, null);
				log.append(",http.discoverService=").append(discoverService);
				if (discoverService != null) {
					if (discoverService instanceof String)
						httpServiceHosts.setDiscoverService((String) discoverService);
					else if (discoverService instanceof HttpHostDiscover) {
						httpServiceHosts.setHostDiscover((HttpHostDiscover) discoverService);
					}
				}

				Object exceptionWare = ClientConfiguration._getObjectValue(name, "http.exceptionWare", context, null);

				if (exceptionWare != null) {
					if (exceptionWare instanceof String) {
						httpServiceHosts.setExceptionWare((String) exceptionWare);
						log.append(",http.exceptionWare=").append(exceptionWare);
					}
					else if (exceptionWare instanceof ExceptionWare) {
						httpServiceHosts.setExceptionWareBean((ExceptionWare) exceptionWare);
						log.append(",http.exceptionWare=").append(exceptionWare.getClass().getCanonicalName());
					}

				}
				String hosts = ClientConfiguration._getStringValue(name, "http.hosts", context, null);
				log.append(",http.hosts=").append(hosts);
				httpServiceHosts.setHosts(hosts);
				String failAllContinue_ = ClientConfiguration._getStringValue(name, "http.failAllContinue", context, "true");
				log.append(",http.failAllContinue=").append(failAllContinue_);
				if (failAllContinue_ != null) {
					try {
						httpServiceHosts.setFailAllContinue(Boolean.parseBoolean(failAllContinue_));
					}
					catch (Exception e){

					}
				}
				String healthCheckInterval_ = ClientConfiguration._getStringValue(name, "http.healthCheckInterval", context, null);
				log.append(",http.healthCheckInterval=").append(healthCheckInterval_);
				if (healthCheckInterval_ == null) {
					httpServiceHosts.setHealthCheckInterval(3000l);
				} else {
					try {
						httpServiceHosts.setHealthCheckInterval(Long.parseLong(healthCheckInterval_));
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("Parse Long healthCheckInterval parameter failed:" + healthCheckInterval_, e);
						}
					}
				}
				String discoverServiceInterval_ = ClientConfiguration._getStringValue(name, "http.discoverServiceInterval", context, null);
				log.append(",http.discoverServiceInterval=").append(discoverServiceInterval_);
				if (discoverServiceInterval_ == null) {
					httpServiceHosts.setDiscoverServiceInterval(10000l);
				} else {
					try {
						httpServiceHosts.setDiscoverServiceInterval(Long.parseLong(discoverServiceInterval_));
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("Parse Long discoverServiceInterval parameter failed:" + discoverServiceInterval_, e);
						}
					}
				}

				String handleNullOrEmptyHostsByDiscovery_ = ClientConfiguration._getStringValue(name, "http.handleNullOrEmptyHostsByDiscovery", context, null);
				log.append(",http.handleNullOrEmptyHostsByDiscovery=").append(handleNullOrEmptyHostsByDiscovery_);
				if (handleNullOrEmptyHostsByDiscovery_ == null) {
					httpServiceHosts.setHandleNullOrEmptyHostsByDiscovery(false);
				} else {
					try {
						httpServiceHosts.setHandleNullOrEmptyHostsByDiscovery(Boolean.parseBoolean(handleNullOrEmptyHostsByDiscovery_));
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("Parse Boolean handleNullOrEmptyHostsByDiscovery_ parameter failed:" + handleNullOrEmptyHostsByDiscovery_, e);
						}
					}
				}
			}

			if(healthPoolname == null && httpServiceHosts != null) {
				httpServiceHosts.toString(log);

				clientConfiguration.httpServiceHosts = httpServiceHosts;
			}
			if(logger.isInfoEnabled()){
					logger.info("Http Pool[{}] config:{}", rname(healthPoolname,name), log.toString());
			}
			clientConfiguration.afterPropertiesSet();
			//初始化http发现服务组件
			if(healthPoolname == null && httpServiceHosts != null)
				httpServiceHosts.after(name,context);

			clientConfigs.put(rname(healthPoolname,name), clientConfiguration);
			if(resourceStartResult != null)
				resourceStartResult.addResourceStartResult(rname(healthPoolname,name));

		}
		return clientConfiguration;

	}

	public static ResourceStartResult bootClientConfiguations(String[] serverNames, GetProperties context) {
		ResourceStartResult resourceStartResult = new HttpResourceStartResult();
		//初始化Http连接池
		for (String serverName : serverNames) {
			ClientConfiguration.configClientConfiguation(resourceStartResult,null,serverName, context);
		}
		return resourceStartResult;
	}

	public static ClientConfiguration stopHttpClient(String poolName){
		if(poolName == null){
			poolName = "default";
		}
		logger.info("Stop HttpClient[{}] client begin......",poolName);
		String healthPool = getHealthPoolName(poolName);
		boolean r = false;
		ClientConfiguration clientConfiguration = getClientConfigurationAndRemove( poolName);
		if(clientConfiguration != null){
			clientConfiguration.close();
		}
		ClientConfiguration healthclientConfiguration = getClientConfigurationAndRemove( healthPool);
		if(healthclientConfiguration != null){
			healthclientConfiguration.close();
		}
		if(poolName.equals("default"))
			ClientConfiguration.defaultClientConfiguration = null;
		logger.info("Stop HttpClient[{}] client complete.",poolName);
		return clientConfiguration;

	}
	private boolean closed = false;
	public synchronized void close(){
		if(closed)
			return;
		closed = true;
		if(httpServiceHosts != null){
			httpServiceHosts.close();
			httpServiceHosts = null;
		}
		try {
			Thread.sleep(100);
		}
		catch (InterruptedException e){

		}
		if(this.httpclient != null){
			try {
				httpclient.close();
				httpclient = null;
			} catch (IOException e) {
				logger.warn("stop http pool "+ this.getBeanName() + " failed:",e);
			}

		}

	}
	public static ResourceStartResult bootHealthCheckClientConfiguations(String[] serverNames, GetProperties context) {
		ResourceStartResult resourceStartResult = new HttpResourceStartResult();
		//初始化Http连接池
		HealthCheckGetProperties healthCheckGetProperties = new HealthCheckGetProperties(context);
		for (String serverName : serverNames) {
			ClientConfiguration.configClientConfiguation(resourceStartResult,getHealthPoolName(serverName),serverName, healthCheckGetProperties);
		}
		return resourceStartResult;
	}

	private static ClientConfiguration configClientConfiguation(ResourceStartResult resourceStartResult,String healthPoolname,String poolname, GetProperties context) {
//		loadClientConfiguration();
		if (poolname == null || poolname.equals("default"))
			return _getDefaultClientConfiguration(resourceStartResult,healthPoolname,context);
		try {
			return makeDefualtClientConfiguration(resourceStartResult,healthPoolname,poolname, context);
		} catch (Exception e) {
			throw new ConfigHttpRuntimeException("Build ClientConfiguration [" + healthPoolname + poolname + "] failed:", e);
		}
	}
	public static String getHealthPoolName(String httpPool){
		String healthPool = httpPool != null ?ClientConfiguration.http_healthCheck_prex + httpPool:ClientConfiguration.http_healthCheck_prex + "default";
		return healthPool;
	}


	public static ClientConfiguration getClientConfigurationAndRemove(String poolname) {

		if (poolname == null)
			poolname ="default";
		return clientConfigs.remove(poolname);

	}
	public static ClientConfiguration getClientConfigurationOnly(String poolname) {

		if (poolname == null)
			poolname ="default";
		return clientConfigs.get(poolname);

	}
	public static ClientConfiguration getClientConfiguration(String poolname) {
		loadClientConfiguration();
		if (poolname == null)
			return getDefaultClientConfiguration((ResourceStartResult)null);
		try {
			return makeDefualtClientConfiguration((ResourceStartResult)null,poolname);
		} catch (Exception e) {
			throw new ConfigHttpRuntimeException("makeDefualtClientConfiguration [" + poolname + "] failed:", e);
		}
//		ClientConfiguration config = clientConfigs.get(poolname);
//		if(config != null)
//			return config;
//		config = context.getTBeanObject(poolname, ClientConfiguration.class);
//		return config;
	}

	public String getKeystore() {
		return keystore;
	}

	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	public String getSupportedProtocols() {
		return supportedProtocols;
	}

	public void setSupportedProtocols(String supportedProtocols) {
		this.supportedProtocols = supportedProtocols;
	}

	public HostnameVerifier getHostnameVerifier() {
		return hostnameVerifier;
	}

	public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}

	public int getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public Boolean getSoKeepAlive() {
		return soKeepAlive;
	}

	public void setSoKeepAlive(Boolean soKeepAlive) {
		this.soKeepAlive = soKeepAlive;
	}

	public Boolean getSoReuseAddress() {
		return soReuseAddress;
	}

	public void setSoReuseAddress(Boolean soReuseAddress) {
		this.soReuseAddress = soReuseAddress;
	}

	public int getValidateAfterInactivity() {
		return validateAfterInactivity;
	}

	public void setValidateAfterInactivity(int validateAfterInactivity) {
		this.validateAfterInactivity = validateAfterInactivity;
	}

	public long getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(long retryInterval) {
		this.retryInterval = retryInterval;
	}

	public int getTimeoutConnection() {
		return timeoutConnection;
	}

	public void setTimeoutConnection(int timeoutConnection) {
		this.timeoutConnection = timeoutConnection;
	}

	public int getTimeoutSocket() {
		return timeoutSocket;
	}

	public void setTimeoutSocket(int timeoutSocket) {
		this.timeoutSocket = timeoutSocket;
	}

	public int getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(int retryTime) {
		this.retryTime = retryTime;
	}

	private SSLConnectionSocketFactory buildSSLConnectionSocketFactory() throws CertificateException, NoSuchAlgorithmException,
			KeyStoreException, IOException,
			KeyManagementException, UnrecoverableKeyException {
		if(pemCert != null && !pemCert.equals("")){
			if(!pemCert.endsWith(".crt")) {
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						SSLHelper.initSSLConfig("TLS", pemKey, this.pemkeyPassword, pemCert, pemtrustedCA),
						_supportedProtocols,
						null, hostnameVerifier
				);
				return sslsf;
			}
			else{
				Path caCertificatePath = Paths.get(pemCert);
				CertificateFactory factory =
						CertificateFactory.getInstance("X.509");
				java.security.cert.Certificate trustedCa;
				try (InputStream is = Files.newInputStream(caCertificatePath)) {
					trustedCa = factory.generateCertificate(is);
				}
				KeyStore trustStore = KeyStore.getInstance("pkcs12");
				trustStore.load(null, null);
				trustStore.setCertificateEntry("ca", trustedCa);
				SSLContextBuilder sslContextBuilder = SSLContexts.custom()
						.loadTrustMaterial(trustStore, null);
				return new SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);
			}
		}



		// Trust own CA and all self-signed certs
		if (this.keystore == null || this.keystore.equals("")) {
			if(truststore == null || truststore.equals("")) {
				SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
				sslContextBuilder.loadTrustMaterial(null, new TrustStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}
				});
				return new SSLConnectionSocketFactory(sslContextBuilder.build(), NoopHostnameVerifier.INSTANCE);
			}
			else {
				Path trustStorePath = Paths.get(truststore);
				KeyStore keyTruststore = KeyStore.getInstance(truststore.endsWith("p12")?"pkcs12":"JKS");
				try (InputStream is = Files.newInputStream(trustStorePath)) {
					keyTruststore.load(is, (trustPassword == null || trustPassword.length() == 0) ? null:trustPassword.toCharArray());
				}
				SSLContextBuilder sslBuilder = SSLContexts.custom()
						.loadTrustMaterial(keyTruststore, null);
				return new SSLConnectionSocketFactory(sslBuilder.build(), NoopHostnameVerifier.INSTANCE);
			}
		} else {


			HostnameVerifier hostnameVerifier = this.hostnameVerifier != null ? this.hostnameVerifier :
					NoopHostnameVerifier.INSTANCE;
			if(truststore == null || truststore.equals("")) {
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						SSLHelper.initSSLConfig(  keystore,   keyPassword),
						_supportedProtocols,
						null, hostnameVerifier
				);
				return sslsf;
			}
			else{
				if(!truststore.endsWith(".p12")) {
					SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
							SSLHelper.initSSLConfig("TLS", keystore, "JKS", keyPassword, keystoreAlias, truststore, "JKS", trustPassword, trustAlias),
							_supportedProtocols,
							null, hostnameVerifier
					);

					return sslsf;
				}
				else{
					Path trustStorePath = Paths.get(truststore);
					Path keyStorePath = Paths.get(keystore);
					KeyStore trustStore = KeyStore.getInstance("pkcs12");
					KeyStore keyStore = KeyStore.getInstance("pkcs12");
					try (InputStream is = Files.newInputStream(trustStorePath)) {
						trustStore.load(is, (trustPassword == null || trustPassword.length() == 0) ? null:trustPassword.toCharArray());
					}
					try (InputStream is = Files.newInputStream(keyStorePath)) {
						keyStore.load(is, (keyPassword == null || keyPassword.length() == 0) ? null:keyPassword.toCharArray());
					}
					SSLContextBuilder sslBuilder = SSLContexts.custom()
							.loadTrustMaterial(trustStore, null)
							.loadKeyMaterial(keyStore, (keyPassword == null || keyPassword.length() == 0) ? null:keyPassword.toCharArray());
					return new SSLConnectionSocketFactory(sslBuilder.build(), NoopHostnameVerifier.INSTANCE);
				}
			}
		}
	}

	public final CloseableHttpClient getHttpClient() throws Exception {
		if (httpclient != null)
			return httpclient;


		SSLConnectionSocketFactory SSLConnectionSocketFactory = this.buildSSLConnectionSocketFactory();//SSLContexts.createSystemDefault();

		// Create a registry of custom connection socket factories for supported
		// protocol schemes.
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", SSLConnectionSocketFactory)
				.build();


		// Use custom DNS resolver to override the system DNS resolution.
		DnsResolver dnsResolver = new SystemDefaultDnsResolver() {

			@Override
			public InetAddress[] resolve(final String host) throws UnknownHostException {
				if (host.equalsIgnoreCase("localhost")) {
					return new InetAddress[]{InetAddress.getByAddress(new byte[]{127, 0, 0, 1})};
				} else {
					return super.resolve(host);
				}
			}

		};



		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, null, null, dnsResolver,
				this.timeToLive, TimeUnit.MILLISECONDS);

		// Create socket configuration
		SocketConfig socketConfig = SocketConfig.custom()
				.setTcpNoDelay(true)
				.setSoTimeout(timeoutSocket)
				.setSoKeepAlive(this.soKeepAlive)
				.setSoReuseAddress(this.soReuseAddress)
				.build();
		// Configure the connection manager to use socket configuration either
		// by default or for a specific host.
		connManager.setDefaultSocketConfig(socketConfig);
		//	        connManager.setSocketConfig(new HttpHost("localhost", 80), socketConfig);
		// Validate connections after 1 sec of inactivity
		connManager.setValidateAfterInactivity(validateAfterInactivity);

		/**
		// Create message constraints
		MessageConstraints messageConstraints = MessageConstraints.custom()
				.setMaxHeaderCount(this.maxHeaderCount)
				.setMaxLineLength(this.maxLineLength)
				.build();
		// Create connection configuration
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE)
				.setCharset(Consts.UTF_8)
				.setMessageConstraints(messageConstraints)
				.build();

		// Configure the connection manager to use connection configuration either
		// by default or for a specific host.
		connManager.setDefaultConnectionConfig(connectionConfig);
		 */
		//	        connManager.setConnectionConfig(new HttpHost("localhost", 80), ConnectionConfig.DEFAULT);

		// Configure total max or per route limits for persistent connections
		// that can be kept in the pool or leased by the connection manager.
		connManager.setMaxTotal(maxTotal);
		connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
		//	        connManager.setMaxPerRoute(new HttpRoute(new HttpHost("localhost", 80)), 20);

		// Use custom cookie store if necessary.
//		CookieStore cookieStore = new BasicCookieStore();

		// Use custom credentials provider if necessary.

		// Create global request configuration
		RequestConfig requestConfig = RequestConfig.custom()
//				.setCookieSpec(CookieSpecs.DEFAULT)
//				.setExpectContinueEnabled(true)
//				.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
//				.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
				.setConnectTimeout(this.timeoutConnection)
				.setConnectionRequestTimeout(connectionRequestTimeout)
				.setSocketTimeout(this.timeoutSocket)
				.setStaleConnectionCheckEnabled(staleConnectionCheckEnabled)
				.build();

		// Create an HttpClient with the given custom dependencies and configuration.
		HttpClientBuilder builder = HttpClients.custom();

		initCredentialsProvider(  builder );
		if(evictExpiredConnections)
			builder.evictExpiredConnections();
		if (keepAlive > 0)//设置链接保活策略
		{
			HttpConnectionKeepAliveStrategy httpConnectionKeepAliveStrategy = new HttpConnectionKeepAliveStrategy(this.keepAlive);



			builder.setConnectionManager(connManager)
//					.setDefaultCookieStore(cookieStore)

					//.setProxy(new HttpHost("myproxy", 8080))
					.setDefaultRequestConfig(requestConfig).setKeepAliveStrategy(httpConnectionKeepAliveStrategy);


		} else {


			builder.setConnectionManager(connManager)
//					.setDefaultCookieStore(cookieStore)
					//.setProxy(new HttpHost("myproxy", 8080))
					.setDefaultRequestConfig(requestConfig);


		}
		buildRetryHandler(builder);
		customizeHttpBuilder( builder );
		httpclient = builder.build();
		if (this.beanName.equals("default")) {
			defaultRequestConfig = requestConfig;
		}
		clientConfigs.put(beanName, this);
//		ShutdownUtil.addShutdownHook(new Runnable() {
//			@Override
//			public void run() {
//				close();
//			}
//		});
		return httpclient;


	}
	private void customizeHttpBuilder(HttpClientBuilder builder ) throws Exception {
		HttpClientBuilderCallback _httpClientBuilderCallback = null;
		if(this.getHttpClientBuilderCallback() != null){
			if(httpClientBuilderCallback instanceof String) {
				_httpClientBuilderCallback = (HttpClientBuilderCallback) Class.forName((String)this.httpClientBuilderCallback).newInstance();

			}
			else if(httpClientBuilderCallback instanceof HttpClientBuilderCallback) {
				_httpClientBuilderCallback = (HttpClientBuilderCallback) httpClientBuilderCallback;

			}
            _httpClientBuilderCallback.customizeHttpClient(builder,this);
		}
        
        if(SimpleStringUtil.isNotEmpty(this.getHttpRequestInterceptors())){
            String[] tmp = ((String)this.getHttpRequestInterceptors()).split(",");
            HttpClientBuilderCallback httpClientBuilderCallback = new HttpClientBuilderCallback() {
                @Override
                public HttpClientBuilder customizeHttpClient(HttpClientBuilder builder, ClientConfiguration clientConfiguration) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
                    for(String i:tmp){
                        HttpRequestInterceptor httpRequestInterceptor = (HttpRequestInterceptor) Class.forName(i).getDeclaredConstructor().newInstance();
                        builder.addInterceptorLast(httpRequestInterceptor);
                    }
                    return builder;
                }
            };
            httpClientBuilderCallback.customizeHttpClient(builder,this);
        }
		
	}
	private void initCredentialsProvider(HttpClientBuilder builder ){

		if(this.getAuthAccount() != null) {
			if(!this.isBackoffAuth()) {
				CredentialsProvider credentialsProvider = null;
				ClassUtil.ClassInfo classInfo = ClassUtil.getClassInfo(builder.getClass());
				credentialsProvider = (CredentialsProvider) classInfo.getPropertyValue(builder, "credentialsProvider");
				if (credentialsProvider == null) {
					credentialsProvider = new BasicCredentialsProvider();
					credentialsProvider.setCredentials(AuthScope.ANY,
							new UsernamePasswordCredentials(this.getAuthAccount(), this.getAuthPassword()));
					builder.setDefaultCredentialsProvider(credentialsProvider);
				}
			}
			else{
				BasicHeader header =  new BasicHeader("Authorization", getHeader(encodedAuthCharset,this.getAuthAccount(), this.getAuthPassword()));
//				headers.put("Authorization", getHeader(this.getAuthAccount(), this.getAuthPassword()));
				List<Header> headers = new ArrayList<Header>();
				headers.add(header);
				builder.setDefaultHeaders(headers);
			}
		}
	}
	public static String getHeader(String encodedAuthCharset ,String user, String password) {
		String auth = user + ":" + password;
		if(encodedAuthCharset != null && !encodedAuthCharset.equals("")) {
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName(encodedAuthCharset)));
			return "Basic " + new String(encodedAuth);
		}
		else{
			byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
			return "Basic " + new String(encodedAuth);
		}
	}
	private void buildRetryHandler(HttpClientBuilder builder) {
		if(automaticRetriesDisabled) {
			builder.disableAutomaticRetries();
			return ;
		}

		if (getRetryTime() > 0 ) {
			CustomHttpRequestRetryHandler customHttpRequestRetryHandler = null;
			if (this.customHttpRequestRetryHandler != null && !this.customHttpRequestRetryHandler.trim().equals("")) {
				try {
					customHttpRequestRetryHandler = (CustomHttpRequestRetryHandler) Class.forName(this.customHttpRequestRetryHandler).newInstance();
				} catch (Exception e) {
					logger.error("Create CustomHttpRequestRetryHandler[" + this.customHttpRequestRetryHandler + "] failed:", e);
					customHttpRequestRetryHandler = null;
				}
			}
			HttpRequestRetryHandlerHelper httpRequestRetryHandlerHelper = new HttpRequestRetryHandlerHelper(customHttpRequestRetryHandler, this);
			builder.setRetryHandler(httpRequestRetryHandlerHelper);
		}

	}

	public HttpClient _getHttpclient() {
		return httpclient;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getDefaultMaxPerRoute() {
		return defaultMaxPerRoute;
	}

	public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
		this.defaultMaxPerRoute = defaultMaxPerRoute;
	}

	/** (non-Javadoc)
	 * @see org.frameworkset.spi.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.supportedProtocols != null && !this.supportedProtocols.equals("")) {
			this._supportedProtocols = this.supportedProtocols.split(",");
		} else {
			this._supportedProtocols = this.defaultSupportedProtocols;
		}
		this.getHttpClient();

	}

	/** (non-Javadoc)
	 * @see org.frameworkset.spi.BeanNameAware#setBeanName(java.lang.String)
	 */
	@Override
	public void setBeanName(String name) {
		this.beanName = name;

	}

	public RequestConfig getRequestConfig() {
		return requestConfig;
	}

	public int getMaxLineLength() {
		return maxLineLength;
	}

	public void setMaxLineLength(int maxLineLength) {
		this.maxLineLength = maxLineLength;
	}

	public int getMaxHeaderCount() {
		return maxHeaderCount;
	}

	public void setMaxHeaderCount(int maxHeaderCount) {
		this.maxHeaderCount = maxHeaderCount;
	}


	public int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}


	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}


	public long getKeepAlive() {
		return keepAlive;
	}


	public void setKeepAlive(long keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean isStaleConnectionCheckEnabled() {
		return staleConnectionCheckEnabled;
	}

	public void setStaleConnectionCheckEnabled(boolean staleConnectionCheckEnabled) {
		this.staleConnectionCheckEnabled = staleConnectionCheckEnabled;
	}

	public String getCustomHttpRequestRetryHandler() {
		return customHttpRequestRetryHandler;
	}

	public void setCustomHttpRequestRetryHandler(String customHttpRequestRetryHandler) {
		this.customHttpRequestRetryHandler = customHttpRequestRetryHandler;
	}

	public String getHostnameVerifierString() {
		return hostnameVerifierString;
	}

	public void setHostnameVerifierString(String hostnameVerifierString) {
		this.hostnameVerifierString = hostnameVerifierString;
	}



	public GetProperties getContextProperties() {
		return contextProperties;
	}

	public void setContextProperties(GetProperties contextProperties) {
		this.contextProperties = contextProperties;
	}

	public String getTruststore() {
		return truststore;
	}

	public void setTruststore(String truststore) {
		this.truststore = truststore;
	}

	public String getTrustPassword() {
		return trustPassword;
	}

	public void setTrustPassword(String trustPassword) {
		this.trustPassword = trustPassword;
	}

	public String getKeystoreAlias() {
		return keystoreAlias;
	}

	public void setKeystoreAlias(String keystoreAlias) {
		this.keystoreAlias = keystoreAlias;
	}

	public String getTrustAlias() {
		return trustAlias;
	}

	public void setTrustAlias(String trustAlias) {
		this.trustAlias = trustAlias;
	}

	public String getPemCert() {
		return pemCert;
	}

	public void setPemCert(String pemCert) {
		this.pemCert = pemCert;
	}

	public String getPemtrustedCA() {
		return pemtrustedCA;
	}

	public void setPemtrustedCA(String pemtrustedCA) {
		this.pemtrustedCA = pemtrustedCA;
	}

	public String getPemKey() {
		return pemKey;
	}

	public void setPemKey(String pemKey) {
		this.pemKey = pemKey;
	}

	public String getPemkeyPassword() {
		return pemkeyPassword;
	}

	public void setPemkeyPassword(String pemkeyPassword) {
		this.pemkeyPassword = pemkeyPassword;
	}

	public boolean isAutomaticRetriesDisabled() {
		return automaticRetriesDisabled;
	}

	public void setAutomaticRetriesDisabled(boolean automaticRetriesDisabled) {
		this.automaticRetriesDisabled = automaticRetriesDisabled;
	}

	public boolean isBackoffAuth() {
		return backoffAuth;
	}

	public void setBackoffAuth(boolean backoffAuth) {
		this.backoffAuth = backoffAuth;
	}
}
