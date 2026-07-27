# FROM openjdk:21
FROM eclipse-temurin:21-jdk

ADD healtify/target/*.jar application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]