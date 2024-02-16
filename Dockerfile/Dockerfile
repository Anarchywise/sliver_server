# 使用一个基础的Java 17镜像
FROM openjdk:17-alpine

# 设置工作目录
WORKDIR /app

#VOLUME 指向了一个/tmp的目录，由于 Spring Boot 使用内置的Tomcat容器
#，Tomcat 默认使用/tmp作为工作目录。这个命令的效果是：在宿主机的/var/lib/docker目录下创建一个临时文件并把它链接到容器中的/tmp目录
VOLUME /tmp

# 将本地的 JAR 文件添加到容器中
COPY /sliver_dev_final-0.0.1-SNAPSHOT.jar /app/sliver_dev_final-0.0.1-SNAPSHOT.jar

# 创建 data 目录并复制到容器中
RUN mkdir /app/data
RUN mkdir /app/data/post
RUN mkdir /app/data/post/images
RUN mkdir /app/data/user
RUN mkdir /app/data/user/headPortrait
COPY ../../data /app/data

# 暴露应用的端口（如果有需要）
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "sliver_dev_final-0.0.1-SNAPSHOT.jar"]
