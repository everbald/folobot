package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Адрес
 * @param building Дом
 * @param city Город
 * @param description Описание
 * @param id Адрес из [списка доступных адресов работодателя](https://api.hh.ru/openapi/redoc#tag/Adresa-rabotodatelya/operation/get-employer-addresses)
 * @param lat Широта
 * @param lng Долгота
 * @param metro 
 * @param metroStations 
 * @param raw Полный адрес
 * @param street Улица
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacancyAddressRawOutput(

    @get:JsonProperty("building") val building: String? = null,

    @get:JsonProperty("city") val city: String? = null,

    @get:JsonProperty("description") val description: String? = null,

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("lat") val lat: java.math.BigDecimal? = null,

    @get:JsonProperty("lng") val lng: java.math.BigDecimal? = null,

    @get:JsonProperty("metro") val metro: VacancyAddressRawOutputMetro? = null,

    @get:JsonProperty("metro_stations") val metroStations: List<IncludesMetroStation>? = null,

    @get:JsonProperty("raw") val raw: String? = null,

    @get:JsonProperty("street") val street: String? = null
) {

}

