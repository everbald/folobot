package com.telegram.folobot.model

enum class BotCommandsEnum(val command: String) {
    START("/start"),
    SILENTSTREAM("/silentstream"),
    FREELANCE("/freelance"),
    NOFAP("/nofap"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORTOP("/folopidortop"),
    FOLOSLACKERS("/foloslackers"),
    FOLOUNDERDOGS("/folounderdogs"),
    FOLOPIDORALPHA("/folopidoralpha"),
    FOLOCOIN("/folocoin"),
    FOLOMILLIONAIRE("/folomillionaire");

    companion object {
        private val map = BotCommandsEnum.values().associateBy(BotCommandsEnum::command)
        fun fromCommand(command: String) = map[command]
    }
}