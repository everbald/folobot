FROM openjdk:18-jdk-alpine as base
RUN apk add font-vollkorn
WORKDIR /app
COPY /build/libs/folobot-*.jar folobot.jar
ENTRYPOINT ["java","-jar","folobot.jar"]

