package com.everbald.folobot.extensions


import com.everbald.folobot.utils.FoloId.ADEQUATE_COMMUNICATION
import com.everbald.folobot.utils.FoloId.ANDREWSLEGACY_ID
import com.everbald.folobot.utils.FoloId.FOLO_CHAT_ID
import com.everbald.folobot.utils.FoloId.FOLO_TEST_CHAT_ID
import com.everbald.folobot.utils.FoloId.FO_LEGACY_ID
import com.everbald.folobot.utils.FoloId.POC_ID
import org.telegram.telegrambots.meta.api.objects.Chat

val Chat?.isFolochat: Boolean get() = this?.id == FOLO_CHAT_ID
val Chat?.isFoloTestChat: Boolean get() = this?.id == FOLO_TEST_CHAT_ID

val Long?.chatIdentity: String get() =
    this?.let {
        when (it) {
            FOLO_CHAT_ID -> "фолочат"
            FOLO_TEST_CHAT_ID -> "тестовый чат"
            POC_ID -> "Тайна личной переписки"
            ANDREWSLEGACY_ID -> "Наследие Андрея"
            FO_LEGACY_ID -> "Наследие Фо"
            ADEQUATE_COMMUNICATION -> "Васин загон"
            else -> it.toString()
        }
    } ?: "Undefined chat"

val String?.chatIdentity: String get() = this?.toLong().chatIdentity
