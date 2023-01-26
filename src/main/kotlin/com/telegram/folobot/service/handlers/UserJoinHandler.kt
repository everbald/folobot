package com.telegram.folobot.service.handlers

import com.telegram.folobot.IdUtils.Companion.getChatIdentity
import com.telegram.folobot.IdUtils.Companion.isAndrew
import com.telegram.folobot.IdUtils.Companion.isFolochat
import com.telegram.folobot.IdUtils.Companion.isVitalik
import com.telegram.folobot.service.MessageService
import com.telegram.folobot.service.UserService
import com.telegram.folobot.service.getName
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class UserJoinHandler(
    private val messageService: MessageService,
    private val userService: UserService
) : KLogging() {

    /**
     * Пользователь зашел в чат
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handleJoin(update: Update): BotApiMethod<*>? {
        val user = update.message.newChatMembers[0]
        if (isAndrew(user)) {
            return messageService
                .buildMessage("Наконец то ты вернулся, мой сладкий пирожочек Андрюша!", update, true)
        } else if (isVitalik(user)) {
            messageService.sendMessage("Как же я горю сейчас", update)
            messageService.sendMessage("Слово мужчини", update)
        } else if (userService.isSelf(user)) {
            messageService.sendMessage("Привет, с вами я, сильный и незаурядный репер МС Фоломкин.", update)
            messageService.sendMessage("Спасибо, что вы смотрите мои замечательные видеоклипы.", update)
            messageService.sendMessage("Я читаю текст, вы слушаете текст", update)
        } else {
            if (isFolochat(update.message.chat)) {
                return messageService
                    .buildMessage(
                        "Добро пожаловать в замечательный высокоинтеллектуальный фолочат, "
                                + userService.getFoloUserName(user) + "!", update, true
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
        return null
    }

    /**
     * Пользователь покинул чат
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    fun handleLeft(update: Update): BotApiMethod<*> {
        val user = update.message.leftChatMember
        return if (isAndrew(user)) {
            messageService.buildMessage("Сладкая бориспольская булочка покинула чат", update)
        } else {
            messageService
                .buildMessage("Куда же ты, " + userService.getFoloUserName(user) + "! Не уходи!", update)
        }.also { logger.info { "Said goodbye to ${user.getName()} in chat ${getChatIdentity(update.message.chatId)}" } }
    }
}