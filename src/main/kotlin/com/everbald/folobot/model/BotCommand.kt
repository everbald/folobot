package com.everbald.folobot.model

enum class BotCommand(val command: String) {
    START("/start"),
    SILENTSTREAM("/silentstream"),
    SMALLTALK("/smalltalk"),
    FREELANCE("/freelance"),
    NOFAP("/nofap"),
    FOLOPIDOR("/folopidor"),
    FOLOPIDORALPHA("/folopidoralpha"),
    FOLOCOIN("/folocoin"),
    FOLOCOINTRANSFER("folocointransfer"),
    FOLOCOINTRANSFERCANCEL("Отменить перевод"),
    IT("/it");

    companion object {
        private val map = BotCommand.values().associateBy(BotCommand::command)
        fun fromCommand(command: String?) = map[command]
    }
}