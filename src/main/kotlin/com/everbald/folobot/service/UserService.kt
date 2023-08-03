package com.everbald.folobot.service

import com.everbald.folobot.FoloBot
import com.everbald.folobot.extensions.getName
import com.everbald.folobot.extensions.getPremiumPrefix
import com.everbald.folobot.model.dto.FoloPidorDto
import com.everbald.folobot.model.dto.FoloUserDto
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.MemberStatus
import org.telegram.telegrambots.meta.api.objects.User

@Component
class UserService(
    private val foloUserService: FoloUserService,
    private val chatService: ChatService,
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
        if (userName.isEmpty()) userName = chatService.getChatMember(foloPidorDto.id.userId, chatId)?.user?.getName() ?: ""
        // По сохраненному имени
        if (userName.isEmpty()) userName = foloPidorDto.getName()
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец"
        return userName
    }

    fun getCustomAdminTitle(userId: Long, chatId: Long): String? =
        chatService.getChatAdminTitles(chatId)[userId]

    fun getCustomNamePrefix(user: User, chatId: Long): String =
        (getCustomAdminTitle(user.id, chatId)
            ?.let { "ваше фолопидрейшество $it " }
            ?: user.getPremiumPrefix())

    fun getCustomName(user: User, chatId: Long): String =
        getCustomNamePrefix(user, chatId) + getFoloUserName(user)

    fun getCustomNameLinked(user: User, chatId: Long): String =
        getCustomNamePrefix(user, chatId) + getFoloUserNameLinked(user)

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
     * @return [Boolean]
     */
    fun isInChat(foloPidorDto: FoloPidorDto): Boolean =
        chatService.getChatMember(foloPidorDto.id.userId, foloPidorDto.id.chatId)
            ?.let { !(it.status == MemberStatus.LEFT || it.status == MemberStatus.KICKED) }
            ?: false
}
