package com.telegram.folobot.service

import com.telegram.folobot.utils.FoloId.ANDREWSLEGACY_ID
import com.telegram.folobot.utils.FoloId.FO_LEGACY_ID
import com.telegram.folobot.utils.FoloId.POC_ID
import com.telegram.folobot.extensions.*
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class RegistryService(
    private val foloUserService: FoloUserService,
    private val foloPidorService: FoloPidorService,
    private val messageService: MessageService,
    private val foloIndexService: FoloIndexService,
    private val foloCoinService: FoloCoinService,
    private val messageQueueService: MessageQueueService
) : KLogging() {

    fun register(update: Update) {
        //Добавление фолопользователя в бд
        saveFoloUser(update)
        //Пересылка личных сообщений в спецчат
        forwardPrivate(update)
        //Добавление очков активности
        addActivityPoints(update)
        //Добавить в очередь
        addToMessageQueue(update)
    }

    /**
     * Залоггировать пользователя
     *
     * @param update [Update]
     */
    private fun saveFoloUser(update: Update) {
        val message = update.message
        if (message.isAutomaticForward != true) {
            (message.from ?: message.newChatMembers?.firstOrNull())?.run {
                // Фолопользователь
                foloUserService.save(foloUserService.findById(this.id).setName(this.getName()))
                // И фолопидор
                if (!message.isUserMessage && message.isNotUserJoin()) {
                    foloPidorService.save(foloPidorService.findById(message.chatId, this.id).updateMessagesPerDay())
                }
                logger.trace { "Saved foloUser ${this.getName()}" }
            }
        }
    }

    /**
     * Пересылка личных сообщений
     *
     * @param update [Update]
     */
    private fun forwardPrivate(update: Update) {
        if (update.hasMessage() && update.message.isNotUserJoin()) {
            when {
                update.message.isUserMessage -> POC_ID
                update.message.from.isFo() -> FO_LEGACY_ID
                update.message.from.isAndrew() -> ANDREWSLEGACY_ID
                else -> null
            }?.let {
                messageService.forwardMessage(it, update)
            }.also { logger.addMessageForward(it) }
        }
    }

    private fun addActivityPoints(update: Update) {
        foloIndexService.addActivityPoints(update)
        foloCoinService.addCoinPoints(update)
    }

    private fun addToMessageQueue(update: Update) = messageQueueService.addToQueue(update.message)
}