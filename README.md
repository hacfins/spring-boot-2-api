# spring-boot-2-api
spring-boot-2-api framework and integrated a lot of third-party sdk features



## 简介

spring-boot-2-api 可用于后端单应用及多应用的 **API 接口开发框架**，它基于 [SpringBoot2](https://spring.io/projects/spring-boot) + [MyBatis3](http://www.mybatis.org/mybatis-3/zh/index.html) + [Spring Security5](https://spring.io/projects/spring-security) 实现。它使用了最新的后台技术栈，相信不管你的需求是什么，本项目都能帮助到你。

| 技术                 | 说明                | 官网                                                  |
| -------------------- | ------------------- | ----------------------------------------------------- |
| Spring Boot          | 容器+MVC框架        | https://spring.io/projects/spring-boot                |
| Spring Security      | 认证和授权框架      | https://spring.io/projects/spring-security            |
| JWT                  | JWT登录支持         | https://github.com/jwtk/jjwt                          |
| MyBatis              | ORM框架             | http://www.mybatis.org/mybatis-3/zh/index.html        |
| MyBatis Plus         | ORM框架             | https://github.com/baomidou/mybatis-plus              |
| Druid                | 数据库连接池        | https://github.com/alibaba/druid                      |
| Redis                | 分布式缓存          | https://redis.io/                                     |
| MySQL                | MySQL数据库         | https://www.mysql.com/                                |
| Lombok               | 简化对象封装工具    | https://github.com/rzwitserloot/lombok                |
| Hibernator-Validator | 验证框架            | http://hibernate.org/validator                        |
| Hutool               | Java工具类库        | https://github.com/looly/hutool                       |
| fastjson             | Json 工具类库       | https://github.com/alibaba/fastjson                   |
| easyexcel            | excel操作工具库     | https://github.com/alibaba/easyexcel                  |
| ip2region            | 离线IP地址定位库    | https://github.com/lionsoul2014/ip2region             |
| thumbnailator        | 缩略图              | https://github.com/coobird/thumbnailator              |
|                      |                     |                                                       |
| Docker               | 应用容器引擎        | https://www.docker.com                                |
| IDEA                 | 开发IDE             | https://www.jetbrains.com/idea/download               |
| RedisDesktop         | redis客户端连接工具 | https://github.com/qishibo/AnotherRedisDesktopManager |
| SwitchHosts          | 本地host管理        | https://oldj.github.io/SwitchHosts/                   |
| X-shell              | Linux远程连接工具   | http://www.netsarang.com/download/software.html       |
| Navicat              | 数据库连接工具      | http://www.formysql.com/xiazai.html                   |
| PowerDesigner        | 数据库设计工具      | http://powerdesigner.de/                              |
| XMind                | 思维导图设计工具    | https://www.xmind.cn/                                 |
| ProcessOn            | 流程图绘制工具      | https://www.processon.com/                            |
| Postman              | API接口调试工具     | https://www.postman.com/                              |
| Typora               | Markdown编辑器      | https://typora.io/                                    |



## 编码规范

严格按照 [阿里编码规约](https://github.com/alibaba/p3c) 的编码规范

[IDEA 常用插件，强烈推荐！！！](https://langyastudio.github.io/langya-doc/#/./docs/tool/idea/idea-%E6%8F%92%E4%BB%B6%E6%8E%A8%E8%8D%90)



## 模块

```
├── edu-common    -- 工具类及通用代码
├── edu-db        -- 数据库操作代码(可使用MybatisCodeHelperPro)
├── edu-security  -- SpringSecurity封装的公用模块
├── edu-portal    -- 前台系统接口
└── edu-admin     -- 后台系统接口
```



## 功能

### edu-common

```
├── src-xxx                       
│   │   java                     
│   │   │   anno                 注解
│   │   │   config               配置
│   │   │   data                 数据封装
│   │   │   ├── BaseDefine       常量定义类
│   │   │   ├── EC               错误码/错误信息类
│   │   │   ├── PageIn           分页传入参数
│   │   │   ├── pageInfo         分页返回数据
│   │   │   ├── ReturnInfo       返回数据
│   │   │   entity               Bean类与枚举类
│   │   │   exception            异常
│   │   │   ├── ErrController    /error 异常处理
│   │   │   ├── ExceptionHandle  Exception 处理
│   │   │   ├── MyException      自定义异常类
│   │   │   middleware           
│   │   │   |   aop              代理（jsonp、log、速率限制等） 
│   │   │   |   filter           过滤（跨域）
│   │   │   |   Interceptor      拦截（请求拦截、数据脱敏等）
│   │   │   third                第三方API库（OSS、短信、邮件等）
│   │   │   tools                工具集
│   │   resource                
|   |   |   ip2region            ip地址库
│   │   ├── log4j2-spring.xml    日志配置文件
│   │   ├── mybatis-config.xml   mybatis通用配置文件                     
└── pom.xml                      maven 依赖库
```



### edu-db

```
├── src-xxx                      
│   │   java                     
│   │   │   mapper               db操作
│   │   │   model                ORM对象
│   │   resource                 
│   │   │   mapper               mapper xml                     
└── pom.xml                      maven 依赖库
```



### api

```
├── src-xxx                     
│   │   java                     
│   │   │   bean             
│   │   │   │   dto              传入对象Bean
│   │   │   │   vo               返回对象Bean
│   │   │   common       
│   │   │   │   conf             application.yml配置的Bean映射
│   │   │   │   data             数据定义
│   │   │   │   middleware       拦截器等
│   │   │   │   service          第三方服务
│   │   │   config               配置目录（如Redis、Mybatis等）
│   │   │   controller           控制基类
│   │   │   service              逻辑层
│   │   │   │   impl             接口实现
│   │   │   │   base             基础逻辑
│   │   resource                 资源目录 
│   │   │   static               静态文件
│   │   │   templates            view 模板  
│   │   ├── application.yml      配置文件
│   │   ├── application-dev.yml  开发配置文件
│   │   ├── application-pro.yml  线上配置文件               
└── pom.xml                      maven 依赖库
```



## 注意事项

- 使用 `EC` 组织错误码和错误信息
- 非 Controller 层采用抛异常提前结束请求  
- 操作异常等，会通过 `ExceptionHandle` 拦截并给出友好的提示

### Control 服务层

服务层是 API 调用的入口，统一处理 API 应用接入授权、参数校验等

- 使用注解验证请求参数
- 调用业务逻辑层, `ReturnInfo` 方法返回数据
- 一个函数就是一个独立的 API 接口
- 服务层之间不可相互调用


### Service 业务逻辑层

- 一个函数完成一个独立的业务逻辑处理
- 内部相关资源权限的授权检测
- 调用模型层，完成业务的逻辑处理
- 不直接参与数据库的直接操作
- 独立的业务逻辑层之间不可相互调用
- 通过全局的抛异常返回错误信息


### mapper 模型层

- 调用数据库，完成数据的读写操作
- 单条信息有查询缓存，并且在更新、删除操作时同步缓存信息
- 不进行权限的授权检测（统一由业务逻辑层处理）
- 数据库操作的事务处理交由业务逻辑层处理
- 无法通过返回值处理的可直接抛出异常，不可捕获异常
- 独立的模型层之间不可相互调用

> 更新的最佳实践原则是：如果需要使用模型事件，那么就先查询后更新



## 打包与发布

### 打包

```shell
mvnw clean package  -Dmaven.test.skip=true
```



### 运行

控制台运行 jar 包
```shell
java -jar edu-portal/target/edu-portal-x.x.x.jar --server.port=8001
java -jar edu-admin/target/edu-admin-x.x.x.jar --server.port=8002
```
> 只要控制台关闭，服务就不能访问了


后台运行 jar 包:
```shell
nohup java -jar xxx.jar &
```


启动的时候选择读取不同的配置文件
```shell
java -jar xxx.jar --spring.profiles.active=dev
```


启动的时候设置 jvm 参数
```shell
java -Xms10m -Xmx80m -jar xxx.jar &
```



### docker 发布

```shell
cd edu-admin 
..\mvnw dockerfile:build
..\mvnw dockerfile:push

cd edu-portal
..\mvnw dockerfile:build
..\mvnw dockerfile:push
```



**注意**

基于 windows 推送 docker 镜像，需要安装 [docker desktop](https://www.docker.com/get-started)

![image-20210625101440009](https://img-note.langyastudio.com/20210625101440.png?x-oss-process=style/watermark)



