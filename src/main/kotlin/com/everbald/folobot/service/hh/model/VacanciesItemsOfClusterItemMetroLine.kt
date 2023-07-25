package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param area 
 * @param hexColor Цвет линии в HEX-формате `RRGGBB` (от `000000` до `FFFFFF`)
 * @param id Идентификатор линии
 */
data class VacanciesItemsOfClusterItemMetroLine(

    @get:JsonProperty("area", required = true) val area: IncludesClusterMetroLineArea,

    @get:JsonProperty("hex_color", required = true) val hexColor: String,

    @get:JsonProperty("id", required = true) val id: String
) {

}

