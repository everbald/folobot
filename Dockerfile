FROM openjdk:19-jdk-alpine as base
RUN apk add --update fontconfig freetype
RUN apk add font-vollkorn
WORKDIR /app
COPY /build/libs/folobot-*.jar folobot.jar
COPY /build/resources/main/ffmpeg/ffmpeg ffmpeg/ffmpeg
RUN chmod +x ffmpeg/ffmpeg
ENTRYPOINT ["java","-jar","folobot.jar"]

