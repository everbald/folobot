package com.everbald.folobot.utils

import com.everbald.folobot.model.NumType
import java.text.ChoiceFormat
import java.time.Period
import kotlin.math.abs

class Utils {
    companion object {
        private val numText = mutableMapOf(
            NumType.YEAR to arrayOf("лет", "год", "года", "лет"),
            NumType.MONTH to arrayOf("месяцев", "месяц", "месяца", "месяцев"),
            NumType.DAY to arrayOf("дней", "день", "дня", "дней"),
            NumType.COUNT to arrayOf("раз", "раз", "раза", "раз"),
            NumType.YEARISH to arrayOf("годиков", "годик", "годика", "годиков"),
            NumType.MESSAGE to arrayOf("сообщений", "сообщение", "сообщения", "сообщений"),
            NumType.POINT to arrayOf("пунктов", "пункт", "пункта", "пунктов"),
            NumType.COIN to arrayOf("фолокойнов", "фолокойн", "фолокойна", "фолокойнов")
        )

        /**
         * Текстовое представление части даты (1901 -> 1901 год)
         *
         * @param part часть даты
         * @param numType [NumType]
         * @return Текст
         */
        fun getNumText(part: Int, numType: NumType): String {
            val format = ChoiceFormat(doubleArrayOf(0.0, 1.0, 2.0, 5.0), numText[numType])
            val rule = if (part % 100 in 11..14) part else part % 10
            return part.toString() + ' ' + format.format(rule.toLong())
        }

        fun getPeriodText(period: Period): String {
            val stringBuilder = StringBuilder()
            if (period.years > 0) {
                stringBuilder.append(getNumText(abs(period.years), NumType.YEAR))
            }
            if (period.months > 0) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(", ")
                }
                stringBuilder.append(getNumText(abs(period.months), NumType.MONTH))
            }
            if (period.days > 0) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(" и ")
                }
                stringBuilder.append(getNumText(abs(period.days), NumType.DAY))
            }
            return stringBuilder.toString()
        }
    }
}