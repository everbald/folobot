package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id временной интервал работы из [справочника working_time_intervals](#tag/Obshie-spravochniki/operation/get-dictionaries)
 * @param name название интервала работы
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacancyWorkingTimeIntervalItemOutput(

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("name") val name: String? = null
) {

}

