FROM jenkins/jenkins:lts-jdk17

USER root

# Installer Docker et Maven
RUN apt-get update && \
    apt-get install -y docker.io maven && \
    rm -rf /var/lib/apt/lists/*

USER jenkins
