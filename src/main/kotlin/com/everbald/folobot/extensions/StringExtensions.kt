package com.everbald.folobot.extensions

fun String.telegramEscape() =
    this.trimMargin().trimIndent()
        .replace("""[_*\[\]()~>#+=\-|{}.!]""".toRegex()) { """\${it.value}""" }

fun String.removeBotName() = this
    .replace("гурманыч", "", true)
    .replace("гурманыч, шурка", "", true)