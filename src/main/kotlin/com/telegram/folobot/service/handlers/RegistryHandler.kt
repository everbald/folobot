package com.telegram.folobot.service.handlers

import com.telegram.folobot.FoloId.ANDREWSLEGACY_ID
import com.telegram.folobot.FoloId.FO_LEGACY_ID
import com.telegram.folobot.FoloId.POC_ID
import com.telegram.folobot.extensions.isAndrew
import com.telegram.folobot.extensions.isFo
import com.telegram.folobot.extensions.isNotUserJoin
import com.telegram.folobot.service.*
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class RegistryHandler(
    private val foloUserService: FoloUserService,
    private val foloPidorService: FoloPidorService,
    private val messageService: MessageService,
    private val foloIndexService: FoloIndexService,
    private val foloCoinService: FoloCoinService,
    private val messageQueueService: MessageQueueService
) : Handler, KLogging() {

    override fun handle(update: Update) {
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
            if (update.message.isUserMessage) {
                messageService.forwardMessage(POC_ID, update)
                logger.info { "Forwarded message to POC" }
            } else if (update.message.from.isFo()) {
                messageService.forwardMessage(FO_LEGACY_ID, update)
                logger.info { "Forwarded message to Fo's legacy" }
            } else if (update.message.from.isAndrew()) {
                messageService.forwardMessage(ANDREWSLEGACY_ID, update)
                logger.info { "Forwarded message to Andrews legacy" }
            }
        }
    }

    private fun addActivityPoints(update: Update) {
        foloIndexService.addActivityPoints(update)
        foloCoinService.addCoinPoints(update)
    }

    private fun addToMessageQueue(update: Update) {
        messageQueueService.addToQueue(update.message)
    }
}