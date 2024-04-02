package com.everbald.folobot.service

import com.everbald.folobot.domain.FoloPidor
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient

@Component
class ChatService(
    private val telegramClient: TelegramClient
) : KLogging() {
    fun getChatMember(userId: Long, chatId: Long = userId): ChatMember? =
        try {
            telegramClient.execute(GetChatMember(chatId.toString(), userId))
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
                .let { telegramClient.execute(it) }
        } catch (e: TelegramApiException) {
            logger.debug(e) { "Can't get admins for chat $chatId" }
            emptyList()
        }

    fun getChatAdminTitles(chatId: Long): Map<Long, String> =
        getChatAdmins(chatId)
            .filter { it is ChatMemberAdministrator && it.customTitle != null }
            .associate { it.user.id to (it as ChatMemberAdministrator).customTitle }

    fun kickFromChat(foloPidor: FoloPidor) {
        try {
            BanChatMember
                .builder()
                .chatId(foloPidor.chatId)
                .userId(foloPidor.user.userId)
                .build()
                .let { telegramClient.execute(it) }
            UnbanChatMember
                .builder()
                .chatId(foloPidor.chatId)
                .userId(foloPidor.user.userId)
                .onlyIfBanned(true)
                .build()
                .let { telegramClient.execute(it) }
            logger.warn { "Kicked deleted user ${foloPidor.user.userId} for chat ${foloPidor.chatId}" }
        } catch (e: TelegramApiException) {
            logger.warn("Can't kick user ${foloPidor.user.userId} for chat ${foloPidor.chatId}")
        }
    }

    fun getChatName(chatId: Long): String? =
        try {
            GetChat
                .builder()
                .chatId(chatId)
                .build()
                .let { telegramClient.execute(it) }
                .title
        } catch (e: TelegramApiException) {
            logger.warn("Can't get name for chat $chatId")
            null
        }
}
