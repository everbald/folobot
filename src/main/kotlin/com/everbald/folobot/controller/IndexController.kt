package com.everbald.folobot.controller

import mu.KLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController() : KLogging() {
    @GetMapping(value = ["", "/", "/index"])
    fun index(model: MutableMap<String, Any>): String {
        model["rndvid"] = "media/${
            listOf(
                "dancefo.mp4",
                "dancefodance.mp4",
                "dothefo.mp4",
                "eatfoeat.mp4",
                "runforun.mp4",
            ).random()
        }"
        return "index"
//        return "redirect:/frontpage";
    }
}