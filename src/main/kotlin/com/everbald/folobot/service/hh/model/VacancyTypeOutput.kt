package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id тип из [справочника vacancy_type](#tag/Obshie-spravochniki/operation/get-dictionaries)
 * @param name название типа вакансии
 */
data class VacancyTypeOutput(

    @get:JsonProperty("id", required = true) val id: String,

    @get:JsonProperty("name") val name: String? = null
) {

}

