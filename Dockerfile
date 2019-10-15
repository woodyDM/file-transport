FROM openjdk:8
MAINTAINER woody09
LABEL app="fileTransport" version="0.0.1" by="woody09"
WORKDIR /app
COPY target/file-transport-jar-with-dependencies.jar fileTransport.jar
EXPOSE 10900
CMD java -jar fileTransport.jar -s 10900
