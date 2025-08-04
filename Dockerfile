FROM tomcat:11.0.9-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY ./build/libs/payper-server-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080