package com.everbald.folobot.service.nlp

import com.everbald.folobot.extensions.removeBotName
import com.textrazor.TextRazor
import com.textrazor.annotations.AnalyzedText
import org.springframework.stereotype.Service

@Service
class TextRazorService(
    private val textRazor: TextRazor
) {
    fun textAnalysis(text: String?): AnalyzedText? = text?.removeBotName()?.let { textRazor.analyze(it) }
}

