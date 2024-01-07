package com.everbald.folobot.controller

import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/releases")
class ReleasesController() : KLogging() {
    @GetMapping
    fun releases(): String {
        return "releases"
    }
}