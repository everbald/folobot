package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Объект с информацией об интервью о жизни в компании
 * @param id Идентификатор интервью
 * @param url Адрес страницы, содержащей интервью
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacanciesStandardVacancyFieldsInsiderInterview(

    @get:JsonProperty("id", required = true) val id: String,

    @get:JsonProperty("url", required = true) val url: String
) {

}

