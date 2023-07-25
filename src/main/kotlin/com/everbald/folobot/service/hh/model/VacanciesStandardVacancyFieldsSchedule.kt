package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id график работы из [справочника schedule](#tag/Obshie-spravochniki/operation/get-dictionaries)
 * @param name Название графика работы
 */
data class VacanciesStandardVacancyFieldsSchedule(

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("name") val name: String? = null
) {

}

