FROM openjdk:21-jdk

WORKDIR /app

COPY ../../target/auditor.jar /app/

ENTRYPOINT ["java", "-jar", "/app/auditor.jar"]

# docker build -t dai/auditor -f docker/image-auditor/Dockerfile .
# docker run -d -p 2205:2205 dai/auditor