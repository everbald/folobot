package com.everbald.folobot.controller

import com.everbald.folobot.model.ControllerCommandsEnum
import com.everbald.folobot.model.dto.FoloPidorDto
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
        @RequestParam(required = false) lastWinDate: String? = LocalDate.of(1900,1,1).toString(),
        @RequestParam(required = false) lastActiveDate: String? = LocalDate.now().toString(),
        @RequestParam(required = false) messagesPerDay: Int? = 0,
        @RequestParam action: String,
        model: MutableMap<String, Any>
    ): String {
        when (ControllerCommandsEnum.valueOf(action.uppercase())) {
            ControllerCommandsEnum.ADD -> if (foloUserService.existsById(userId!!) &&
                !foloPidorService.existsById(chatId, userId)
            ) {
                foloPidorService.save(FoloPidorDto(chatId, userId))
            }
            ControllerCommandsEnum.UPDATE -> if (foloPidorService.existsById(chatId, userId!!)) {
                val foloPidor = foloPidorService.findById(chatId, userId)
                foloPidor.score = score!!
                foloPidor.lastWinDate = LocalDate.parse(lastWinDate)
                foloPidor.lastActiveDate = LocalDate.parse(lastActiveDate)
                foloPidor.messagesPerDay = messagesPerDay!!
                foloPidorService.save(foloPidor)
            }
            ControllerCommandsEnum.DELETE -> foloPidorService.delete(FoloPidorDto(chatId, userId!!))
            ControllerCommandsEnum.FILTER -> {
                model["folopidors"] =
                    if (!Objects.isNull(chatId)) foloPidorService.findByIdChatId(chatId)
                    else foloPidorService.findAll()
                return "folopidor"
            }
        }
        return main(model)
    }
}