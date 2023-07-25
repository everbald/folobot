package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param count Количество вакансий в данном элементе кластера
 * @param name Название элемента кластера
 * @param url Ссылка на поисковую выдачу по данному элементу кластера
 * @param metroLine 
 * @param metroStation 
 * @param type Тип значения, связанного с группой
 */
data class VacanciesItemsOfClusterItem(

    @get:JsonProperty("count", required = true) val count: java.math.BigDecimal,

    @get:JsonProperty("name", required = true) val name: String,

    @get:JsonProperty("url", required = true) val url: String,

    @get:JsonProperty("metro_line") val metroLine: VacanciesItemsOfClusterItemMetroLine? = null,

    @get:JsonProperty("metro_station") val metroStation: VacanciesItemsOfClusterItemMetroStation? = null,

    @get:JsonProperty("type") val type: Type? = null
) {

    /**
    * Тип значения, связанного с группой
    * Values: STATION,LINE
    */
    enum class Type(val value: String) {

        @JsonProperty("metro_station") STATION("metro_station"),
        @JsonProperty("metro_line") LINE("metro_line")
    }

}

