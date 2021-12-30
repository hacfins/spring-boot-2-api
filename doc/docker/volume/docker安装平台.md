安装 CentOS 8.x 系统

# 基础环境

## 安装docker
在系统安装完成之后，使用 xshell 工具远程登录系统或者直接在系统的终端中进行操作
```bash
yum erase podman buildah
yum install -y yum-utils
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
yum install -y containerd.io docker-ce docker-ce-cli
```

依次执行上面四条命令之后，会直接进行 docker 的安装过程。在安装完成之后使用下面的命令打开 docker 的开机启动

```bash
systemctl start docker
systemctl enable docker
```


docker 参考文档 https://yeasy.gitbooks.io/docker_practice/content/



注意：如果旧版本的 Docker 为 docker or docker-engine，需要卸载：

```bash
yum remove docker \
docker-client \
docker-client-latest \
docker-common \
docker-latest \
docker-latest-logrotate \
docker-logrotate \
docker-engine
```



## 安装docker composer 

```bash
#curl -L https://github.com/docker/compose/releases/download/1.27.4/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose

# 国内用户可以使用以下方式加快下载
curl -L https://download.fastgit.org/docker/compose/releases/download/1.27.4/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose

chmod +x /usr/local/bin/docker-compose

#校验是否成功
docker-compose --version
```



## 镜像加速

针对 Docker 客户端版本大于 1.10.0 的用户，可以通过修改daemon配置文件 /etc/docker/daemon.json 来使用加速器
执行如下代码即可：[为了提高镜像下载的速度]

```bash
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
"registry-mirrors": ["https://4uykx8ta.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
```



**阿里云web平台仓库**

地址： https://signin.aliyun.com/hacfin.onaliyun.com/login.htm
用户名： docker@hacfin.onaliyun.com
密码：xxx



# 安装平台

> 磁盘挂载点 - `/mnt/volume`
>
> 如果采用smb、nfs等挂载方式，要确保共享存储的账号拥有该挂载目录的**所有者权限**

## 文件说明

```markdown
├── mnt
│    |     volume
|    |     |    api_admin                    
|    |     |    ├── application.yml
|    |     |    api_portal
|    |     |    ├── application.yml
|    |     |    mysql
|    |     |    |      db
|    |     |    |      ├── xxx.sql
|    |     |    |      conf
|    |     |    |      ├── my.cnf
|    |     |    oss
|    |     |    |      admin_pms
|    |     |    |      portal_imgs
|    |     |    redis
|    |     |    |      conf
|    |     |    |      ├── redis.conf
xxxxxxx
|    |     |    ├── docker-compose-env.yml
|    |     |    ├── docker-compose-app.yml
```



### 复制配置文件

将 `volume` 下的所有文件复制到挂载的目录，如 `/mnt/volume`



### 修改IP

`192.168.123.22` 替换为实际的IP

```
sed -i "s/192.168.123.22/192.168.123.110/g" `grep 192.168.123.22 -rl /mnt/volume`
```



## docker打包

docker 打包并发布到仓库，可以使用私有仓库，也可以使用阿里云等第三方仓库。

[docker 打包参考资料](https://mp.weixin.qq.com/s/3X6vVdWmjmWCyiLm35jpVw)


## 安装依赖服务

执行 docker-compose up 命令即可启动依赖的所有服务

```bash
#修改权限
chmod 777 /mnt/volume/api_admin/logs -R
chmod 777 /mnt/volume/api_portal/logs -R
chmod 777 /mnt/volume/oss -R

chmod 777 /mnt/volume/mysql
chmod 777 /mnt/volume/mysql/logs -R
chmod 777 /mnt/volume/redis

#安装依赖
cd /mnt/volume
docker-compose -f docker-compose-env.yml up -d
```

> mysql、redis 启动时会执行 chown 操作



### 数据库导入

- 将 xxx.sql 文件拷贝到 mysql 容器的 /tmp 目录下

```bash
docker cp /mnt/volume/mysql/db/ mysql:/tmp/
```

- 进入 mysql 容器

```bash
#进入mysql容器
docker exec -it mysql /bin/bash
cd /tmp/db
```

- 导入 db 数据：

```bash
#连接到mysql服务
mysql -uroot -p --default-character-set=utf8
#输入密码 
#testMysql

#创建远程访问用户 test，密码为 testMysql
#CREATE USER 'test'@'%' IDENTIFIED BY 'testMysql';
GRANT ALL PRIVILEGES ON *.* TO 'test'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

#为了客户端的兼容性，更新一下用户的密码
#todo：未来可考虑不这样处理
#use mysql;
#ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'testMysql';  
#FLUSH PRIVILEGES;
 
#导入sql脚本
source /tmp/db/test_edu.sql;
source /tmp/db/test_edu_data.sql;
source /tmp/db/test_edu_division.sql;
source /tmp/db/test_edu_index.sql;
```



## 安装应用服务

执行 docker-compose up 命令即可启动 web 站点服务

```bash
docker-compose -f docker-compose-app.yml up -d
```



### **tips**

停止所有相关容器

```bash
docker-compose -f docker-compose-app.yml stop
```

列出所有容器信息

```bash
docker-compose -f docker-compose-app.yml ps
```



# 常见问题
## 磁盘挂载
查看磁盘（发现新购买的 /dev/vdb 磁盘，或者使用 fdisk -l ）
`ls /dev`
磁盘格式化（G 回车 N 回车：G是建立GPT分区表\N是建立新分区）
`fdisk /dev/vdb`
最后选择 w 保存结果

修改分区格式（ext4）
`mkfs.ext4 /dev/vdb1`
建立挂载的目录（/mnt/volume）
`mkdir /mnt/volume`

开机挂载磁盘
查看分区的磁盘Id号（194e9719-4787-4f88-bd52-1dc2d1b60680）
`ls -l /dev/disk/by-uuid`
修改配置
`vim /etc/fstab`
增加
`UUID=194e9719-4787-4f88-bd52-1dc2d1b60681/mnt/volume/ ext4 defaults 1 1`

重启电脑，查看分区
`df -h`




## CAUTION request is not finished yet
问题原因：
请求的资源可能会被（扩展或其他什么机制）屏蔽掉，比如说 AdBlock、服务器网络监控等阻碍了请求。

解决方法：
找网管



## docker 日志文件过大
日志文件默认位置： /var/lib/docker/containers/容器id号/xx.log
可以通过配置文件 /etc/docker/daemon.json更改日志配置：

```bash
{
"log-driver": "json-file",
"log-opts": {
"max-size": "100m",
"max-file": "10"
}
}
```
需要重建容器才能生效