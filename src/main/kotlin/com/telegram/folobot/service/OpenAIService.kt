package com.telegram.folobot.service

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.telegram.folobot.config.OpenAICredentialsConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class OpenAIService(
    private val openAICredentialsConfig: OpenAICredentialsConfig,
    private val openAI: OpenAI = OpenAI(openAICredentialsConfig.token),
    private val messageService: MessageService
) {
    @OptIn(DelicateCoroutinesApi::class)
    fun smallTalk(update: Update): BotApiMethod<*>? {
        var prompt = update.message.text.take(
            (Regex("[.!?]").findAll(update.message.text.take(1000))
                .lastOrNull()?.groups?.first()?.range?.last?.plus(1)) ?: 1000
        )
        prompt += if (listOf('.', '!', '?').none { it == update.message.text.last() }) "." else ""
        val completionRequest = CompletionRequest(
            model = ModelId("text-davinci-003"),
            prompt = prompt,
            maxTokens = 2048
        )
        GlobalScope.async {
            openAI.completion(completionRequest).choices.firstOrNull()?.let {
                messageService.sendMessage(it.text.trimMargin().trimIndent(), update, true)
            }
        }
        return null
    }

}