package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Зарплата
 * @param currency Код валюты из [справочника currency](#tag/Obshie-spravochniki/operation/get-dictionaries)
 * @param from Нижняя граница зарплаты
 * @param gross Признак что границы зарплаты указаны до вычета налогов
 * @param to Верхняя граница зарплаты
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacancySalary(

    @get:JsonProperty("currency") val currency: String? = null,

    @get:JsonProperty("from") val from: Int? = null,

    @get:JsonProperty("gross") val gross: Boolean? = null,

    @get:JsonProperty("to") val to: Int? = null
) {

}

