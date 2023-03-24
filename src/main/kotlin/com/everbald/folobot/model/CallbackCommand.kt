package com.everbald.folobot.model

enum class CallbackCommand(val command: String, val description: String) {
    COINBALANCE("/coinbalance", "Баланс кошелька"),
    COINPRICE("/coinprice", "Цена фолокойна"),
    FOLOMILLIONAIRE("/millionaire", "Топ акционеров"),
    BUYCOIN("/buycoin", "Покупка ₣");
    companion object {
        private val map = CallbackCommand.values().associateBy(CallbackCommand::command)
        fun fromCommand(command: String?) = map[command]
        fun isMyCommand(command: String?) = map.contains(command)
    }
}