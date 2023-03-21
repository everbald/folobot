package com.everbald.folobot.extensions

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

fun Double.format(): String = DecimalFormat("#.00", DecimalFormatSymbols.getInstance().withSeparator('.')).format(this)

fun DecimalFormatSymbols.withSeparator(separator: Char): DecimalFormatSymbols {
    this.decimalSeparator = separator
    return this
}