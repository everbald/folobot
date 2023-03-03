package com.telegram.folobot.service.handlers

import com.telegram.folobot.IdUtils
import com.telegram.folobot.config.BotCredentialsConfig
import com.telegram.folobot.isNotForward
import com.telegram.folobot.isUserJoin
import com.telegram.folobot.isUserLeft
import com.telegram.folobot.model.ActionsEnum
import com.telegram.folobot.service.UserService
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class ActionHandler(
    private val botCredentials: BotCredentialsConfig,
    private val commandHandler: CommandHandler,
    private val userMessageHandler: UserMessageHandler,
    private val replyHandler: ReplyHandler,
    private val userJoinHandler: UserJoinHandler,
    private val registryHandler: RegistryHandler,
    private val smallTalkHandler: SmallTalkHandler,
    private val userService: UserService
) : KLogging() {

    fun handle(update: Update): BotApiMethod<*>? {
        if (update.hasMessage()) {
            //Выполнение независящих от контекста действий
            registryHandler.handle(update)

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
            message.isNotForward() &&
                    message.entities?.firstOrNull { it.type == EntityType.BOTCOMMAND }?.text?.let {
                        message.chat.isUserChat || (!message.chat.isUserChat && it.contains(botCredentials.botUsername))
                    } == true -> ActionsEnum.COMMAND
            // Личное сообщение
            message.isUserMessage -> ActionsEnum.USERMESSAGE
            // Ответ на обращение
            message.isNotForward() && message.hasText() &&
                    (message.text.lowercase().contains("гурманыч") &&
                            message.text.lowercase().contains("привет")) -> ActionsEnum.REPLY
            // Беседа
            (message.hasText() && (IdUtils.isFromFoloSwarm(update) ||
                    message.text.lowercase().contains("гурманыч"))) ||
                    userService.isSelf(message.replyToMessage.from) -> ActionsEnum.SMALLTALK
            // Пользователь зашел в чат
            message.isUserJoin() -> ActionsEnum.USERNEW
            // Пользователь покинул чат
            message.isUserLeft() -> ActionsEnum.USERLEFT
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
                ActionsEnum.SMALLTALK -> smallTalkHandler.handle(update)
                else -> null
            }
        }
        return null
    }
}