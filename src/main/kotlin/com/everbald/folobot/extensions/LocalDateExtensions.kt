package com.everbald.folobot.extensions

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

fun LocalDate.toTextWithNumber() =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        .withLocale(Locale.forLanguageTag("ru"))
        .format(this).toString()