FROM openjdk:17-jdk-alpine
VOLUME /tmp
ADD ./target/yw-live-user-provider-1.0.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]