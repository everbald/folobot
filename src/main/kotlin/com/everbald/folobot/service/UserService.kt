package com.everbald.folobot.service

import com.everbald.folobot.FoloBot
import com.everbald.folobot.extensions.getName
import com.everbald.folobot.extensions.getPremiumPrefix
import com.everbald.folobot.model.dto.FoloPidorDto
import com.everbald.folobot.model.dto.FoloUserDto
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.objects.MemberStatus
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Component
class UserService(
    private val foloUserService: FoloUserService,
    private val foloBot: FoloBot
) : KLogging() {
    /**
     * Получение имени пользователя
     *
     * @param user [User]
     * @return Имя пользователя
     */
    fun getFoloUserName(user: User): String {
        val foloUser: FoloUserDto = foloUserService.findById(user.id)
        // По тэгу
        var userName: String = foloUser.tag
        // Получение динамически
        if (userName.isEmpty()) userName = user.getName()
        // По сохраненному имени
        if (userName.isEmpty()) userName = foloUser.name
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец"
        return userName
    }

    /**
     * Получение имени пользователя
     *
     * @param userId [Long]
     * @return Имя пользователя
     */
    fun getFoloUserName(userId: Long): String {
        val foloUser: FoloUserDto = foloUserService.findById(userId)
        // По тэгу
        var userName: String = foloUser.getTagName()
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец"
        return userName
    }

    /**
     * Получение имени фолопидора
     *
     * @param foloPidorDto [FoloPidorDto]
     * @return Имя фолопидора
     */
    fun getFoloUserName(foloPidorDto: FoloPidorDto, chatId: Long): String {
        // По тэгу
        var userName: String = foloPidorDto.getTag()
        // По пользователю
        if (userName.isEmpty()) userName = getChatMember(foloPidorDto.id.userId, chatId)?.user?.getName() ?: ""
        // По сохраненному имени
        if (userName.isEmpty()) userName = foloPidorDto.getName()
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец"
        return userName
    }

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

    fun getCustomAdminTitle(userId: Long, chatId: Long): String? =
        getChatAdminTitles(chatId)[userId]

    fun getCustomName(user: User, chatId: Long): String =
        (getCustomAdminTitle(user.id, chatId)
            ?.let { "ваше фолопидрейшество $it " }
            ?: user.getPremiumPrefix()) +
                getFoloUserName(user)

    fun getCustomNameLinked(user: User, chatId: Long): String =
        (getCustomAdminTitle(user.id, chatId)
            ?.let { "ваше фолопидрейшество $it " }
            ?: user.getPremiumPrefix()) +
                getFoloUserNameLinked(user)

    /**
     * Получение кликабельного имени пользователя
     *
     * @param user [User]
     * @return Имя пользователя
     */
    fun getFoloUserNameLinked(user: User): String =
        "[" + getFoloUserName(user) + "](tg://user?id=" + user.id + ")"

    /**
     * Получение кликабельного имени пользователя
     *
     * @param userId [Long]
     * @return Имя пользователя
     */
    fun getFoloUserNameLinked(userId: Long): String =
        "[" + foloUserService.findById(userId).getTagName() + "](tg://user?id=" + userId + ")"

    /**
     * Получение кликабельного имени фолопидора
     *
     * @param foloPidor [FoloPidorDto]
     * @return Имя фолопидора
     */
    fun getFoloUserNameLinked(foloPidor: FoloPidorDto, chatId: Long): String =
        "[" + getFoloUserName(foloPidor, chatId) + "](tg://user?id=" + foloPidor.id.userId + ")"

    /**
     * Проверка, что [User] это этот бот
     *
     * @param user [User]
     * @return да/нет
     */
    fun isSelf(user: User?): Boolean = user?.id == foloBot.me.id

    /**
     * Проверка, что пользователь состоит в чате
     * @param foloPidorDto [FoloPidorDto]
     * @param chatId [Long]
     * @return [Boolean]
     */
    fun isInChat(foloPidorDto: FoloPidorDto, chatId: Long): Boolean =
        getChatMember(foloPidorDto.id.userId, chatId)
            ?.let { !(it.status == MemberStatus.LEFT || it.status == MemberStatus.KICKED) }
            ?: false
}
