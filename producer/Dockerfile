FROM openjdk:8
VOLUME /tmp
EXPOSE 8080
ADD ./target/producer-0.0.1-SNAPSHOT.jar producer.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-Djava.security.egd=file:/dev/./urandom","-jar","/producer.jar"]
