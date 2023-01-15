package com.telegram.folobot.service.handlers

import com.telegram.folobot.IdUtils
import com.telegram.folobot.IdUtils.Companion.isAndrew
import com.telegram.folobot.IdUtils.Companion.isFo
import com.telegram.folobot.service.*
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class ContextIndependentHandler(
    private val foloUserService: FoloUserService,
    private val foloPidorService: FoloPidorService,
    private val messageService: MessageService,
    private val foloIndexService: FoloIndexService,
    private val foloCoinService: FoloCoinService
) : KLogging() {

    fun handle(update: Update) {
        //Добавление фолопользователя в бд
        saveFoloUser(update)

        //Пересылка личных сообщений в спецчат
        forwardPrivate(update)

        //Добавление очков активности
        addActivityPoints(update)
    }

    /**
     * Залоггировать пользователя
     *
     * @param update [Update]
     */
    private fun saveFoloUser(update: Update) {
        val message = update.message
        if (message.isAutomaticForward == null || !message.isAutomaticForward) {
            (message.from ?: message.newChatMembers?.firstOrNull())?.run {
                // Фолопользователь
                foloUserService.save(foloUserService.findById(this.id).setName(this.getName()))
                // И фолопидор
                if (!message.isUserMessage) {
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
        if (update.hasMessage()) {
            if (update.message.isUserMessage) {
                messageService.forwardMessage(IdUtils.POC_ID, update)
                logger.info { "Forwarded message to POC" }
            } else if (isFo(update.message.from)) {
                messageService.forwardMessage(IdUtils.FO_LEGACY_ID, update)
                logger.info { "Forwarded message to Fo's legacy" }
            } else if (isAndrew(update.message.from)) {
                messageService.forwardMessage(IdUtils.ANDREWSLEGACY_ID, update)
//                messageService.substituteMessage(update)
                logger.info { "Forwarded message to Andrews legacy" }
            }
        }
    }

    private fun addActivityPoints(update: Update) {
        foloIndexService.addActivityPoints(update)
        foloCoinService.addCoinPoints(update)
    }
}