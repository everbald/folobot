package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param name Название компании
 * @param trusted флаг, показывающий, прошла ли компания проверку на сайте
 * @param accreditedItEmployer флаг, показывающий, прошла ли компания IT аккредитацию
 * @param alternateUrl ссылка на представление компании на сайте
 * @param id Идентификатор компании
 * @param logoUrls 
 * @param url URL, на который нужно сделать GET-запрос, чтобы получить информацию о компании
 * @param vacanciesUrl ссылка на поисковую выдачу вакансий данной компании
 */
data class VacanciesEmployerPublic(

    @get:JsonProperty("name", required = true) val name: String,

    @get:JsonProperty("trusted", required = true) val trusted: Boolean,

    @get:JsonProperty("accredited_it_employer") val accreditedItEmployer: Boolean? = null,

    @get:JsonProperty("alternate_url") val alternateUrl: String? = null,

    @get:JsonProperty("id") val id: String? = null,

    @get:JsonProperty("logo_urls") val logoUrls: VacanciesEmployerPublicLogoUrls? = null,

    @get:JsonProperty("url") val url: String? = null,

    @get:JsonProperty("vacancies_url") val vacanciesUrl: String? = null
) {

}

