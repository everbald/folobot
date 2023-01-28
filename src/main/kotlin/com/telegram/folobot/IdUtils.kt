package com.telegram.folobot

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.EntityType
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
        val FOLO_SWARM = listOf(-1001200306934L, -1001405153506L, -1001354661440L, -1001814776157L)
        val ADEQUATE_COMMUNICATION = -1001286289850L
        val MESSAGE_QUEUE_ID = -800718824L


        val ANDREW_ID = 146072069L
        val VITALIK_ID = 800522859L
        val FOLOMKIN_ID = 362689512L
        val VASYA_ID = listOf(512485120L, 5038748126L, 5454744825L)



        fun isFolochat(chat: Chat?): Boolean {
            return chat?.id == FOLO_CHAT_ID
        }

        fun isFoloTestChat(chat: Chat?): Boolean {
            return chat?.id == FOLO_TEST_CHAT_ID
        }

        fun isAndrew(user: User?): Boolean {
            return user?.id == ANDREW_ID
        }

        fun isVitalik(user: User?): Boolean {
            return user?.id == VITALIK_ID
        }

        fun isFo(user: User?): Boolean {
            return user?.id == FOLOMKIN_ID
        }

        fun isFromFoloSwarm(update: Update): Boolean {
            return FOLO_SWARM.contains(update.message.forwardFromChat?.id) || update.message.forwardFrom?.id == FOLOMKIN_ID
        }

        fun isVasya(user: User?): Boolean {
            return VASYA_ID.contains(user?.id)
        }
        fun isLikesToDelete(user: User?): Boolean {
            return isAndrew(user) || isFo(user) || isVasya(user)
        }

        fun isAboutFo(update: Update): Boolean {
            return (update.message.isReply && update.message.replyToMessage.from.id == FOLOMKIN_ID) ||
                    (update.message.hasText() &&
                            listOf(
                                "фоло",
                                "фолик",
                                "алекс",
                                "гуру",
                                "саш",
                                "санчоус",
                                "шурк",
                                "гурманыч",
                                "вайтифас",
                                "просвещения",
                                "цветочкин",
                                "расческин",
                                "folo"
                            ).any { update.message.text.contains(it, ignoreCase = true) }) ||
                    (update.message?.entities?.any { it.type == EntityType.TEXTMENTION && isFo(it.user) } == true)
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

        fun getPremium(user: User): String {
            return if (user.isPremium == true) "премиум " else ""
        }
    }
}