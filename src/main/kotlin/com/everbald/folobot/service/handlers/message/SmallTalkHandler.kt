package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.extensions.addActionReceived
import com.everbald.folobot.extensions.isFromFoloSwarm
import com.everbald.folobot.extensions.isTextMessage
import com.everbald.folobot.domain.type.Action
import com.everbald.folobot.extensions.chatIdentity
import com.everbald.folobot.service.CommandService
import com.everbald.folobot.service.FreelanceService
import com.everbald.folobot.service.SmallTalkService
import com.everbald.folobot.service.UserService
import jakarta.annotation.Priority
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
    private val smallTalkService: SmallTalkService,
    private val userService: UserService,
    private val freelanceService: FreelanceService,
    private val commandService: CommandService
) : AbstractMessageHandler() {
    private var smallTalkStatus: MutableMap<Long?, Boolean> = mutableMapOf()

    fun Message.isSmallTalk() = this.isTextMessage
        .and(isUserMessage.or(userService.isSelf(this.replyToMessage?.from)))

    override fun canHandle(update: Update) = (super.canHandle(update) && update.message.isSmallTalk())
            .also { if (it) logger.addActionReceived(Action.SMALLTALK, update.message.chatId) }

    override fun handle(update: Update) = handle(update, false)

    fun handle(update: Update, withInit: Boolean = false) {
        if (freelanceService.isAboutFreelance(update)) {
            commandService.aboutIt(update)
        } else {
            if (update.message.isFromFoloSwarm()) {
                if (smallTalkStatus[update.message?.chatId] != false) {
                    smallTalkService.smallTalk(update, withInit)
                    suspend(update, 30.seconds)
                } else {
                    logger.info { "Canceling small talk BC it's suspended in ${update.message.chatId.chatIdentity}" }
                }
            } else smallTalkService.smallTalk(update, withInit)
        }
    }

    private fun suspend(update: Update, duration: Duration) {
        smallTalkStatus[update.message.chatId] = false
        logger.info {
            "Small talk on repost is suspended for ${duration.inWholeSeconds} seconds in " +
                    update.message.chatId.chatIdentity
        }
        Timer().schedule(duration.inWholeMilliseconds) {
            smallTalkStatus[update.message.chatId] = true
            logger.info { "Small talk on repost is resumed in ${update.message.chatId.chatIdentity}" }
        }
    }
}