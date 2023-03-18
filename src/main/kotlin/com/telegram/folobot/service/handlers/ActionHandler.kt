package com.telegram.folobot.service.handlers

import com.telegram.folobot.config.BotCredentialsConfig
import com.telegram.folobot.extensions.*
import com.telegram.folobot.model.ActionsEnum
import com.telegram.folobot.service.MessageQueueService
import com.telegram.folobot.service.UserService
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.EntityType
import org.telegram.telegrambots.meta.api.objects.Message
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
    private val userService: UserService,
    private val messageQueueService: MessageQueueService,
    private val chatCommandHandler: ChatCommandHandler,
    private val transcriptionHandler: TranscriptionHandler
) : Handler, KLogging() {
    override fun handle(update: Update): BotApiMethod<*>? {
        if (update.hasMessage()) {
            //Выполнение независящих от контекста действий
            registryHandler.handle(update)

            //Действие в зависимости от содержимого update
            return if (messageQueueService.checkFirstInMediaGroup(update.message?.mediaGroupId))
                onAction(getAction(update), update)
            else null
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
            message.isMyCommand() -> ActionsEnum.COMMAND
            // Команда в чате
            message.isChatCommand() -> ActionsEnum.CHATCOMMAND
            // Конвертация в аудио
            message.isTranscribe() -> ActionsEnum.TRANSCRIPTION
            // Личное сообщение
            message.isUserMessage -> ActionsEnum.SMALLTALK //ActionsEnum.USERMESSAGE
            // Ответ на обращение
            message.isGreetMe() -> ActionsEnum.REPLY
            // Беседа
            message.isSmallTalk() -> ActionsEnum.SMALLTALK
            // Пользователь зашел в чат
            message.isUserJoin() -> ActionsEnum.USERNEW
            // Пользователь покинул чат
            message.isUserLeft() -> ActionsEnum.USERLEFT
            // Неопределено
            else -> ActionsEnum.UNDEFINED
        }.also {
            if (it != ActionsEnum.UNDEFINED) logger.info {
                "Received request with action $it in chat ${getChatIdentity(message.chatId)}"
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
                ActionsEnum.CHATCOMMAND -> chatCommandHandler.handle(update)
                ActionsEnum.TRANSCRIPTION -> transcriptionHandler.handle(update)
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

    fun Message.isMyCommand() =
        this.isCommand && this.isNotForward() &&
                (this.chat.isUserChat ||
                        this.entities.firstOrNull { it.type == EntityType.BOTCOMMAND }?.text
                            ?.contains(botCredentials.botUsername) == true)

    fun Message.isChatCommand() = chatCommandHandler.isChatCommand(this)

    fun Message.isGreetMe() = this.isNotForward() && this.isAboutBot() &&
            this.text?.contains("привет", ignoreCase = true) == true

    fun Message.isSmallTalk() = userService.isSelf(this.replyToMessage?.from) || this.isFromFoloSwarm()

    fun Message.isTranscribe() = this.hasVoice() || this.hasVideoNote()
}