package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id Идентификатор региона из [справочника](https://github.com/hhru/api/blob/master/docs/areas.md)
 * @param name Название региона
 * @param url Ссылка на информацию о регионе
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class IncludesClusterMetroLineArea(

    @get:JsonProperty("id", required = true) val id: String,

    @get:JsonProperty("name", required = true) val name: String,

    @get:JsonProperty("url", required = true) val url: String
) {

}

