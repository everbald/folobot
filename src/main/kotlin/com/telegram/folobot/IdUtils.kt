package com.telegram.folobot

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

class IdUtils {

    companion object {
        val TECH_GROUP_ID = 777000L

        val FOLO_CHAT_ID = -1001439088515L
        val FOLO_GROUP_ID = -1001375766618L
        val POC_ID = -1001154453685L
        val ANDREWSLEGACY_ID = -1001210743498L
        val FO_LEGACY_ID = -863607240L
        val FOLO_TEST_CHAT_ID = -1001783789636L
        val FOLO_TEST_GROUP_ID = -1001739309365L
        val FOLO_SWARM = listOf<Long>(-1001200306934L, -1001405153506L, -1001354661440L)
        val ADEQUATE_COMMUNICATION = -1001286289850L


        val ANDREW_ID = 146072069L
        val VITALIK_ID = 800522859L
        val FOLOMKIN_ID = 362689512L

        fun isFolochat(chat: Chat?): Boolean {
            return chat != null && chat.id == FOLO_CHAT_ID
        }

        fun isFoloTestChat(chat: Chat?): Boolean {
            return chat != null && chat.id == FOLO_TEST_CHAT_ID
        }

        fun isAndrew(user: User?): Boolean {
            return user != null && user.id == ANDREW_ID
        }

        fun isVitalik(user: User?): Boolean {
            return user != null && user.id == VITALIK_ID
        }

        fun isFo(user: User?): Boolean {
            return user != null && user.id == FOLOMKIN_ID
        }

        fun isFromFoloSwarm(update: Update): Boolean {
            return FOLO_SWARM.contains(update.message.forwardFromChat.id) || update.message.forwardFrom.id == FOLOMKIN_ID
        }

        fun isAboutFo(update: Update): Boolean {
            return (update.message.isReply && update.message.replyToMessage.from.id == FOLOMKIN_ID) ||
                    (update.message.hasText() &&
                            listOf(
                                "фоломкин",
                                "фолик",
                                "алекс фо",
                                "гуру",
                                "саша",
                                "сашк",
                                "фоломб",
                                "сашок",
                                "санчоус",
                                "фоломкиен",
                                "шурк",
                                "александр",
                                "гурманыч",
                                "вайтифас",
                                "просвещения",
                                "цветочкин",
                                "расческин"
                            )
                                .any { update.message.text.contains(it, ignoreCase = true) })
        }

        fun getChatIdentity(chatId: Long?) : String {
            return when (chatId) {
                FOLO_CHAT_ID -> "фолочат"
                FOLO_TEST_CHAT_ID -> "тестовый чат"
                POC_ID -> "Тайна личной переписки"
                ANDREWSLEGACY_ID -> "Наследие Андрея"
                FO_LEGACY_ID -> "Наследие Фо"
                ADEQUATE_COMMUNICATION -> "Васин загон"
                else -> chatId.toString()
            }
        }

        fun getChatIdentity(chatId: String): String {
            return getChatIdentity(chatId.toLong())
        }
    }
}