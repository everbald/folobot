package com.everbald.folobot.domain

import com.everbald.folobot.domain.type.VarType

data class FoloVar(
    val chatId: Long,
    val type: VarType,
    val value: String?
) {
  constructor(chatId: Long, type: VarType, value: Any?) : this(chatId, type, value.toString())
}

