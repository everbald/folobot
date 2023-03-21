package com.everbald.folobot.controller

import jakarta.servlet.RequestDispatcher.ERROR_REQUEST_URI
import jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE
import jakarta.servlet.http.HttpServletRequest
import mu.KLogging
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class CustomErrorController() : ErrorController, KLogging() {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): String? {
        request.parameterMap.flatMap { (key, values) -> values.associateBy { key }.toList() }
        logger.trace { "Bad request:\n" +
                "   method ${request.method}\n" +
                "   status: ${request.getAttribute(ERROR_STATUS_CODE)}\n" +
                "   URI: ${request.getAttribute(ERROR_REQUEST_URI)}\n" +
                "   header: ${request.headerNames.toList().map { it to request.getHeader(it) }}\n" +
                "   params: ${request.parameterMap.flatMap { (key, value) -> value.map { key to it } }}\n" +
                "   body ${request.reader.readLines()}"
        }
        return "error"
    }
}