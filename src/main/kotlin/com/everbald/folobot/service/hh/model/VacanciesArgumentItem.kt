package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param argument Параметр поиска вакансии
 * @param disableUrl URL поиска вакансий, который получится, если перестать учитывать в поиске данный параметр
 * @param &#x60;value&#x60; Значение параметра
 * @param clusterGroup 
 * @param hexColor Цвет линии в HEX-формате `RRGGBB` (от `000000` до `FFFFFF`)
 * @param metroType Станция или линия метро (`station`/`line`)
 * @param name Название значения
 * @param valueDescription Описание параметра
 */
data class VacanciesArgumentItem(

    @get:JsonProperty("argument", required = true) val argument: String,

    @get:JsonProperty("disable_url", required = true) val disableUrl: String,

    @get:JsonProperty("value", required = true) val `value`: String,

    @get:JsonProperty("cluster_group") val clusterGroup: VacanciesArgumentItemClusterGroup? = null,

    @get:JsonProperty("hex_color") val hexColor: String? = null,

    @get:JsonProperty("metro_type") val metroType: String? = null,

    @get:JsonProperty("name") val name: String? = null,

    @get:JsonProperty("value_description") val valueDescription: String? = null
) {

}

