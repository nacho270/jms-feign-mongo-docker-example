FROM openjdk:8
VOLUME /tmp
EXPOSE 8080
ADD ./target/consumer-0.0.1-SNAPSHOT.jar consumer.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-Djava.security.egd=file:/dev/./urandom","-jar","/consumer.jar"]
