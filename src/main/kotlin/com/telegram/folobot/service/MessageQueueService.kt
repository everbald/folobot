package com.telegram.folobot.service

import com.telegram.folobot.FoloId.MESSAGE_QUEUE_ID
import com.telegram.folobot.FoloId.POC_ID
import com.telegram.folobot.extensions.getChatIdentity
import com.telegram.folobot.extensions.isFolochat
import com.telegram.folobot.extensions.isLikesToDelete
import com.telegram.folobot.extensions.isNotUserJoin
import com.telegram.folobot.model.dto.MessageQueueDto
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.LocalDateTime

@Service
class MessageQueueService(
    private val messageService: MessageService,
    private val userService: UserService
) : KLogging() {
    private val messageQueue: MutableList<MessageQueueDto> = mutableListOf()
    val messageStack: MutableList<MessageQueueDto> = mutableListOf()

    fun addToQueue(message: Message) {
        if (message.isNotUserJoin() && !message.isCommand) {
            messageQueue.add(
                MessageQueueDto(
                    LocalDateTime.now(),
                    message,
                    if (message.chat.isFolochat() && message.from.isLikesToDelete()) messageService.silentForwardMessage(
                        MESSAGE_QUEUE_ID,
                        message
                    ) else null
                )
            )
        }
    }

    fun checkFirstInMediaGroup(mediaGroup: String?) =
        mediaGroup == null || messageStack.plus(messageQueue).count { it.message.mediaGroupId == mediaGroup } == 1

    fun sendAndAddToQueue(text: String, update: Update, parseMode: String = ParseMode.HTML, reply: Boolean) {
        messageService.sendMessage(text, update, parseMode, reply)?.let {
            messageQueue.add(MessageQueueDto(LocalDateTime.now(), it))
            if (update.message.isUserMessage) messageService.forwardMessage(POC_ID, it)
        }
    }


    fun restoreMessages() {
        messageStack.addAll(messageQueue)
        messageQueue.clear()
        messageStack.removeIf { it.recievedAt < LocalDateTime.now().minusDays(1) || it.restored }

        messageStack.filter { it.message.chat.isFolochat() }
            .forEach { queueMessage ->
            queueMessage.backupMessage?.let {
                if (messageService.checkIfMessageDeleted(queueMessage.message)) {
                    messageService.forwardMessage(queueMessage.message.chatId, it)
                    messageService.deleteMessage(MESSAGE_QUEUE_ID, it.messageId)
                    queueMessage.restored = true
                    logger.info {
                        "Restored message from ${userService.getFoloUserName(queueMessage.message.from)} " +
                                "in chat ${getChatIdentity(queueMessage.message.chatId)}"
                    }
                }
            }
        }
    }

    fun getStack(message: Message): List<Message> {
        val fullStack = messageStack.plus(messageQueue).associateBy { it.message.messageId }
        val stack = flattenStack(message.messageId, fullStack)
        return stack.ifEmpty { flattenMessage(message) }
    }

    private fun flattenStack(messageId: Int?, fullStack: Map<Int, MessageQueueDto>): MutableList<Message> {
        val messages = mutableListOf<Message>()
        messageId?.let { id ->
            fullStack[id]?.message?.let { stackMessage ->
                stackMessage.replyToMessage?.let {
                    messages.addAll(flattenStack(it.messageId, fullStack))
                }
                messages.add(stackMessage)
            }
        }
        return messages
    }

    private fun flattenMessage(message: Message?): MutableList<Message> {
        val messages = mutableListOf<Message>()
        message?.let {
            messages.addAll(flattenMessage(it.replyToMessage))
            messages.add(it)
        }
        return messages
    }
}