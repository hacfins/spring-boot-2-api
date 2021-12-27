# 该镜像需要依赖的基础镜像
FROM openjdk:11
LABEL 说明="edu-api-portal"

# 指定维护者的名字
MAINTAINER langyatudio <15589933912>

# 声明服务运行在8001端口
EXPOSE 8001

ARG JAR_FILE
# 将当前目录下的jar包复制到docker容器的/目录下
ADD ${JAR_FILE} /edu-portal.jar

# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-Duser.timezone=GMT+8","-jar", "/edu-portal.jar", "--spring.profiles.active=pro"]
