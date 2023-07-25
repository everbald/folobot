package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id рабочие дни из [справочника working_days](#tag/Obshie-spravochniki/operation/get-dictionaries)
 * @param name название рабочего дня
 */
data class VacancyWorkingDayItemOutput(

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("name") val name: String? = null
) {

}

