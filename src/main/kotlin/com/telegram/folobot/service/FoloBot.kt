package com.telegram.folobot.service

import com.telegram.folobot.config.BotCredentialsConfig
import com.telegram.folobot.service.handlers.ActionHandler
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.objects.Update

@Service
final class FoloBot(
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
    override fun getBotUsername() = botCredentials.botUsername
    override fun getBotPath() = botCredentials.botPath
    override fun onWebhookUpdateReceived(update: Update) = actionHandler.handle(update)
}