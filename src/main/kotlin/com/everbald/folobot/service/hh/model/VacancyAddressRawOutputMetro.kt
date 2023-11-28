package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param lat широта
 * @param lineId идентификатор линии метро
 * @param lineName названии линии метро
 * @param lng долгота
 * @param stationId идентификатор станции метро
 * @param stationName название станции метро
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacancyAddressRawOutputMetro(

    @get:JsonProperty("lat", required = true) val lat: java.math.BigDecimal,

    @get:JsonProperty("line_id", required = true) val lineId: String,

    @get:JsonProperty("line_name", required = true) val lineName: String,

    @get:JsonProperty("lng", required = true) val lng: java.math.BigDecimal,

    @get:JsonProperty("station_id", required = true) val stationId: String,

    @get:JsonProperty("station_name", required = true) val stationName: String
) {

}

