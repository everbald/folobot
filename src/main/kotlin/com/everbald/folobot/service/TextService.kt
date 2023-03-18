package com.everbald.folobot.service

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Component
class TextService {
    val foloPidorPunch = getText("foloPidorPunch")
    val foloPidorSetup = getText("foloPidorSetup")
    val quotesforAndrew = getText("quotesForAndrew")

    /**
     * Чтение текстов из ресурсов
     * @param textName Имя файла без расширения
     * @return [<] Тексты
     */
    private final fun getText(textName: String): List<String> {
        try {
            BufferedReader(
                InputStreamReader(
                    ClassPathResource("texts\\$textName.txt")
                        .inputStream
                )
            ).use { reader ->
                return reader.lines()
                    .map { line: String -> line.replace("\\n", System.lineSeparator()) }
                    .toList()
            }
        } catch (e: IOException) {
            return ArrayList()
        }
    }

    val setup: String
        get() = foloPidorSetup.random()

    fun getPunch(userName: String): String {
        return String.format(foloPidorPunch.random(), userName)
    }

    val quoteForAndrew: String
        get() = quotesforAndrew.random()
}