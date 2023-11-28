package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id режимы времени работы из [справочника working_time_modes](#tag/Obshie-spravochniki/operation/get-dictionaries)
 * @param name название интервала работы
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacancyWorkingTimeModeItemOutput(

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("name") val name: String? = null
) {

}

