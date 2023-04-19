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
    override fun canHandle(update: Update): Boolean {
        return (update.message?.userShared != null).also {
            if (it) logger.addUserSharedReceived(getChatIdentity(update.message.chatId), update.message.from.getName())
        }
    }

    override fun handle(update: Update) = foloCoinService.transferCoin(update)
}