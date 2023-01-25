package com.telegram.folobot.service

import com.telegram.folobot.IdUtils.Companion.MESSAGE_QUEUE_ID
import com.telegram.folobot.IdUtils.Companion.getChatIdentity
import com.telegram.folobot.IdUtils.Companion.isLikesToDelete
import com.telegram.folobot.model.dto.MessageQueueDto
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDateTime

@Service
class MessageQueueService(
    private val messageService: MessageService,
    private val userService: UserService
): KLogging() {
    private val messageQueue: MutableList<MessageQueueDto> = mutableListOf()

    fun addToQueue(message: Message) {
        if (isLikesToDelete(message.from)) {
            messageService.silentForwardMessage(MESSAGE_QUEUE_ID, message)?.run {
                messageQueue.add(MessageQueueDto(LocalDateTime.now(), message, this))
            }
        }
    }

    fun restoreMessages() {
        messageQueue.removeIf { it.recievedAt < LocalDateTime.now().minusDays(1) || it.restored }
        messageQueue.forEach {
            if (messageService.checkIfMessageDeleted(it.message)) {
                messageService.silentForwardMessage(it.message.chatId, it.backupMessage)
                messageService.deleteMessage(MESSAGE_QUEUE_ID, it.backupMessage.messageId)
                it.restored = true
                logger.info { "Restored message from ${userService.getFoloUserName(it.message.from)} " +
                        "in chat ${getChatIdentity(it.message.chatId)}" }
            }
        }
    }
}