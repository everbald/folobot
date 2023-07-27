package com.everbald.folobot.service.nlp

import com.everbald.folobot.extensions.removeBotName
import com.textrazor.TextRazor
import com.textrazor.annotations.AnalyzedText
import kotlinx.coroutines.*
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class TextRazorService(
    private val textRazor: TextRazor
) : KLogging() {
    fun textAnalysis(text: String?): AnalyzedText? = runBlocking {
        text?.removeBotName()?.let { makeRequest(it) }
    }

    private suspend fun makeRequest(text: String) =
        try {
            textRazor.analyze(text)
        } catch (ex: Exception) {
            logger.warn(ex) { "Error occurred while analysing text" }
            null
        }
}

