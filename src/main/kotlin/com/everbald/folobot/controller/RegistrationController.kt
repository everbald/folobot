package com.everbald.folobot.controller

import com.everbald.folobot.model.Authority
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/registration")
class RegistrationController(
    private val userDetailsManager: UserDetailsManager,
    private val passwordEncoder: PasswordEncoder
) {
    @GetMapping
    fun registration(): String {
        return "registration"
    }

    @PostMapping
    fun addUser(username: String, password: String, model: MutableMap<String, Any>): String {
        if (username.isEmpty() || password.isEmpty()) {
            model["message"] = "Неверный Фоло ID или пароль!"
            return "registration"
        }

        if (userDetailsManager.userExists(username)) {
            model["message"] = "Фоло ID уже существует!"
            return "registration"
        }

        userDetailsManager.createUser(
            User.withUsername(username).password(passwordEncoder.encode(password)).roles(Authority.ROLE_USER.role).build()
        )

        return "redirect:/login"
    }
}