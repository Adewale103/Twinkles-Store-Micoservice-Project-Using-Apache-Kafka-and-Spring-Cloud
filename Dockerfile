FROM openjdk:8-jdk-alpine
MAINTAINER microservices-store
EXPOSE 8080
ADD target/microservices-store.jar microservices-store.jar
ENTRYPOINT ["java", "-jar", "/spring-boot-docker.jar"]