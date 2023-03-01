FROM openjdk:18-jdk-alpine as base
RUN apt-get update -y && apt-get install -y libfreetype6
WORKDIR /app
COPY /build/libs/folobot-*.jar folobot.jar
ENTRYPOINT ["java","-jar","folobot.jar"]

