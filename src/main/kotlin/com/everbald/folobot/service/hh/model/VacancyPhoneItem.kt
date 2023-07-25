package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param city код города
 * @param comment комментарий (удобное время для звонка по этому номеру)
 * @param country код страны
 * @param formatted телефонный номер
 * @param number телефон
 */
data class VacancyPhoneItem(

    @get:JsonProperty("city") val city: String? = null,

    @get:JsonProperty("comment") val comment: String? = null,

    @get:JsonProperty("country") val country: String? = null,

    @get:JsonProperty("formatted") val formatted: String? = null,

    @get:JsonProperty("number") val number: String? = null
) {

}

