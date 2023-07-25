package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id Идентификатор
 * @param name Название
 */
data class VacanciesArgumentItemClusterGroup(

    @get:JsonProperty("id", required = true) val id: String,

    @get:JsonProperty("name", required = true) val name: String
) {

}

