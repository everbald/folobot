package com.everbald.folobot.service

import com.everbald.folobot.extensions.addMessageForward
import com.everbald.folobot.extensions.name
import com.everbald.folobot.extensions.isAndrew
import com.everbald.folobot.extensions.isFo
import com.everbald.folobot.extensions.isNotSuccessfulPayment
import com.everbald.folobot.extensions.isNotUserJoin
import com.everbald.folobot.extensions.isNotUserShared
import com.everbald.folobot.utils.FoloId.ANDREWSLEGACY_ID
import com.everbald.folobot.utils.FoloId.FO_LEGACY_ID
import com.everbald.folobot.utils.FoloId.POC_ID
import com.everbald.folobot.service.folocoin.FoloCoinService
import com.everbald.folobot.service.folocoin.FoloIndexService
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
    private val messageQueueService: MessageQueueService,
    private val foloBailService: FoloBailService,
) : KLogging() {
    fun register(update: Update) {
        if (update.hasMessage()) {
            //Добавление фолопользователя в бд
            saveFoloUser(update)
            //Пересылка личных сообщений в спецчат
            forwardPrivate(update)
            //Добавление очков активности
            addActivityPoints(update)
            //Зарегистрировать слив
            registerBail(update)
            //Добавить в очередь
            addToMessageQueue(update)
        }
    }

    /**
     * Залоггировать пользователя
     *
     * @param update [Update]
     */
    private fun saveFoloUser(update: Update) =
        update.message
            .let { message ->
                if (message.isAutomaticForward != true) {
                    (message.from ?: message.newChatMembers?.firstOrNull())
                        ?.let {
                        // Фолопользователь
                        foloUserService.save(foloUserService.find(it.id).setName(it.name))
                        // И фолопидор
                        if (!message.isUserMessage && message.isNotUserJoin) {
                            foloPidorService.save(
                                foloPidorService.find(message.chatId, it.id).updateMessagesPerDay()
                            )
                        }
                        logger.trace { "Saved foloUser ${it.name}" }
                    }
                }
            }

    /**
     * Пересылка личных сообщений
     *
     * @param update [Update]
     */
    private fun forwardPrivate(update: Update) {
        if (
            update.hasMessage() && update.message.isNotUserJoin &&
            update.message.isNotSuccessfulPayment &&
            update.message.isNotUserShared
        ) {
            when {
                update.message.isUserMessage -> POC_ID
                update.message.from.isFo -> FO_LEGACY_ID
                update.message.from.isAndrew -> ANDREWSLEGACY_ID
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

    private fun registerBail(update: Update) = foloBailService.register(update)
}