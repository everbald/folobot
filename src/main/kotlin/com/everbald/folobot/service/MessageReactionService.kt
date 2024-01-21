package com.everbald.folobot.service

import com.everbald.folobot.FoloBot
import com.everbald.folobot.extensions.chatId
import com.everbald.folobot.extensions.messageId
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.reactions.ReactionTypeEmoji
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Service
class MessageReactionService(
    private val foloBot: FoloBot,
) : KLogging() {
    private fun buildReaction(update: Update, emoji: List<String>): SetMessageReaction =
        SetMessageReaction
            .builder()
            .chatId(update.chatId)
            .messageId(update.messageId)
            .reactionTypes(
                emoji
                    .map {
                        ReactionTypeEmoji
                            .builder()
                            .type(ReactionTypeEmoji.EMOJI_TYPE)
                            .emoji(it)
                            .build()
                    }
            )
            .build()

    fun setReaction(update: Update, emoji: List<String>) =
        try {
            buildReaction(update, emoji)
                .let { foloBot.execute(it) }
        } catch (e: TelegramApiException) {
            logger.error(e) { "Reactions was: ${emoji.joinToString()}" }
            null
        }

    fun setReaction(update: Update, emoji: String) = setReaction(update, listOf(emoji))
}