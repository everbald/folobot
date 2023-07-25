package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id требуемый опыт работы из [справочника experience](#tag/Obshie-spravochniki/operation/get-dictionaries)
 * @param name название опыта работы
 */
data class VacancyExperienceOutput(

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("name") val name: String? = null
) {

}

