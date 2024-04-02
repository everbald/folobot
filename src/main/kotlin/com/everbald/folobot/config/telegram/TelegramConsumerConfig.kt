package com.everbald.folobot.config.telegram

import com.everbald.folobot.config.bot.BotCredentialsConfig
import com.everbald.folobot.domain.type.UpdateField
import com.everbald.folobot.service.telegram.TelegramUpdatesConsumer
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.longpolling.util.DefaultGetUpdatesGenerator
import org.telegram.telegrambots.meta.TelegramUrl


@Configuration
class TelegramConsumerConfig(
    private val botCredentialsConfig: BotCredentialsConfig,
    private val telegramUpdatesConsumer: TelegramUpdatesConsumer,
    private val telegramApplication: TelegramBotsLongPollingApplication,
) {
    init {
        telegramApplication
            .registerBot(
                botCredentialsConfig.botToken,
                { TelegramUrl.DEFAULT_URL },
                DefaultGetUpdatesGenerator(UpdateField.entries.map { it.fieldName }),
                telegramUpdatesConsumer
        )
    }
}