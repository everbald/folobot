package com.everbald.folobot.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Objects
import kotlin.math.absoluteValue

data class FoloPidor(
    val chatId: Long,
    val user: FoloUser,
    var score: Int = 0,
    var lastWinDate: LocalDate = LocalDate.of(1900, 1, 1),
    var lastActiveDate: LocalDate = LocalDate.now(),
    var messagesPerDay: Int = 0
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

    fun getPassiveDays(): Int = ChronoUnit.DAYS.between(lastActiveDate, LocalDate.now()).absoluteValue.toInt()

    /**
     * Проверка активности
     */
    private fun isActive(): Boolean = getPassiveDays() <= 30

    /**
     * Проверка на твинка
     */
    fun isTwink(): Boolean = user.userId != user.getMainUserId()

    /**
     * Проверка валидности топа
     */
    fun isValid(): Boolean = isActive() && !isTwink()

    /**
     * Проверка валидности топа
     */
    fun isValidTop(): Boolean = hasScore() && !isTwink()

    /**
     * Проверка валидности топа
     */
    fun isValidSlacker(): Boolean = getPassiveDays() > 0 && !isTwink()

    /**
     * Проверка валидности аутсайдера
     */
    fun isValidUnderdog(): Boolean {
        return isValid() && !hasScore()
    }

    /**
     * Проверка наличия якоря
     */
    fun isAnchored(): Boolean = user.anchor

    /**
     * Обновить счет и вернуть себя
     * @return [FoloPidor]
     */
    fun updateScore(score: Int): FoloPidor = this.apply { this.score = score }

    /**
     * Обновить дату активности и вернуть себя
     * @return [FoloPidor]
     */
    fun updateMessagesPerDay(): FoloPidor =
        this.apply {
            if (lastActiveDate != LocalDate.now()) {
                lastActiveDate = LocalDate.now()
                messagesPerDay = 0
            }
            messagesPerDay++
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FoloPidor) return false
        return getMainUserId() == other.getMainUserId()
    }

    override fun hashCode(): Int {
        return Objects.hash(getMainUserId())
    }
}