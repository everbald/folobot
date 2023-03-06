package com.telegram.folobot.extensions

fun String.telegramEscape() =
    this.trimMargin().trimIndent()
        .replace("""[_*\[\]()~>#+=\-|{}.!]""".toRegex()) { """\${it.value}""" }