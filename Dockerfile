FROM openjdk:18-jdk-alpine as base
RUN apk add --update fontconfig freetype
WORKDIR /app
COPY /build/libs/folobot-*.jar folobot.jar
ENTRYPOINT ["java","-jar","folobot.jar"]

