package com.telegram.folobot.service

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.telegram.folobot.IdUtils
import io.ktor.client.network.sockets.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class OpenAIService(
    private val openAI: OpenAI,
    private val messageService: MessageService
): KLogging(){
    fun smallTalk(update: Update) {
        val completionRequest = CompletionRequest(
            model = ModelId("text-davinci-003"),
            prompt = buildPrompt(update.message),
            maxTokens = 2048,
            user = update.message.from.id.toString()
        )
        makeRequest(completionRequest, update)
    }

    private fun buildPrompt(message: Message): String {
        var prompt = message.text.take(
            (Regex("[.!?]").findAll(message.text.take(1000))
                .lastOrNull()?.groups?.first()?.range?.last?.plus(1)) ?: 1000
        )
        prompt += if (listOf('.', '!', '?').none { it == message.text.last() }) "." else ""
        return prompt
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun makeRequest(request: CompletionRequest, update: Update) = GlobalScope.async {
        try {
            openAI.completion(request).choices.firstOrNull()?.let {
                messageService.sendMessage(it.text.trimMargin().trimIndent(), update, true)
            }
            logger.info { "Had a small talk with ${update.message.from.getName()} " +
                    "in chat ${IdUtils.getChatIdentity(update.message.chatId)}" }
        } catch (ex: SocketTimeoutException) {
            logger.warn { "Request to OpenAI API finished with socket timeout" }
        }
    }

}