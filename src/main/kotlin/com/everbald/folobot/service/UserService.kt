package com.everbald.folobot.service

import com.everbald.folobot.config.bot.BotCredentialsConfig
import com.everbald.folobot.domain.FoloPidor
import com.everbald.folobot.domain.FoloUser
import com.everbald.folobot.extensions.name
import com.everbald.folobot.extensions.premiumPrefix
import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.MemberStatus
import org.telegram.telegrambots.meta.api.objects.User

@Component
class UserService(
    private val foloUserService: FoloUserService,
    private val chatService: ChatService,
    private val botCredentialsConfig: BotCredentialsConfig
) : KLogging() {
    /**
     * Получение имени пользователя
     *
     * @param user [User]
     * @return Имя пользователя
     */
    fun getFoloUserName(user: User): String {
        val foloUser: FoloUser = foloUserService.find(user.id)
        // По тэгу
        var userName: String = foloUser.tag
        // Получение динамически
        if (userName.isEmpty()) userName = user.name
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
        val foloUser: FoloUser = foloUserService.find(userId)
        // По тэгу
        var userName: String = foloUser.getTagName()
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец"
        return userName
    }

    /**
     * Получение имени фолопидора
     *
     * @param foloPidor [FoloPidor]
     * @return Имя фолопидора
     */
    fun getFoloUserName(foloPidor: FoloPidor, chatId: Long): String {
        // По тэгу
        var userName: String = foloPidor.getTag()
        // По пользователю
        if (userName.isEmpty()) userName = chatService.getChatMember(foloPidor.user.userId, chatId)?.user?.name ?: ""
        // По сохраненному имени
        if (userName.isEmpty()) userName = foloPidor.getName()
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец"
        return userName
    }

    fun getCustomAdminTitle(userId: Long, chatId: Long): String? =
        chatService.getChatAdminTitles(chatId)[userId]

    fun getCustomNamePrefix(user: User, chatId: Long): String =
        (getCustomAdminTitle(user.id, chatId)
            ?.let { "ваше фолопидрейшество $it " }
            ?: user.premiumPrefix)

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
        "[" + foloUserService.find(userId).getTagName() + "](tg://user?id=" + userId + ")"

    /**
     * Получение кликабельного имени фолопидора
     *
     * @param foloPidor [FoloPidor]
     * @return Имя фолопидора
     */
    fun getFoloUserNameLinked(foloPidor: FoloPidor, chatId: Long): String =
        "[" + getFoloUserName(foloPidor, chatId) + "](tg://user?id=" + foloPidor.user.userId + ")"

    /**
     * Проверка, что [User] это этот бот
     *
     * @param user [User]
     * @return да/нет
     */
    fun isSelf(user: User?): Boolean =
      user?.isBot == true && user.userName == botCredentialsConfig.botUsername

    /**
     * Проверка, что пользователь состоит в чате
     * @param foloPidor [FoloPidor]
     * @return [Boolean]
     */
    fun isInChat(foloPidor: FoloPidor): Boolean =
        chatService.getChatMember(foloPidor.user.userId, foloPidor.chatId)
            ?.let { !(it.status == MemberStatus.LEFT || it.status == MemberStatus.KICKED) }
            ?: false
}
