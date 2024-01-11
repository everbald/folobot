package com.everbald.folobot.controller

import com.everbald.folobot.service.MessageService
import jakarta.servlet.http.HttpServletRequest
import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DonationController(messageService: MessageService) : KLogging() {
    @GetMapping(value = ["/donation", "/donate"])
    fun donate(request: HttpServletRequest): String = "redirect:https://www.tinkoff.ru/cf/9luWBGcAtE1"
        .also {
            request.parameterMap.flatMap { (key, values) -> values.associateBy { key }.toList() }
            logger.info {
                "Donation request:\n" +
                        "   header:\n" +
                        request.headerNames
                            .toList()
                            .map { it to request.getHeader(it) }
                            .joinToString(separator = "\n      ", prefix = "      ") +
                        "\n" +
                        "   country: ${request.locale.isO3Country}\n" +
                        "   ip: ${request.getHeader("X-FORWARDED-FOR") ?: request.remoteAddr}\n"
            }
        }
}