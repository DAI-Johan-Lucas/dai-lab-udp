FROM openjdk:21-jdk

WORKDIR /app

COPY ../../target/musician.jar /app/

ENTRYPOINT ["java", "-jar", "/app/musician.jar"]

# docker build -t dai/musician -f docker/image-musician/Dockerfile .
# docker run -d dai/musician piano