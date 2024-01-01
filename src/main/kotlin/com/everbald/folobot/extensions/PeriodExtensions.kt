package com.everbald.folobot.extensions

import com.everbald.folobot.utils.PluralUtils
import java.time.Period

fun Period.toTextWithNumber() = PluralUtils.buildPeriodText(this)