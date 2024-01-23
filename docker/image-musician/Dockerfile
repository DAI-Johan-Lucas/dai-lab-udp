FROM openjdk:21-jdk

WORKDIR /app

COPY ../../target/1.0-musician.jar /app/

ENTRYPOINT ["java", "-jar", "/app/1.0-musician.jar"]

# docker build -t dai/musician -f docker/image-musician/Dockerfile .