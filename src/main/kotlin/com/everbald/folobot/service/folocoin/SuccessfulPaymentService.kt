package com.everbald.folobot.service.folocoin

import com.everbald.folobot.persistence.entity.toDto
import com.everbald.folobot.persistence.repos.OrderRepo
import com.everbald.folobot.service.MessageService
import com.everbald.folobot.service.UserService
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
        invoiceService.clearInvoices()
        val newOrder = orderRepo.save(
            OrderInfoDto(
                status = OrderStatus.NEW,
                payment = update.message.successfulPayment
            ).toEntity()
        ).toDto()
        foloCoinService.issuePurchasedCoins(update.message.from.id, 1)
        orderRepo.save(newOrder.setStatus(OrderStatus.DONE).toEntity())
        if (!newOrder.payload.isPrivateChat) {
            messageService.sendMessage(
                "Поздравим фолопидора ${userService.getFoloUserName(update.message.from.id)} " +
                        "с приобретением фолокойна!",
                newOrder.payload.chatId
            )
        }
        logger.info { "Issued folocoin to ${userService.getFoloUserName(update.message.from.id)} after successful purchase" }
    }
}