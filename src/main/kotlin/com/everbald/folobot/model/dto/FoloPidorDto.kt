package com.everbald.folobot.model.dto

import com.everbald.folobot.persistence.entity.FoloPidorEntity
import com.everbald.folobot.persistence.entity.FoloPidorId
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Objects
import kotlin.math.absoluteValue

data class FoloPidorDto(
    val id: FoloPidorId,
    val foloUser: FoloUserDto = FoloUserDto(id.userId),
    var score: Int = 0,
    var lastWinDate: LocalDate = LocalDate.of(1900, 1, 1),
    var lastActiveDate: LocalDate = LocalDate.now(),
    var messagesPerDay: Int = 0
) {
    constructor(chatId: Long, userId: Long) : this(FoloPidorId(chatId, userId))

    /**
     * Получить основоного пользователя
     */
    fun getMainUserId(): Long {
        return foloUser.getMainUserId()
    }

    /**
     * Получить тег, если он пуст то имя
     */
    fun getTagName(): String {
        return foloUser.getTagName()
    }

    /**
     * Получить имя пользователя
     */
    fun getName(): String {
        return foloUser.name
    }

    /**
     * Получить тэг
     */
    fun getTag(): String {
        return foloUser.tag
    }

    /**
     * Проверка наличичия побед
     */
    private fun hasScore(): Boolean {
        return score > 0
    }

    fun getPassiveDays(): Int {
        return ChronoUnit.DAYS.between(lastActiveDate, LocalDate.now()).absoluteValue.toInt()
    }

    /**
     * Проверка активности
     */
    private fun isActive(): Boolean {
        return getPassiveDays() <= 30
    }

    /**
     * Проверка на твинка
     */
    fun isTwink(): Boolean {
        return foloUser.userId != foloUser.getMainUserId()
    }

    /**
     * Проверка валидности топа
     */
    fun isValid(): Boolean {
        return isActive() && !isTwink()
    }

    /**
     * Проверка валидности топа
     */
    fun isValidTop(): Boolean {
        return hasScore() && !isTwink()
    }

    /**
     * Проверка валидности топа
     */
    fun isValidSlacker(): Boolean {
        return getPassiveDays() > 0 && !isTwink()
    }

    /**
     * Проверка валидности аутсайдера
     */
    fun isValidUnderdog(): Boolean {
        return isValid() && !hasScore()
    }

    /**
     * Проверка наличия якоря
     */
    fun isAnchored(): Boolean {
        return foloUser.anchor
    }

    /**
     * Обновить счет и вернуть себя
     * @return [FoloPidorDto]
     */
    fun updateScore(score: Int): FoloPidorDto {
        this.score = score
        return this
    }

    /**
     * Обновить дату активности и вернуть себя
     * @return [FoloPidorDto]
     */
    fun updateMessagesPerDay(): FoloPidorDto {
        if (lastActiveDate != LocalDate.now()) {
            lastActiveDate = LocalDate.now()
            messagesPerDay = 0
        }
        messagesPerDay++
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FoloPidorDto) return false
        return getMainUserId() == other.getMainUserId()
    }

    override fun hashCode(): Int {
        return Objects.hash(getMainUserId())
    }
}

fun FoloPidorDto.toEntity(): FoloPidorEntity = FoloPidorEntity(
    id = this.id,
    foloUserEntity = this.foloUser.toEntity(),
    score = this.score,
    lastWinDate = this.lastWinDate,
    lastActiveDate = this.lastActiveDate,
    messagesPerDay = this.messagesPerDay
)