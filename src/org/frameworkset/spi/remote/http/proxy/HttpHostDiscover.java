package org.frameworkset.spi.remote.http.proxy;


import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.assemble.GetProperties;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动发现es 主机节点
 */
public abstract class HttpHostDiscover extends Thread{
	private static Logger logger = LoggerFactory.getLogger(HttpHostDiscover.class);
	private long discoverInterval  = 10000l;
	private HttpServiceHosts httpServiceHosts;
	public HttpHostDiscover( ){
		super("Http HostDiscover Thread");
		BaseApplicationContext.addShutdownHook(new Runnable() {
			@Override
			public void run() {
				stopCheck();
			}
		});
	}
	boolean stop = false;
	public void stopCheck(){
		this.stop = true;
		this.interrupt();
	}

	/**
	 * 主动定时服务发现和被动服务发现api
	 * @param hosts
	 */
	public synchronized void handleDiscoverHosts(List<HttpHost> hosts){

		List<HttpAddress> newAddress = new ArrayList<HttpAddress>();
		//恢复移除节点
		boolean changed = httpServiceHosts.recoverRemovedNodes(hosts);
		HttpHost httpHost = null;
		String health = httpServiceHosts.getHttpServiceHostsConfig().getHealth();
		//识别新增节点
		for(int i = 0; hosts !=null && i < hosts.size();i ++){
			httpHost = hosts.get(i);
			HttpAddress address = new HttpAddress(httpHost.getOrigineAddress(),
					                              httpHost.getHostAddress(),
					                              httpHost.getRouting(),health);
//			if(address.getRouting() == null || address.getRouting().equals(""))
//				address.setRouting(httpHost.getRouting());
			address.setAttributes(httpHost.getAttributes());
			if(!httpServiceHosts.containAddress(address)){
				newAddress.add(address);
			}
		}
		//处理新增节点
		if(newAddress.size() > 0) {
			if(!changed )
				changed = true;
			if (logger.isInfoEnabled()) {
				logger.info(new StringBuilder().append("Discovery new Http pool[")
						.append(httpServiceHosts.getClientConfiguration().getBeanName()).append("] servers ").append(newAddress).append(".").toString());
			}
			httpServiceHosts.addAddresses(newAddress);
		}
		//处理删除节点
		httpServiceHosts.handleRemoved( hosts);
		//如果数据有变化，则根据routing规则对地址进行重新分组
		if(changed) {
			httpServiceHosts.routingGroup();
		}
	}
	protected abstract List<HttpHost> discover(HttpServiceHostsConfig httpServiceHostsConfig, ClientConfiguration configuration, GetProperties context);
	@Override
	public void run() {
		do {
			if(this.stop)
				break;
			doDiscover();
			try {
				sleep(discoverInterval);
			} catch (InterruptedException e) {
				break;
			}
		}while(true);

	}

	private void doDiscover(){
		try {
//				clientInterface.discover("_nodes/http",ClientInterface.HTTP_GET, new ResponseHandler<Void>() {
//
//					@Override
//					public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
//						int status = response.getStatusLine().getStatusCode();
//						if (status >= 200 && status < 300) {
//							List<HttpHost> hosts = readHosts(response.getEntity());
//							handleDiscoverHosts(hosts);
//
//						} else {
//
//						}
//						return null;
//					}
//				});
			if (logger.isDebugEnabled())
				logger.debug(new StringBuilder().append("Discovery Http pool[")
						.append(httpServiceHosts.getClientConfiguration().getBeanName()).append("] servers.").toString());
			List<HttpHost> httpHosts = discover(httpServiceHosts.getHttpServiceHostsConfig(), httpServiceHosts.getClientConfiguration(), httpServiceHosts.getClientConfiguration().getContextProperties());
			handleDiscoverHosts( httpHosts);
		} catch (Exception e) {
			if (logger.isInfoEnabled())
				logger.info(new StringBuilder().append("Discovery ")
						.append(httpServiceHosts.getClientConfiguration().getBeanName()).append(" servers failed:").toString(),e);
		}
	}

	public void setHttpServiceHosts(HttpServiceHosts httpServiceHosts) {
		this.httpServiceHosts = httpServiceHosts;
		String threadName = "Http pool["+httpServiceHosts.getClientConfiguration().getBeanName()+"] HostDiscover Thread";
		this.setName(threadName);
		if(logger.isInfoEnabled())
			logger.info("HttpServiceHosts dicover thread:"+threadName);

	}
	public void start(){
		//do server discover first，then start the thread.
		if (logger.isInfoEnabled())
			logger.info("First doDiscovery Http pool["+httpServiceHosts.getClientConfiguration().getBeanName()+"] servers at start time.");
		this.doDiscover();
		super.start();

	}
}
