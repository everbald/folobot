package com.everbald.folobot.extensions

import com.everbald.folobot.domain.type.PluralType
import com.everbald.folobot.utils.PluralUtils
import com.ibm.icu.text.RuleBasedNumberFormat
import java.util.*

fun Number.toText(pluralType: PluralType) = PluralUtils.buildPluralText(this, pluralType)
fun Number.toTextWithNumber(pluralType: PluralType) = PluralUtils.buildPluralTextWithNumber(this, pluralType)

fun Number.spellOut() =
    RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT)
        .format(this.toLong(), "%spellout-ordinal-masculine").toString()