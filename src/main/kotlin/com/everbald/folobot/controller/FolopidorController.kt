package com.everbald.folobot.controller

import com.everbald.folobot.domain.type.ControllerCommand
import com.everbald.folobot.domain.FoloPidor
import com.everbald.folobot.service.FoloPidorService
import com.everbald.folobot.service.FoloUserService
import com.everbald.folobot.service.FoloVarService.Companion.COMMON_CHATID
import com.everbald.folobot.service.FoloVarService.Companion.INITIAL_USERID
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.util.*

// TODO логику из контроллеров вынести в сервисы
@Controller
@RequestMapping("/admin/folopidor")
class FolopidorController(
    private val foloPidorService: FoloPidorService,
    private val foloUserService: FoloUserService
) {

    /**
     * Заполнение основного экрана
     * @param model Map с переменными
     * @return Имя экрана
     */
    @GetMapping
    fun main(model: MutableMap<String, Any>): String {
        model["folopidors"] = foloPidorService.findAll()
        return "folopidor"
    }

    /**
     * Post-запрос на выполнение команды с основного экрана
     * @param chatId ID чата
     * @param userId ID пользователя
     * @param score Счет
     * @param action Команда
     * @param model Map с переменными
     * @return Имя экрана
     */
    @PostMapping
    fun onAction(
        @RequestParam chatId: Long = COMMON_CHATID,
        @RequestParam(required = false) userId: Long? = INITIAL_USERID,
        @RequestParam(required = false) score: Int? = 0,
        @RequestParam(required = false) lastWinDate: String?,
        @RequestParam action: String,
        model: MutableMap<String, Any>
    ): String {
        when (ControllerCommand.valueOf(action.uppercase())) {
            ControllerCommand.ADD -> if (foloUserService.exists(userId!!) &&
                !foloPidorService.exists(chatId, userId)
            ) {
                foloPidorService.save(FoloPidor(chatId, userId))
            }
            ControllerCommand.UPDATE -> if (foloPidorService.exists(chatId, userId!!)) {
                val foloPidor = foloPidorService.find(chatId, userId)
                foloPidor.score = score!!
                foloPidor.lastWinDate = LocalDate.parse(lastWinDate)
                foloPidorService.save(foloPidor)
            }
            ControllerCommand.DELETE -> foloPidorService.delete(FoloPidor(chatId, userId!!))
            ControllerCommand.FILTER -> {
                model["folopidors"] =
                    if (!Objects.isNull(chatId)) foloPidorService.findByChatId(chatId)
                    else foloPidorService.findAll()
                return "folopidor"
            }
        }
        return main(model)
    }
}