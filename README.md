## summary

### day1/spring_cloud_demo01

Emulating microservices by creating 2 Spring boot web apps and use RestTemplate to communicate.

通过RestTemplate调用模拟微服务

对应的视频: 

- 10-模拟微服务环境-上
- 11-模拟微服务环境-下
- 12-模拟微服务环境：通过RestTemplate调用远程服务

### spring_cloud_demo02

加入EurekaServer, 通过discoveryClient获取微服务的调用路径, 仍用restTemplate调用

对应的视频: 

- 16-eureka：搭建EurekaServer注册中心
- 17-eureka：将服务注册到注册中心
- 18-eureka：eureka中通过元数据获取微服务的调用路径

## EurekaServer的高可用

​	add these in hosts

```
# for local coding projects
127.0.0.1 peer-1-server.com
127.0.0.1 peer-2-server.com
127.0.0.1 peer-3-server.com
```



1. 准备2个EurekaServer, 需要相互注册
   1. 详见`day1/spring_cloud_demo03/eureka_server/src/main/resources/application.yml`
   2. 注册后, 它们也会互相同步注册信息
2. 需要将微服务注册到2个Eureka server上
   1. `eureka.client.service-url.defaultZone=http://localhost:9000/eureka, http://localhost:8000/eureka`

### spring_cloud_demo03

基于spring_cloud_demo02上修改的Eureka高可用 

EurekaServer的application.yml有2个profile, 需要2个Run Configuration来启动

对应的视频:

- 20-eurekaServer高可用：server间的相互注册
- 21-eurekaServer高可用：服务注册到多个eurekaserver
- 22-eurekaServer高可用：显示IP与服务续约时间设置
- 23-eurekaServer高可用：自我保护机制

## eureka 细节问题

### 在控制台显示服务IP

```
eureka:
	instance:
		prefer-ip-address: true
		instance-id: ${spring.cloud.client.ip-address}-${server.port} #向注册中心注册服务id
```

### Eureka 的服务的剔除问题

client每30s向server发送一次心跳

如果90s内没有发送心跳, 宕机

在服务的提供者, 设置心跳间隔, 设置续约到期时间

```yaml
eureka:
  instance:
#    发送心跳间隔
    lease-renewal-interval-in-seconds: 5
#    续约到期的时间
    lease-expiration-duration-in-seconds: 10
```



### eureka的自我保护机制

在eureka中配置关闭自我保护和提出服务间隔

```yaml
eureka:
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 4000
```

## Ribbon

### 1 服务调用

eureka内部继承了ribbon

- 在创建RestTemplate的时候, 声明@LoadBalanced
- 使用restTemplate调用远程微服务: 不需要拼接微服务的url, 以待请求的服务名替换IP地址


### 2 负载均衡

Ribbon是一个典型的**客户端负载均衡**器, Ribbon会获取服务的所有地址, 根据内部的负载均衡算法, 获取本次请求的有效地址
准备2个商品微服务(9001, 9011)
在订单系统中远程以负载均衡的形式调用商品服务
注意要启动2个product service(9001和9011)

### 3 重试机制

注意我把heart beat等信息都恢复默认了, 这样停掉的product service仍然会在eureka上保留注册一段时间

问题呈现

1. 恢复轮询模式
2. 用order访问product, 当下一个要访问9011时, 停掉9011
3. 访问9011
4. 访问出错

使用重试机制

1. order service引入spring的重试组件

2. 对ribbon进行重试配置

   ```yaml
   spring:
     cloud:
       loadbalancer:
         retry:
           enabled: true
   #全局debug
   logging:
     level:
       root: debug
   #    修改ribbon的负载均衡策略 服务名 - ribbon - NFLoadBalancerRuleClassName: 策略
   service-product:
     ribbon:
     #    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
       ConnectTimeout: 2500 # Ribbon的连接超时时间, 我测试不生效
       ReadTimeout: 1000 # Ribbon的数据读取超时时间, 我测试不生效
       OkToRetryOnAllOperations: true # 是否对所有操作都进行重试
       MaxAutoRetriesNextServer: 1 # 切换实例的重试次数
       MaxAutoRetries: 0 # 对当前实例的重试次数
   ```

3. 用order访问product, 当下一个要访问9011时, 停掉9011

4. 访问9011

5. 返回9001的响应

## Consul

### 安装启动

进入consul的安装目录

```bash
# 以开发者模式快速启动
./consul agent -dev -client='0.0.0.0'
```

进入到管理后台界面

`http://ip:8500`

### 入门案例

- 提供一个商品微服务
- 提供一个订单系统

将微服务注册到注册中心consul

服务的消费者从consul拉取所有的服务列表

#### 1. 将微服务注册到注册中心consul

### 2. 服务的消费者从consul中拉取所有的服务列表

### Consul集群

agent: 启动一个consule的守护进程

dev: 开发者模式

client: 是consul代理, 和consul server交互

​	一个微服务对应一个client

微服务和client部署到一台机器上

server: 真正干活的consul服务

推荐3-5个

### Gossip: 流言协议

所有的consul都会参与到gossip协议中多节点中数据复制()

### Raft协议

保证server集群的数据一致

Leader: 是server集群中唯一处理客户端请求的

Follower: 选民, 被动接受数据

候选人: 可以被选举为leader

![](https://img.raiden.live/images/2021/03/19/1569470458348._compressed.png)

### 安装consul

```bash
sudo apt-get update -y
apt-get install unzip gnupg2 curl wget -y
wget https://releases.hashicorp.com/consul/1.9.4/consul_1.9.4_linux_amd64.zip
unzip consul_1.9.4_linux_amd64.zip
sudo mv consul /usr/local/bin/
consul --version


```

> https://www.atlantic.net/vps-hosting/how-to-install-consul-server-on-ubuntu-20-04/

### consul 集群

1. 准备3台局域网linux服务器

2. 开启8500等端口或关闭防火墙

3. 安装consul

4. 运行3台consul服务器

   ```bash
   sudo consul agent -server -bootstrap-expect 3 -data-dir /etc/consul.d -node=server-1 -bind=192.168.51.177 -ui -client 0.0.0.0 &
   sudo consul agent -server -bootstrap-expect 3 -data-dir /etc/consul.d -node=server-2 -bind=192.168.51.146 -ui -client 0.0.0.0 &
   sudo consul agent -server -bootstrap-expect 3 -data-dir /etc/consul.d -node=server-3 -bind=192.168.51.133 -ui -client 0.0.0.0 &
   ```

   -server 以server身份启动

   -bootstrap-expect 3 集群要求的最少server数量, 当低于这个数量, 集群即失效

   -node节点id, 在同一集群不能重复

   -bind监听的ip地址

   -ui 表示提供web ui

   -client 客户端的ip地址 0.0.0.0表示运行任何ip访问

   & 表示后台运行

5. 运行本地consul client

   ```powershell
   cd D:\app\consul_1.9.4
   ./consul agent -client='0.0.0.0' -bind='192.168.51.104' -data-dir /etc/consul.d -node=client-1
   ```

6. 这4台机器是互相独立的, 在其它机器上运行`consul join 192.168.51.177`, 把它们加入server-1的集群

7. 查看成员 `consul members`, 会看到所有的成员

   ```
   Node      Address              Status  Type    Build  Protocol  DC   Segment
   server-1  192.168.51.177:8301  alive   server  1.9.4  2         dc1  <all>
   server-2  192.168.51.146:8301  alive   server  1.9.4  2         dc1  <all>
   server-3  192.168.51.133:8301  alive   server  1.9.4  2         dc1  <all>
   client-1  192.168.51.104:8301  alive   client  1.9.4  2         dc1  <default>
   ```

   

8. 启动product service

9. 启动product后, 启动order service

10. 在web可以查看节点: http://192.168.51.177:8500/ui/dc1/nodes, 其它服务器也是完全一样的内容

11. client里面有2个service, product和order

12. 关闭consule 

    `consul leave`

### 报错

端口没有开启报错, 可以关闭防火墙

报错`Error leaving: Put "http://127.0.0.1:8500/v1/agent/leave": dial tcp 127.0.0.1:8500: connect: connection refused`

### Consul常见问题

#### 节点和服务注销

Consul不会自动剔除失效的服务或节点, 但我们可以使用http api的方式处理

#### 健康检查及故障转移

在集群环境下，健康检查是由服务注册到的Agent来处理的，那么如果这个Agent挂掉了，那么此节点
的健康检查就处于无人管理的状态。

从实际应用看，节点上的服务可能既要被发现，又要发现别的服务，如果节点挂掉了，仅提供被发现的
功能实际上服务还是不可用的。当然发现别的服务也可以不使用本机节点，可以通过访问一个Nginx实
现的若干Consul节点的负载均衡来实现。  


