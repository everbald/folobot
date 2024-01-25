package com.everbald.folobot.domain.type

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
    IT("/it"),
    FOLOBAIL("/folobail"),
    IMAGE ("/image"),
    DAYSTATS("/daystats");

    companion object {
        private val map = entries.associateBy(BotCommand::command)
        fun fromCommand(command: String?) = map[command]
    }
}