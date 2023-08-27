package com.everbald.folobot.service

import com.everbald.folobot.FoloBot
import com.everbald.folobot.model.dto.FoloPidorDto
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.groupadministration.*
import org.telegram.telegrambots.meta.api.objects.MemberStatus
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Component
class ChatService(
    private val foloBot: FoloBot
) : KLogging() {
    fun getChatMember(userId: Long, chatId: Long = userId): ChatMember? =
        try {
            foloBot.execute(GetChatMember(chatId.toString(), userId))
        } catch (e: TelegramApiException) {
            logger.warn("Can't get user $userId for chat $chatId")
            null
        }

    fun buildGetChatAdmins(chatId: Long): GetChatAdministrators =
        GetChatAdministrators
            .builder()
            .chatId(chatId)
            .build()

    fun getChatAdmins(chatId: Long): List<ChatMember> =
        try {
            buildGetChatAdmins(chatId)
                .let { foloBot.execute(it) }
        } catch (e: TelegramApiException) {
            logger.debug(e) { "Can't get admins for chat $chatId" }
            emptyList()
        }

    fun getChatAdminTitles(chatId: Long): Map<Long, String> =
        getChatAdmins(chatId)
            .filter { it is ChatMemberAdministrator && it.customTitle != null }
            .associate { it.user.id to (it as ChatMemberAdministrator).customTitle }

    fun kickFromChat(foloPidorDto: FoloPidorDto) {
        try {
            BanChatMember
                .builder()
                .chatId(foloPidorDto.id.chatId)
                .userId(foloPidorDto.id.userId)
                .build()
                .let { foloBot.execute(it) }
            UnbanChatMember
                .builder()
                .chatId(foloPidorDto.id.chatId)
                .userId(foloPidorDto.id.userId)
                .onlyIfBanned(true)
                .build()
                .let { foloBot.execute(it) }
            logger.warn { "Kicked deleted user ${foloPidorDto.id.userId} for chat ${foloPidorDto.id.chatId}" }
        } catch (e: TelegramApiException) {
            logger.warn("Can't kick user ${foloPidorDto.id.userId} for chat ${foloPidorDto.id.chatId}")
        }
    }

    fun isBotAdmin(chatId: Long): Boolean = getChatMember(foloBot.me.id, chatId)?.status == MemberStatus.ADMINISTRATOR

    fun getChatName(chatId: Long): String? =
        try {
            GetChat
                .builder()
                .chatId(chatId)
                .build()
                .let { foloBot.execute(it) }
                .title
        } catch (e: TelegramApiException) {
            logger.warn("Can't get name for chat $chatId")
            null
        }
}
