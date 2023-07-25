package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param requirement Отрывок из требований по вакансии, если они найдены в тексте описания
 * @param responsibility Отрывок из обязанностей по вакансии, если они найдены в тексте описания
 */
data class VacancySnippet(

    @get:JsonProperty("requirement") val requirement: String? = null,

    @get:JsonProperty("responsibility") val responsibility: String? = null
) {

}

