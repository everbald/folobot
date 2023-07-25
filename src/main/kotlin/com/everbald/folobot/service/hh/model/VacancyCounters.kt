package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param responses Количество откликов на вакансию с момента публикации
 * @param totalResponses Количество откликов на вакансию с момента создания
 */
data class VacancyCounters(

    @get:JsonProperty("responses") val responses: java.math.BigDecimal? = null,

    @get:JsonProperty("total_responses") val totalResponses: java.math.BigDecimal? = null
) {

}

