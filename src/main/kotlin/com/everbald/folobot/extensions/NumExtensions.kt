package com.everbald.folobot.extensions

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.roundToInt

fun Double.format(): String = DecimalFormat("#.00", DecimalFormatSymbols.getInstance().withSeparator('.')).format(this)
fun Double.round(): Double = (this * 100).roundToInt().toDouble() / 100
fun DecimalFormatSymbols.withSeparator(separator: Char): DecimalFormatSymbols {
    this.decimalSeparator = separator
    return this
}