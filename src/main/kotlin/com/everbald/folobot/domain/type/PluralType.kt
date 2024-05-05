package com.everbald.folobot.domain.type

enum class PluralType(val one: String, val few: String, val many: String) {
    YEAR("год", "года", "лет"),
    MONTH("месяц", "месяца", "месяцев"),
    DAY("день", "дня", "дней"),
    COUNT("раз", "раза", "раз"),
    YEARISH("годик", "годика", "годиков"),
    MESSAGE("сообщение", "сообщения", "сообщений"),
    PERCENT("процент", "процента", "процентов"),
    COIN("фолокойн", "фолокойна", "фолокойнов"),
    PRETOKEN("претокен", "претокена", "претокенов"),
    BAIL("слив", "слива", "сливов"),
    BAIL_COUNTED("засчитан", "засчитано", "засчитано")
}