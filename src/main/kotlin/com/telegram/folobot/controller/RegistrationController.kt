package com.telegram.folobot.controller

import com.telegram.folobot.model.Authority
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

        if (userDetailsManager.userExists(username)) {
            model["message"] = "User already exists!"
            return "registration"
        }

        userDetailsManager.createUser(
            User.withUsername(username).password(passwordEncoder.encode(password)).roles(Authority.ROLE_USER.role).build()
        )

        return "redirect:/login"
    }
}