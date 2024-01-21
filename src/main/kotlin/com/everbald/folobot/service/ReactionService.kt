package com.everbald.folobot.service

import com.everbald.folobot.domain.type.Reaction
import com.everbald.folobot.extensions.isAboutFo
import com.everbald.folobot.extensions.isNotCommand
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class ReactionService(
    private val messageReactionService: MessageReactionService
) : KLogging() {
    fun react(update: Update) {
        if (update.hasMessage() && update.message.isNotCommand) {
            if (update.message.isAboutFo) messageReactionService.setReaction(update, Reaction.LIKE.emoji)
        }
    }
}