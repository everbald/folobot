package com.everbald.folobot.controller

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/login")
class LoginController() {
    @GetMapping
    fun login(
        model: MutableMap<String, Any?>,
        @RequestParam(value = "error", defaultValue = "false") loginError: Boolean
    ): String {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication.isAuthenticated) model["user"] = authentication.principal as? User

        if (loginError) model["message"] = "Неверный Фоло ID или пароль!"

        return "login"
    }
}