package com.everbald.folobot.controller

import com.everbald.folobot.service.MessageService
import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/admin")
class AdminController(private val messageService: MessageService) : KLogging() {
    @GetMapping
    fun main(): String {
        return "admin"
    }

    @PostMapping
    fun sendMessage(@RequestParam chatId: Long, @RequestParam message: String): String {
        messageService.sendMessage(message, chatId)
        return "admin"
    }
}