package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Контактная информация
 * @param callTrackingEnabled Флаг подключения виртуального номера
 * @param email Электронная почта. Значение поля должно соответствовать формату email.
 * @param name Имя контакта
 * @param phones Список телефонов для связи
 */
data class VacancyContactsOutput(

    @get:JsonProperty("call_tracking_enabled") val callTrackingEnabled: Boolean? = null,

    @get:JsonProperty("email") val email: String? = null,

    @get:JsonProperty("name") val name: String? = null,

    @get:JsonProperty("phones") val phones: List<VacancyPhoneItem>? = null
) {

}

