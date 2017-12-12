FROM openjdk:8-jdk

MAINTAINER Brian Schlining <bschlining@gmail.com>

ENV APP_HOME /opt/java/consumer

RUN mkdir -p ${APP_HOME}

COPY . ${APP_HOME}/

EXPOSE 8080

ENTRYPOINT $APP_HOME/build.sh