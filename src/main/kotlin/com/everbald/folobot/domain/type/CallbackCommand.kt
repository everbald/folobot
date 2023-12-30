package com.everbald.folobot.domain.type

enum class CallbackCommand(val command: String, val description: String) {
    COINBALANCE("/coinbalance", "Баланс кошелька"),
    COINPRICE("/coinprice", "Цена фолокойна"),
    FOLOMILLIONAIRE("/millionaire", "Топ акционеров"),
    BUYCOIN("/buycoin", "Покупка ₣"),
    TRANSFERCOIN("/transfercoin", "Перевод ₣"),
    FOLOINDEX("/foloindex", "Фолоиндекс"),
    FOLOPIDOR("/folopidor", "Фолопидор дня"),
    FOLOPIDORTOP("/folopidortop", "Топ фолопидоров"),
    FOLOSLACKERS("/foloslackers", "Фолобездельники"),
    FOLOUNDERDOGS("/folounderdogs", "Фолоаутсайдеры"),;
    companion object {
        private val map = entries.associateBy(CallbackCommand::command)
        fun fromCommand(command: String?) = map[command]
        fun isMyCommand(command: String?) = map.contains(command)
    }
}