package com.everbald.folobot.extensions

fun String.telegramEscape() =
    this.trimMargin().trimIndent()
        .replace("""[_*\[\]()~>#+=\-|{}.!]""".toRegex()) { """\${it.value}""" }