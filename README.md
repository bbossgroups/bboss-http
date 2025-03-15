# 版本构建方法

gradle clean publishToMavenLocal

需要通过gradle构建发布版本,gradle安装配置参考文档：

https://esdoc.bbossgroups.com/#/bboss-build

# bboss httpproxy
 http负载均衡组件-HttpRequestProxy  使用参考文档https://esdoc.bbossgroups.com/#/httpproxy

 负载均衡组件特点：

 1.服务负载均衡（目前提供RoundRobin负载算法）

 2.服务健康检查

 3.服务容灾故障恢复

 4.服务自动发现（apollo，nacos，可以扩展到zk，etcd，consul，eureka，db以及其他第三方注册中心）

 5.路由规则动态切换

 5.分组服务管理

 可以配置多组服务集群地址，每一组地址清单支持的配置格式：

 http://ip:port

 https://ip:port

 ip:port（默认http协议）

 多个地址用逗号分隔

 6.服务安全认证（支持basic认证、Kerberos认证）

 7.主备路由/异地灾备特色

 负载均衡器主备功能开发，如果主节点全部挂掉，请求转发到可用的备用节点，如果备用节点也挂了，就抛出异常，如果主节点恢复正常，那么请求重新发往主节点 

# 开发文档

https://esdoc.bbossgroups.com/#/httpproxy

httpproxy 案例：

基于apollo进行配置管理、节点自动发现、路由规则自动切换，源码地址如下

https://gitee.com/bboss/httpproxy-apollo 

https://github.com/bbossgroups/httpproxy-apollo

基于nacos进行配置管理、节点自动发现、路由规则自动切换，源码地址如下

https://gitee.com/bboss/httpproxy-nacos

https://github.com/bbossgroups/httpproxy-nacos

# 联系我们

**技术交流群：21220580,166471282**

<img src="https://esdoc.bbossgroups.com/images/qrcode.jpg"  height="200" width="200"><img src="https://esdoc.bbossgroups.com/images/douyin.png"  height="200" width="200"><img src="https://esdoc.bbossgroups.com/images/wvidio.png"  height="200" width="200">


# 支持我们

如果您正在使用bboss，或是想支持我们继续开发，您可以通过如下方式支持我们：

1.Star并向您的朋友推荐或分享

[bboss elasticsearch client](https://gitee.com/bboss/bboss-elastic)🚀

[数据采集&流批一体化处理](https://gitee.com/bboss/bboss-elastic-tran)🚀

2.通过[爱发电 ](https://afdian.net/a/bbossgroups)直接捐赠，或者扫描下面二维码进行一次性捐款赞助，请作者喝一杯咖啡☕️

<img src="https://esdoc.bbossgroups.com/images/alipay.png"  height="200" width="200">

<img src="https://esdoc.bbossgroups.com/images/wchat.png"   height="200" width="200" />

非常感谢您对开源精神的支持！❤您的捐赠将用于bboss社区建设、QQ群年费、网站云服务器租赁费用。




# License

The BBoss Framework is released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0

# Star History

[![Star History Chart](https://api.star-history.com/svg?repos=bbossgroups/bboss-http&type=Date)](https://star-history.com/#bbossgroups/bboss-http&Date)
