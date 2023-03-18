package com.everbald.folobot.model.dto

import com.everbald.folobot.persistence.entity.FoloUserEntity

data class FoloUserDto(
    val userId: Long,
    var mainId: Long = 0L,
    var anchor: Boolean = false,
    var name: String = "",
    var tag: String = ""
) {

    /**
     * Получить основоного пользователя
     */
    fun getMainUserId() : Long {
        return if (mainId != 0L) mainId else userId
    }

    /**
     * Получить тэг, если он пуст имя
     */
    fun getTagName(): String { return tag.ifEmpty { name } }

    /**
     * Установить основного пользователя и вернуть себя
     * @return [FoloUserDto]
     */
    fun setMainId(mainId: Long): FoloUserDto {
        this.mainId = mainId
        return this
    }

    /**
     * Установить якорь и вернуть себя
     * @return [FoloUserDto]
     */
    fun setAnchor(anchor: Boolean): FoloUserDto {
        this.anchor = anchor
        return this
    }

    /**
     * Установить имя и вернуть себя
     * @return [FoloUserDto]
     */
    fun setName(name: String?): FoloUserDto {
        if (name != null) {
            this.name = name
        }
        return this
    }

    /**
     * Установить тэг и вернуть себя
     * @return [FoloUserDto]
     */
    fun setTag(tag: String): FoloUserDto {
        this.tag = tag
        return this
    }
}

fun FoloUserDto.toEntity(): FoloUserEntity = FoloUserEntity(
    userId = this.userId,
    mainId = this.mainId,
    anchor = this.anchor,
    name = this.name,
    tag = this.tag
)