package com.everbald.folobot.model

enum class BotCommand(val command: String) {
    START("/start"),
    SILENTSTREAM("/silentstream"),
    SMALLTALK("/smalltalk"),
    FREELANCE("/freelance"),
    NOFAP("/nofap"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORTOP("/folopidortop"),
    FOLOSLACKERS("/foloslackers"),
    FOLOUNDERDOGS("/folounderdogs"),
    FOLOPIDORALPHA("/folopidoralpha"),
    FOLOCOIN("/folocoin"),
    FOLOINDEX("/foloindex");

    companion object {
        private val map = BotCommand.values().associateBy(BotCommand::command)
        fun fromCommand(command: String?) = map[command]
    }
}