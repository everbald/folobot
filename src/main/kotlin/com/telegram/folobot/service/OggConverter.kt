package com.telegram.folobot.service

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException

@Component
class OggConverter(@Value("\${ffmpeg.path}") ffmpegPath: String) {
    private val ffmpeg: FFmpeg

    init {
        val ffmpegFile = ClassPathResource(ffmpegPath).file
        ffmpeg = FFmpeg(ffmpegFile.path)
    }

    @Throws(IOException::class)
    fun convertOggToMp3(inputPath: String?, targetPath: String?) {
        val builder = FFmpegBuilder()
            .setInput(inputPath)
            .overrideOutputFiles(true)
            .addOutput(targetPath)
            .setAudioCodec("libmp3lame")
            .setAudioBitRate(32768)
            .done()
        val executor = FFmpegExecutor(ffmpeg)
        executor.createJob(builder).run()
        try {
            executor.createTwoPassJob(builder).run()
        } catch (ignored: IllegalArgumentException) {
        }
    }
}