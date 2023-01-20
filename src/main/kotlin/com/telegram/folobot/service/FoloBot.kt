package com.telegram.folobot.service

import com.telegram.folobot.IdUtils
import com.telegram.folobot.model.ActionsEnum
import com.telegram.folobot.service.handlers.*
import mu.KLogger
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

@Service
class FoloBot(
    private val ciHandler: ContextIndependentHandler,
    private val commandHandler: CommandHandler,
    private val userMessageHandler: UserMessageHandler,
    private val replyHandler: ReplyHandler,
    private val userJoinHandler: UserJoinHandler,
    private val messageService: MessageService,
    private val userService: UserService,
    private val logger: KLogger = KotlinLogging.logger {}
) : TelegramWebhookBot() {
    @Value("\${bot.username}")
    private val botUsername: String = ""

    @Value("\${bot.token}")
    private val botToken: String = ""

    @Value("\${bot.path}")
    private val botPath: String = ""

    /**
     * Инициализация бота в обработчиках
     */
    init {
        messageService.foloBot = this
        userService.foloBot = this
    }

    override fun getBotToken(): String? {
        return botToken
    }

    override fun getBotUsername(): String? {
        return botUsername
    }

    override fun getBotPath(): String? {
        return botPath
    }

    /**
     * Пришел update от Telegram
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    override fun onWebhookUpdateReceived(update: Update): BotApiMethod<*>? {
        if (update.hasMessage()) {
            //Выполнение независящих от контекста действий
            ciHandler.handle(update)

            //Действие в зависимости от содержимого update
            return onAction(getAction(update), update)
        }
        return null
    }

    /**
     * Определяет действие на основе приходящего Update
     *
     * @param update [Update] пробрасывается из onUpdateReceived
     * @return [ActionsEnum]
     */
    private fun getAction(update: Update): ActionsEnum {
        val message = update.message
        return when {
            // Команда
            message.forwardFrom == null && message.forwardSenderName == null &&
                    message.entities?.firstOrNull { it.type == EntityType.BOTCOMMAND }?.text?.let {
                        message.chat.isUserChat || (!message.chat.isUserChat && it.contains(botUsername))
                    } == true -> ActionsEnum.COMMAND
            // Личное сообщение
            message.isUserMessage -> ActionsEnum.USERMESSAGE
            // Ответ на обращение
            message.hasText() && (message.text.lowercase().contains("гурманыч") ||
                    message.text.lowercase().contains(botUsername.lowercase())) -> ActionsEnum.REPLY
            // Пользователь зашел в чат
            message.newChatMembers.isNotEmpty() -> ActionsEnum.USERNEW
            // Пользователь покинул чат
            Objects.nonNull(message.leftChatMember) -> ActionsEnum.USERLEFT
            // Неопределено
            else -> ActionsEnum.UNDEFINED
        }.also {
            if (it != ActionsEnum.UNDEFINED) logger.info {
                "Received request with action $it in chat ${IdUtils.getChatIdentity(message.chatId)}"
            }
        }
    }

    /**
     * Действие в зависимости от содержимого [Update]
     *
     * @param action [ActionsEnum]
     * @param update пробрасывается из onUpdateReceived
     * @return [BotApiMethod]
     */
    private fun onAction(action: ActionsEnum, update: Update): BotApiMethod<*>? {
        if (action != ActionsEnum.UNDEFINED) {
            return when (action) {
                ActionsEnum.COMMAND -> commandHandler.handle(update)
                ActionsEnum.USERMESSAGE -> userMessageHandler.handle(update)
                ActionsEnum.REPLY -> replyHandler.handle(update)
                ActionsEnum.USERNEW -> userJoinHandler.handleJoin(update)
                ActionsEnum.USERLEFT -> userJoinHandler.handleLeft(update)
                else -> null
            }
        }
        return null
    }
}