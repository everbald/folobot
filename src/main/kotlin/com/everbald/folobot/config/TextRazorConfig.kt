package com.everbald.folobot.config

import com.textrazor.TextRazor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TextRazorConfig() {
    @Value("\${text-razor.token}")
    private val token: String = ""
    @Bean
    fun textRazorClient(): TextRazor =
        TextRazor(token)
            .also {
                it.classifiers = listOf("textrazor_iab_content_taxonomy_3.0")
                it.maxCategories = 3
            }
}