package com.telegram.folobot.service

import com.telegram.folobot.config.BotCredentialsConfig
import com.telegram.folobot.service.handlers.ActionHandler
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class FoloBot(
    private val actionHandler: ActionHandler,
    private val messageService: MessageService,
    private val userService: UserService,
    private val fileService: FileService,
    private val botCredentials: BotCredentialsConfig
) : TelegramWebhookBot(botCredentials.botToken) {
    /**
     * Инициализация бота в обработчиках
     */
    init {
        messageService.foloBot = this
        userService.foloBot = this
        fileService.foloBot = this
    }

    override fun getBotUsername(): String? {
        return botCredentials.botUsername
    }

    override fun getBotPath(): String? {
        return botCredentials.botPath
    }

    /**
     * Пришел update от Telegram
     *
     * @param update [Update]
     * @return [BotApiMethod]
     */
    override fun onWebhookUpdateReceived(update: Update): BotApiMethod<*>? {
        return actionHandler.handle(update)
    }
}