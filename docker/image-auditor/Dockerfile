FROM openjdk:11-jdk

WORKDIR /app

COPY target/1.0-auditor.jar /app/auditor.jar

ENTRYPOINT ["java", "-jar", "/app/auditor.jar"]