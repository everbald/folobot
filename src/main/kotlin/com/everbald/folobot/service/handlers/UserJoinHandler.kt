package com.everbald.folobot.service.handlers

import com.everbald.folobot.extensions.*
import com.everbald.folobot.model.Action
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.UserService
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(6)
class UserJoinHandler(
    private val messageService: MessageService,
    private val userService: UserService
) : Handler, KLogging() {
    override fun canHandle(update: Update): Boolean {
        return (update.hasMessage() && (update.message.isUserJoin() || update.message.isUserLeft())).also {
            if (it) logger.addActionReceived(Action.USERNEW, update.message.chatId)
        }
    }

    override fun handle(update: Update) {
        when {
            update.message.isUserJoin() -> handleJoin(update)
            update.message.isUserLeft() -> handleLeft(update)
        }
    }

    /**
     * Пользователь зашел в чат
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handleJoin(update: Update) {
        val user = update.message.newChatMembers[0]
        if (user.isAndrew()) {
            messageService
                .sendMessage("Наконец то ты вернулся, мой сладкий пирожочек Андрюша!", update, reply = true)
        } else if (user.isVitalik()) {
            messageService.sendMessage("Как же я горю сейчас", update)
            messageService.sendMessage("Слово мужчини", update)
        } else if (userService.isSelf(user)) {
            messageService.sendMessage("Привет, с вами я, сильный и незаурядный репер МС Фоломкин.", update)
            messageService.sendMessage("Спасибо, что вы смотрите мои замечательные видеоклипы.", update)
            messageService.sendMessage("Я читаю текст, вы слушаете текст", update)
        } else {
            if (update.message.chat.isFolochat()) {
                messageService
                    .sendMessage(
                        "Добро пожаловать в замечательный высокоинтеллектуальный фолочат, "
                                + userService.getFoloUserName(user) + "!", update, reply = true
                    )
            } else {
                messageService.sendMessage(
                    "Это не настоящий фолочат, " +
                            userService.getFoloUserName(user) + "!", update
                )
                messageService.sendMessage("настоящий тут: \nt.me/alexfolomkin", update)
            }
        }
        logger.info { "Greeted user ${user.getName()} in chat ${getChatIdentity(update.message.chatId)}" }
    }

    /**
     * Пользователь покинул чат
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handleLeft(update: Update) {
        val user = update.message.leftChatMember
        if (user.isAndrew()) {
            messageService.sendMessage("Сладкая бориспольская булочка покинула чат", update)
        } else {
            messageService
                .sendMessage("Куда же ты, " + userService.getFoloUserName(user) + "! Не уходи!", update)
        }.also { logger.info { "Said goodbye to ${user.getName()} in chat ${getChatIdentity(update.message.chatId)}" } }
    }
}