package com.everbald.folobot.service

import com.everbald.folobot.domain.type.Reaction
import com.everbald.folobot.extensions.isAboutFo
import com.everbald.folobot.extensions.isAboutFood
import com.everbald.folobot.extensions.isAboutMother
import com.everbald.folobot.extensions.isBail
import com.everbald.folobot.extensions.isFromFoloSwarm
import com.everbald.folobot.extensions.isLuxuryLife
import com.everbald.folobot.extensions.isNotCommand
import com.everbald.folobot.utils.FoloId.BARBOSKIN_ID
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class ReactionService(
    private val messageReactionService: MessageReactionService,
) : KLogging() {
    fun react(update: Update) {
        update.message
            ?.let { message ->
                if (message.isNotCommand) {
                    when {
                        message.isAboutFo -> messageReactionService.setReaction(update, Reaction.LIKE.emoji)
                        message.isFromFoloSwarm -> messageReactionService.setReaction(update, Reaction.LOVE.emoji)
                        message.isBail -> when (message.from.id) {
                            BARBOSKIN_ID -> Reaction.POO.emoji
                            else -> Reaction.HANDSHAKE.emoji
                        }
                            .let { messageReactionService.setReaction(update, it) }
                        message.isLuxuryLife -> messageReactionService.setReaction(update, Reaction.HOTDOG.emoji)
                        message.isAboutFood -> messageReactionService.setReaction(update, Reaction.LOVEEYES.emoji)
                        message.isAboutMother -> messageReactionService.setReaction(update, Reaction.STAREYES.emoji)
                    }
                }
            }

    }
}