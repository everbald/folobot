package com.everbald.folobot.utils

import com.everbald.folobot.domain.type.PluralType
import com.ibm.icu.text.MessageFormat
import com.ibm.icu.util.ULocale
import java.time.Period
import kotlin.math.abs

class PluralUtils {
    companion object {
        private val locale = ULocale("ru")

        private fun buildMessageFormat(pluralType: PluralType): MessageFormat =
            MessageFormat(
                "{0,plural, " +
                        "=1{${pluralType.one}}" +
                        "few{${pluralType.few}}" +
                        "other{${pluralType.many}}" +
                        "}",
                locale
            )

        private fun buildMessageFormatWithNumber(pluralType: PluralType): MessageFormat =
            MessageFormat(
                "{0,plural, " +
                        "=1{# ${pluralType.one}}" +
                        "few{# ${pluralType.few}}" +
                        "other{# ${pluralType.many}}" +
                        "}",
                locale
            )

        fun buildPluralText(number: Number, pluralType: PluralType) =
            buildMessageFormat(pluralType)
                .format(arrayOf<Any>(number.toInt()))
                .toString()

        fun buildPluralTextWithNumber(number: Number, pluralType: PluralType) =
            buildMessageFormatWithNumber(pluralType)
                .format(arrayOf<Any>(number.toInt()))
                .toString()

        fun buildPeriodText(period: Period): String {
            val stringBuilder = StringBuilder()
            if (period.years > 0) {
                stringBuilder.append(buildPluralTextWithNumber(abs(period.years), PluralType.YEAR))
            }
            if (period.months > 0) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(", ")
                }
                stringBuilder.append(buildPluralTextWithNumber(abs(period.months), PluralType.MONTH))
            }
            if (period.days > 0) {
                if (stringBuilder.isNotEmpty()) {
                    stringBuilder.append(" и ")
                }
                stringBuilder.append(buildPluralTextWithNumber(abs(period.days), PluralType.DAY))
            }
            return stringBuilder.toString()
        }
    }
}