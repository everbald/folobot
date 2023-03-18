package com.everbald.folobot.utils

import com.everbald.folobot.model.NumTypeEnum
import java.text.ChoiceFormat
import java.time.Period
import kotlin.math.abs

class Utils {
    companion object {
        private val numText = mutableMapOf(
            NumTypeEnum.YEAR to arrayOf("лет", "год", "года", "лет"),
            NumTypeEnum.MONTH to arrayOf("месяцев", "месяц", "месяца", "месяцев"),
            NumTypeEnum.DAY to arrayOf("дней", "день", "дня", "дней"),
            NumTypeEnum.COUNT to arrayOf("раз", "раз", "раза", "раз"),
            NumTypeEnum.YEARISH to arrayOf("годиков", "годик", "годика", "годиков"),
            NumTypeEnum.MESSAGE to arrayOf("сообщений", "сообщение", "сообщения", "сообщений"),
            NumTypeEnum.POINT to arrayOf("пунктов", "пункт", "пункта", "пунктов"),
            NumTypeEnum.COIN to arrayOf("фолокойнов", "фолокойн", "фолокойна", "фолокойнов")
        )

        /**
         * Текстовое представление части даты (1901 -> 1901 год)
         *
         * @param part часть даты
         * @param numTypeEnum [NumTypeEnum]
         * @return Текст
         */
        fun getNumText(part: Int, numTypeEnum: NumTypeEnum): String {
            val format = ChoiceFormat(doubleArrayOf(0.0, 1.0, 2.0, 5.0), numText[numTypeEnum])
            val rule = if (part % 100 in 11..14) part else part % 10
            return part.toString() + ' ' + format.format(rule.toLong())
        }

        fun getPeriodText(period: Period): String {
            val stringBuilder = StringBuilder()
            if (period.years > 0) {
                stringBuilder.append(getNumText(abs(period.years), NumTypeEnum.YEAR))
            }
            if (period.months > 0) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(", ")
                }
                stringBuilder.append(getNumText(abs(period.months), NumTypeEnum.MONTH))
            }
            if (period.days > 0) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(" и ")
                }
                stringBuilder.append(getNumText(abs(period.days), NumTypeEnum.DAY))
            }
            return stringBuilder.toString()
        }
    }
}