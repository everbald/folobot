package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
* 
* Values: FAVORITED,GOT_RESPONSE,GOT_INVITATION,GOT_REJECTION,BLACKLISTED,NULL
*/
enum class VacancyRelationItem(val value: kotlin.String) {

    @JsonProperty("favorited") FAVORITED("favorited"),
    @JsonProperty("got_response") GOT_RESPONSE("got_response"),
    @JsonProperty("got_invitation") GOT_INVITATION("got_invitation"),
    @JsonProperty("got_rejection") GOT_REJECTION("got_rejection"),
    @JsonProperty("blacklisted") BLACKLISTED("blacklisted"),
    @JsonProperty("null") NULL("null")
}

