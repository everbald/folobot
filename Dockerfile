FROM openjdk:18-jdk-alpine as base
WORKDIR /app
COPY /build/libs/folobot-*.jar folobot.jar
ENTRYPOINT ["java","-jar","folobot.jar"]

