package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id тип занятости из [справочника employment](#tag/Obshie-spravochniki/operation/get-dictionaries)
 * @param name название типа занятости
 */
data class VacancyEmploymentOutput(

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("name") val name: String? = null
) {

}

