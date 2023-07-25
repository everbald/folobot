package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id Идентификатор профессиональной роли
 * @param name название профессиональной роли
 */
data class VacancyProfessionalRoleItemOutput(

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("name") val name: String? = null
) {

}

