# FROM fabric8/java-centos-openjdk8-jdk:1.5.1
# FROM java:8
# FROM fis-java-openshift:1.0
FROM registry.redhat.io/fuse7/fuse-java-openshift

MAINTAINER Sergio Platas <splatas@gmail.com>

VOLUME /deployments/config

EXPOSE 8080

# Copy files and install dependencies
ADD ./target/spring-rest-hello-world-1.0.jar /deployments/app.jar

# Para ejecutar:
# 1. docker build . -t claro-mock-service:latest
# 2. docker run -ti claro-mock-service:latest bash

# oc new-build . --docker-image=repo/langimage
# oc new-build . --docker-image=registry.redhat.io/fuse7/fuse-java-openshift