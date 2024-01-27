package com.everbald.folobot.domain

import java.time.LocalDate
import java.util.Objects

data class FoloPidor(
    val chatId: Long,
    val user: FoloUser,
    var score: Int = 0,
    var lastWinDate: LocalDate? = null
) {
    constructor(chatId: Long, userId: Long) : this(chatId, FoloUser(userId))

    /**
     * Получить основоного пользователя
     */
    fun getMainUserId(): Long = user.getMainUserId()

    /**
     * Получить тег, если он пуст то имя
     */
    fun getTagName(): String = user.getTagName()

    /**
     * Получить имя пользователя
     */
    fun getName(): String = user.name

    /**
     * Получить тэг
     */
    fun getTag(): String = user.tag

    /**
     * Проверка наличия побед
     */
    private fun hasScore(): Boolean = score > 0

    /**
     * Проверка на твинка
     */
    fun isTwink(): Boolean = user.userId != user.getMainUserId()

    /**
     * Проверка валидности топа
     */
    fun isValidTop(): Boolean = hasScore() && !isTwink()

    /**
     * Проверка наличия якоря
     */
    fun isAnchored(): Boolean = user.anchor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FoloPidor) return false
        return getMainUserId() == other.getMainUserId()
    }

    override fun hashCode(): Int {
        return Objects.hash(getMainUserId())
    }
}

data class FoloPidorWithMessageCount(
    val foloPidor: FoloPidor,
    val messageCount: Int
)