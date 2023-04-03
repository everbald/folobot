package com.everbald.folobot.utils

import com.everbald.folobot.model.PluralType
import com.ibm.icu.text.MessageFormat
import com.ibm.icu.util.ULocale
import java.time.Period
import kotlin.math.abs

class PluralUtils {
    companion object {
        private val locale = ULocale("ru")

        private val numText = mutableMapOf(
            PluralType.YEAR to arrayOf("год", "года", "лет"),
            PluralType.MONTH to arrayOf("месяц", "месяца", "месяцев"),
            PluralType.DAY to arrayOf("день", "дня", "дней"),
            PluralType.COUNT to arrayOf("раз", "раза", "раз"),
            PluralType.YEARISH to arrayOf("годик", "годика", "годиков"),
            PluralType.MESSAGE to arrayOf("сообщение", "сообщения", "сообщений"),
            PluralType.PERCENT to arrayOf("процент", "процента", "процентов"),
            PluralType.COIN to arrayOf("фолокойн", "фолокойна", "фолокойнов")
        )

        private fun buildPattern(pluralType: PluralType): String =
            "{0,plural, one{# ${numText[pluralType]?.get(0)}} few{# ${numText[pluralType]?.get(1)}} other{# ${numText[pluralType]?.get(2)}}}"

        fun getPluralText(number: Number, pluralType: PluralType) =
            MessageFormat(buildPattern(pluralType), locale).format(arrayOf<Any>(number.toInt())).toString()

        fun getPeriodText(period: Period): String {
            val stringBuilder = StringBuilder()
            if (period.years > 0) {
                stringBuilder.append(getPluralText(abs(period.years), PluralType.YEAR))
            }
            if (period.months > 0) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(", ")
                }
                stringBuilder.append(getPluralText(abs(period.months), PluralType.MONTH))
            }
            if (period.days > 0) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(" и ")
                }
                stringBuilder.append(getPluralText(abs(period.days), PluralType.DAY))
            }
            return stringBuilder.toString()
        }
    }
}