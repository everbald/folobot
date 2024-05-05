package com.everbald.folobot.domain.type

enum class CallbackCommand(val command: String, val description: String) {
    COINBALANCE("/coinbalance", "Баланс претокенов"),
    COINPRICE("/coinprice", "Цена претокена"),
    FOLOMILLIONAIRE("/millionaire", "Топ майнеров"),
    BUYCOIN("/buycoin", "Покупка ₣"),
    TRANSFERCOIN("/transfercoin", "Перевод ₣"),
    FOLOINDEX("/foloindex", "Фолоиндекс");
    companion object {
        private val map = entries.associateBy(CallbackCommand::command)
        fun fromCommand(command: String?) = map[command]
        fun isMyCommand(command: String?) = map.contains(command)
    }
}