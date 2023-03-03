package com.telegram.folobot.service

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
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
    private val messageService: MessageService,
    private val userService: UserService
) : KLogging() {
    fun smallTalk(update: Update) {
        val completionRequest = CompletionRequest(
            model = ModelId("text-davinci-003"),
            prompt = buildPrompt(update.message),
            maxTokens = 2048,
            user = update.message.from.id.toString()
        )
        makeRequest(completionRequest, update)
    }

    @OptIn(BetaOpenAI::class)
    fun smallTalkChat(update: Update) {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "Ты Александр Фоломкин - незаурядный и мегапопулярный репер из бусиново. Отвечай как будто ты он"
                )
            ).plus(flattenMessage(update.message)),
            maxTokens = 2048
        )
        makeRequest(chatCompletionRequest, update)
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
            openAI.completion(request).choices.firstOrNull()?.text?.let {
                messageService.sendMessage(it.trimMargin().trimIndent(), update, true)
            }
            logger.info {
                "Had a small talk with ${update.message.from.getName()} " +
                        "in chat ${IdUtils.getChatIdentity(update.message.chatId)}"
            }
        } catch (ex: SocketTimeoutException) {
            logger.warn { "Request to OpenAI API finished with socket timeout" }
        }
    }

    @OptIn(DelicateCoroutinesApi::class, BetaOpenAI::class)
    private fun makeRequest(request: ChatCompletionRequest, update: Update) = GlobalScope.async {
        try {
            openAI.chatCompletion(request).choices.firstOrNull()?.message?.content?.let {
                messageService.sendMessage(it.trimMargin().trimIndent(), update, true)
            }
            logger.info {
                "Had a small talk with ${update.message.from.getName()} " +
                        "in chat ${IdUtils.getChatIdentity(update.message.chatId)}"
            }
        } catch (ex: SocketTimeoutException) {
            logger.warn { "Request to OpenAI API finished with socket timeout" }
        }
    }

    @OptIn(BetaOpenAI::class)
    private fun flattenMessage(message: Message?): MutableList<ChatMessage> {
        val messages = mutableListOf<ChatMessage>()
        message?.let {
            messages.addAll(flattenMessage(it.replyToMessage))
            if (it.hasText()) messages.add(
                ChatMessage(
                    role = if (userService.isSelf(it.from)) ChatRole.Assistant else ChatRole.User,
                    buildPrompt(it)
                )
            )
        }
        return messages
    }
}