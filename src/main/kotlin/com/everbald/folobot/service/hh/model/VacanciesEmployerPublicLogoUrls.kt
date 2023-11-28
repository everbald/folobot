package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * cсылки на логотипы работодателя разных размеров
 * @param _90 URL логотипа с размером менее 90px по меньшей стороне
 * @param _240 URL логотипа с размером менее 240px по меньшей стороне
 * @param original URL необработанного логотипа
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacanciesEmployerPublicLogoUrls(

    @get:JsonProperty("90", required = true) val _90: String,

    @get:JsonProperty("240", required = true) val _240: String,

    @get:JsonProperty("original", required = true) val original: String
)

