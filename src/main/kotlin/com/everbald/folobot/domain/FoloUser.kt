package com.everbald.folobot.domain

data class FoloUser(
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
     * @return [FoloUser]
     */
    fun setMainId(mainId: Long): FoloUser {
        this.mainId = mainId
        return this
    }

    /**
     * Установить якорь и вернуть себя
     * @return [FoloUser]
     */
    fun setAnchor(anchor: Boolean): FoloUser {
        this.anchor = anchor
        return this
    }

    /**
     * Установить имя и вернуть себя
     * @return [FoloUser]
     */
    fun setName(name: String?): FoloUser {
        if (name != null) {
            this.name = name
        }
        return this
    }

    /**
     * Установить тэг и вернуть себя
     * @return [FoloUser]
     */
    fun setTag(tag: String): FoloUser {
        this.tag = tag
        return this
    }
}