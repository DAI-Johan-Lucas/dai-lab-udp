FROM openjdk:21-jdk

WORKDIR /app

COPY ../../target/1.0-auditor.jar /app/

ENTRYPOINT ["java", "-jar", "/app/1.0-auditor.jar"]

# docker build -t dai/auditor -f docker/image-auditor/Dockerfile .