package com.telegram.folobot.service.handlers

import com.telegram.folobot.extensions.getChatIdentity
import com.telegram.folobot.extensions.isFromFoloSwarm
import com.telegram.folobot.service.OpenAIService
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*
import kotlin.concurrent.schedule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Component
class SmallTalkHandler(
    private val openAIService: OpenAIService
) : Handler, KLogging() {
    private var smallTalkStatus: MutableMap<Long?, Boolean> = mutableMapOf()
    override fun handle(update: Update): BotApiMethod<*>? = handle(update, false)

    fun handle(update: Update, withInit: Boolean = false): BotApiMethod<*>? {
        if (update.message.isFromFoloSwarm()) {
            if (smallTalkStatus[update.message?.chatId] != false) {
                openAIService.smallTalk(update, withInit)
                suspend(update, 30.seconds)
            } else {
                logger.info { "Canceling small talk BC it's suspended in ${getChatIdentity(update.message.chatId)}" }
            }
        } else openAIService.smallTalk(update, withInit)
        return null
    }

    private fun suspend(update: Update, duration: Duration) {
        smallTalkStatus[update.message.chatId] = false
        logger.info { "Small talk on repost is suspended for ${duration.inWholeSeconds} seconds in " +
            getChatIdentity(update.message.chatId)
        }
        Timer().schedule(duration.inWholeMilliseconds) {
            smallTalkStatus[update.message.chatId] = true
            logger.info { "Small talk on repost is resumed in ${getChatIdentity(update.message.chatId)}" }
        }
    }
}