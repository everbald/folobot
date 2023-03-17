FROM openjdk:18-jdk-alpine as base
RUN apk add --update fontconfig freetype
RUN apk add font-vollkorn
WORKDIR /app
COPY /build/libs/folobot-*.jar folobot.jar
COPY /build/resources/main/ffmpeg/ffmpeg ffmpeg/ffmpeg
ENTRYPOINT ["java","-jar","folobot.jar"]

