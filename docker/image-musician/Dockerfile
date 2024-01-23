FROM openjdk:11-jdk

WORKDIR /app

COPY target/1.0-musician.jar /app/musician.jar

ENTRYPOINT ["java", "-jar", "/app/musician.jar"]