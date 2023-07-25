package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param items Список вакансий
 * @param found Найдено результатов
 * @param page Номер страницы
 * @param pages Всего страниц
 * @param perPage Результатов на странице
 * @param clusters Массив [кластеров поиска](#tag/Poisk-vakansij/Klastery-v-poiske-vakansij)
 * @param arguments Массив параметров поиска
 * @param alternateUrl ссылка на вакансию
 */
data class VacanciesVacanciesResponse(

    @get:JsonProperty("items", required = true) val items: List<VacanciesVacanciesItem>,

    @get:JsonProperty("found", required = true) val found: java.math.BigDecimal,

    @get:JsonProperty("page", required = true) val page: java.math.BigDecimal,

    @get:JsonProperty("pages", required = true) val pages: java.math.BigDecimal,

    @get:JsonProperty("per_page", required = true) val perPage: java.math.BigDecimal,

    @get:JsonProperty("clusters") val clusters: List<VacanciesClusterItem>? = null,

    @get:JsonProperty("arguments") val arguments: List<VacanciesArgumentItem>? = null,

    @get:JsonProperty("alternate_url") val alternateUrl: String? = null
) {

}

