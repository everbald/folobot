package com.everbald.folobot.service

import com.everbald.folobot.extensions.*
import com.everbald.folobot.domain.MessageQueue
import com.everbald.folobot.utils.FoloId.MESSAGE_QUEUE_ID
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import java.time.LocalDateTime

@Service
class MessageQueueService(
    private val messageService: MessageService,
    private val userService: UserService
) : KLogging() {
    private val messageQueue: MutableList<MessageQueue> = mutableListOf()
    val messageStack: MutableList<MessageQueue> = mutableListOf()

    fun addToQueue(message: Message) {
        if (message.isNotUserJoin && !message.isCommand) {
            messageQueue.add(
                MessageQueue(
                    LocalDateTime.now(),
                    message,
                    if (message.chat.isFolochat) //&& message.from.isLikesToDelete()
                        messageService.silentForwardMessage(MESSAGE_QUEUE_ID, message)
                    else null
                )
            )
        }
    }

    fun checkFirstInMediaGroup(mediaGroup: String?) =
        mediaGroup == null || messageStack.plus(messageQueue).count { it.message.mediaGroupId == mediaGroup } == 1

    fun sendAndAddToQueue(
        text: String,
        update: Update,
        replyMarkup: ReplyKeyboard? = null,
        reply: Boolean = false,
        disablePreview: Boolean = false,
        parseMode: String = ParseMode.MARKDOWN
    ) {
        messageService.sendMessage(text, update, replyMarkup, reply, disablePreview, parseMode)
            ?.let { messageQueue.add(MessageQueue(LocalDateTime.now(), it)) }
    }

    fun sendAndAddToQueue(
        photo: InputFile,
        chatId: Long,
        text: String? = null,
        replyMarkup: ReplyKeyboard? = null,
        parseMode: String = ParseMode.MARKDOWN
    ) {
        messageService.sendPhoto(photo, chatId, text, replyMarkup, parseMode)
            ?.let { messageQueue.add(MessageQueue(LocalDateTime.now(), it)) }
    }


    fun restoreMessages() {
        return //FIXME при накоплении большого количества сообщений в стаке тг начинает выдвавать ошибку что слишком много запросов
        messageStack.addAll(messageQueue)
        messageQueue.clear()
        messageStack.removeIf { it.recievedAt < LocalDateTime.now().minusDays(1) }

        messageStack.filter { it.message.chat.isFolochat && !it.restored }
            .forEach { queueMessage ->
                queueMessage.backupMessage?.let {
                    if (messageService.checkIfMessageDeleted(queueMessage.message)) {
                        messageService.sendMessage("${queueMessage.message.from.name} удолил сообщение:", queueMessage.message.chatId)
                        if (messageService.forwardMessage(queueMessage.message.chatId, it)) {
                            messageService.deleteMessage(MESSAGE_QUEUE_ID, it.messageId)
                            queueMessage.restored = true
                            logger.info {
                                "Restored message from ${userService.getFoloUserName(queueMessage.message.from)} " +
                                        "in chat ${queueMessage.message.chatId.chatIdentity}"
                            }
                        }
                    }
                }
            }
    }

    fun getStack(message: Message): List<Message> {
        val stack = if (!message.isUserMessage) {
            flattenStack(message.messageId, getFullQueueForChat(message.chatId).associateBy { it.message.messageId })
        } else {
            getFullQueueForChat(message.chatId).sortedBy { it.recievedAt }.takeLast(5).map { it.message }
        }
        return stack.ifEmpty { flattenMessage(message) }
    }

    private fun flattenStack(messageId: Int?, fullStack: Map<Int, MessageQueue>): MutableList<Message> {
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

    private fun getFullQueueForChat(chatId: Long) =
        messageStack.plus(messageQueue).filter { it.message.chatId == chatId }

}