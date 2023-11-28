package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class VacanciesSuggests(
    val found: Int,
    val value: String
)