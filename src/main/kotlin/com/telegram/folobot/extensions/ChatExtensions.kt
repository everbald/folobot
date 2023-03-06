package com.telegram.folobot.extensions

import com.telegram.folobot.FoloId.ADEQUATE_COMMUNICATION
import com.telegram.folobot.FoloId.ANDREWSLEGACY_ID
import com.telegram.folobot.FoloId.FOLO_CHAT_ID
import com.telegram.folobot.FoloId.FOLO_TEST_CHAT_ID
import com.telegram.folobot.FoloId.FO_LEGACY_ID
import com.telegram.folobot.FoloId.POC_ID
import org.telegram.telegrambots.meta.api.objects.Chat

fun Chat?.isFolochat() = this?.id == FOLO_CHAT_ID
fun Chat?.isFoloTestChat() = this?.id == FOLO_TEST_CHAT_ID

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
