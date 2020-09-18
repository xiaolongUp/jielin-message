FROM java:8-jre
MAINTAINER rankin qin <rankin.qin@gmail.com>

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

ADD ./target/jielin-message.jar /app/application.jar
CMD ["java", "-Xmx200m", "-jar", "/app/application.jar", "--spring.profiles.active=prod"]

EXPOSE 8887
