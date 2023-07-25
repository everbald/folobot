package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param area 
 * @param hexColor Цвет линии в HEX-формате `RRGGBB` (от `000000` до `FFFFFF`)
 * @param id Идентификатор станции метро
 * @param lat Широта
 * @param lng Долгота
 * @param order Порядковый номер станции в линии метро
 */
data class VacanciesItemsOfClusterItemMetroStation(

    @get:JsonProperty("area", required = true) val area: IncludesClusterMetroStationArea,

    @get:JsonProperty("hex_color", required = true) val hexColor: String,

    @get:JsonProperty("id", required = true) val id: String,

    @get:JsonProperty("lat", required = true) val lat: java.math.BigDecimal,

    @get:JsonProperty("lng", required = true) val lng: java.math.BigDecimal,

    @get:JsonProperty("order", required = true) val order: java.math.BigDecimal
) {

}

