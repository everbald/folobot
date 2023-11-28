package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id Идентификатор профессиональной роли
 * @param name название профессиональной роли
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacancyProfessionalRoleItemOutput(

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("name") val name: String? = null
) {

}

