package com.everbald.folobot.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "folo_var")
class FoloVarEntity(
    @EmbeddedId
    @Column(nullable = false)
    var id: FoloVarId,
    @Column(nullable = false)
    var value: String
)