FROM openjdk:19-jdk-alpine as base
RUN apk add --update fontconfig freetype
RUN apk add font-vollkorn
WORKDIR /app
COPY /build/libs/folobot-*.jar folobot.jar
ENTRYPOINT ["java","-jar","folobot.jar"]

