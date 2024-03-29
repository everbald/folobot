package com.everbald.folobot.service.handlers.message

import com.everbald.folobot.extensions.*
import com.everbald.folobot.service.folocoin.FoloCoinService
import jakarta.annotation.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Component
@Priority(1)
class UserSharedHandler(
    private val foloCoinService: FoloCoinService
) : AbstractMessageHandler() {
    override fun canHandle(update: Update) = (super.canHandle(update) && update.message.isUserShared)
        .also {
            if (it) logger.addUserSharedReceived(update.message.chatId.chatIdentity, update.message.from.name)
        }

    override fun handle(update: Update) = foloCoinService.transferCoin(update)
}