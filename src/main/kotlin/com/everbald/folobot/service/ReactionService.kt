package com.everbald.folobot.service

import com.everbald.folobot.domain.type.Reaction
import com.everbald.folobot.extensions.isAboutFo
import com.everbald.folobot.extensions.isAboutFood
import com.everbald.folobot.extensions.isBail
import com.everbald.folobot.extensions.isLuxuryLife
import com.everbald.folobot.extensions.isNotCommand
import com.everbald.folobot.extensions.runWithProbability
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
                        message.isAboutFo -> runWithProbability(10) {
                            messageReactionService.setReaction(update, Reaction.LIKE.emoji)
                        }
                        message.isBail -> when (message.from.id) {
                            BARBOSKIN_ID -> Reaction.POO.emoji
                            else -> Reaction.HANDSHAKE.emoji
                        }
                            .let { messageReactionService.setReaction(update, it) }
                        message.isLuxuryLife -> messageReactionService.setReaction(update, Reaction.HOTDOG.emoji)
                        message.isAboutFood -> runWithProbability(50) {
                            messageReactionService.setReaction(update, Reaction.LOVE.emoji)
                        }
                        else -> runWithProbability(1) {
                            messageReactionService.setReaction(update, Reaction.entries.random().emoji)
                        }
                    }
                }
            }

    }
}