FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY ./arthas/arthas-bin.zip /opts/arthas-bin.zip
ADD ./target/yw-live-user-provider-1.0.0.jar app.jar
ENV JAVA_OPTS=" -server -Xmx512m -Xms512m -Xmn256m"
ENTRYPOINT java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar