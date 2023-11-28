package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param id Идентификатор кластера
 * @param items Массив поисковых запросов в данном кластере с указанием дополнительных параметров
 * @param name Название типа кластера
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacanciesClusterItem(

    @get:JsonProperty("id", required = true) val id: String,

    @get:JsonProperty("items", required = true) val items: List<VacanciesItemsOfClusterItem>,

    @get:JsonProperty("name", required = true) val name: String
)

