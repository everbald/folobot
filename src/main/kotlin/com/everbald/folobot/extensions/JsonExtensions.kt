package com.everbald.folobot.extensions

import com.everbald.folobot.config.objectMapper
import com.fasterxml.jackson.module.kotlin.readValue


fun <T> T.toJson(): String = objectMapper.writeValueAsString(this)
inline fun <reified T> String.toObject() = objectMapper.readValue<T>(this)