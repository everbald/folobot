package com.everbald.folobot.service.handlers

import com.everbald.folobot.extensions.addActionReceived
import com.everbald.folobot.extensions.getChatIdentity
import com.everbald.folobot.extensions.isFromFoloSwarm
import com.everbald.folobot.model.ActionsEnum
import com.everbald.folobot.service.OpenAIService
import com.everbald.folobot.service.UserService
import jakarta.annotation.Priority
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*
import kotlin.concurrent.schedule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Component
@Priority(4)
class SmallTalkHandler(
    private val openAIService: OpenAIService,
    private val userService: UserService
) : Handler, KLogging() {
    private var smallTalkStatus: MutableMap<Long?, Boolean> = mutableMapOf()

    fun Message.isSmallTalk() = userService.isSelf(this.replyToMessage?.from) || this.isFromFoloSwarm()

    override fun canHandle(update: Update): Boolean {
        return update.message.isSmallTalk().also {
            if (it) logger.addActionReceived(ActionsEnum.SMALLTALK, update.message.chatId)
        }
    }

    override fun handle(update: Update) = handle(update, false)

    fun handle(update: Update, withInit: Boolean = false) {
        if (update.message.isFromFoloSwarm()) {
            if (smallTalkStatus[update.message?.chatId] != false) {
                openAIService.smallTalk(update, withInit)
                suspend(update, 30.seconds)
            } else {
                logger.info { "Canceling small talk BC it's suspended in ${getChatIdentity(update.message.chatId)}" }
            }
        } else openAIService.smallTalk(update, withInit)
    }

    private fun suspend(update: Update, duration: Duration) {
        smallTalkStatus[update.message.chatId] = false
        logger.info {
            "Small talk on repost is suspended for ${duration.inWholeSeconds} seconds in " +
                    getChatIdentity(update.message.chatId)
        }
        Timer().schedule(duration.inWholeMilliseconds) {
            smallTalkStatus[update.message.chatId] = true
            logger.info { "Small talk on repost is resumed in ${getChatIdentity(update.message.chatId)}" }
        }
    }
}