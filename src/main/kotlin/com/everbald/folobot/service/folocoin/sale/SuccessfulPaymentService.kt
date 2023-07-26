package com.everbald.folobot.service.folocoin.sale

import com.everbald.folobot.extensions.chatId
import com.everbald.folobot.extensions.from
import com.everbald.folobot.persistence.entity.toDto
import com.everbald.folobot.persistence.repos.OrderRepo
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.UserService
import com.everbald.folobot.service.folocoin.FoloCoinService
import com.everbald.folobot.service.folocoin.model.OrderInfoDto
import com.everbald.folobot.service.folocoin.model.OrderStatus
import com.everbald.folobot.service.folocoin.model.toEntity
import mu.KLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class SuccessfulPaymentService(
    private val orderRepo: OrderRepo,
    private val foloCoinService: FoloCoinService,
    private val invoiceService: InvoiceService,
    private val userService: UserService,
    private val messageService: MessageService
) : KLogging() {
    fun processPayment(update: Update) {
        invoiceService.clearInvoices(update.chatId)
        val newOrder = orderRepo.save(
            OrderInfoDto(
                userId = update.from.id,
                status = OrderStatus.NEW,
                payment = update.message.successfulPayment
            ).toEntity()
        ).toDto()
        foloCoinService.issueCoins(update.from.id, 1)
        orderRepo.save(newOrder.setStatus(OrderStatus.DONE).toEntity())
        messageService.sendMessage(
            if (!newOrder.payload.isPrivateChat)
                "Поздравим фолопидора ${userService.getFoloUserName(update.from.id)} " +
                        "с приобретением фолокойна!"
            else "Фолокойн добавлен в твой фолокошелек!",
            newOrder.payload.chatId
        )
        logger.info { "Issued folocoin to ${userService.getFoloUserName(update.from.id)} after successful purchase" }
    }
}