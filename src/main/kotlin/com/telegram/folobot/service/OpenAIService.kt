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

    fun smallTalk(update: Update): BotApiMethod<*>? = runBlocking {
        val prompt = update.message.text + if (listOf('.', '!').none { it == update.message.text.last() }) "." else ""
        val completionRequest = CompletionRequest(
            model = ModelId("text-davinci-003"),
            prompt = prompt,
            maxTokens = 1000,
            echo = true
        )
        return@runBlocking openAI.completion(completionRequest).choices.firstOrNull()?.let {
            messageService.buildMessage(it.text.trimMargin().trimIndent(), update, true)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun smallTalkAsync(update: Update): BotApiMethod<*>? {
        val prompt = update.message.text + if (listOf('.', '!', '?').none { it == update.message.text.last() }) "." else ""
        val completionRequest = CompletionRequest(
            model = ModelId("text-davinci-003"),
            prompt = prompt,
            maxTokens = 1000
        )
        GlobalScope.async {
            openAI.completion(completionRequest).choices.firstOrNull()?.let {
                messageService.sendMessage(it.text.trimMargin().trimIndent(), update, true)
            }
        }
        return null
    }

}