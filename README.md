DAI Lab - UDP orchestra
=======================

Authors
----------

Johan Mikami

Lucas Hussain

How to use
------------

Go to the root of the project and run the following command:

```
mvn clean package
```
To build the project.

```
docker build -t dai/auditor -f docker/image-auditor/Dockerfile .
```
To build the image of the auditor.

```
docker build -t dai/musician -f docker/image-musician/Dockerfile .
```
To build the image of the musician.

```
docker run -d -p 2205:2205 dai/auditor
```
To run the auditor.

```
docker run -d dai/musician piano
```
To run a musician.

You can connect (with telnet) to the auditor on the port 2205 to see the list of active musicians.