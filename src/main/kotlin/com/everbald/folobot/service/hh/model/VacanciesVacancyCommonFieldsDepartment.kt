package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id департамент [из справочника](https://api.hh.ru/openapi/redoc#tag/Informaciya-o-rabotodatele/operation/get-employer-departments), от имени которого размещается вакансия (если данная возможность доступна для компании)
 * @param name название департамента работодателя
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacanciesVacancyCommonFieldsDepartment(

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("name") val name: String? = null
) {

}

