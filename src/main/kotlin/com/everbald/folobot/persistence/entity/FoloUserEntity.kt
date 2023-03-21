package com.everbald.folobot.persistence.entity

import com.everbald.folobot.model.dto.FoloUserDto
import jakarta.persistence.*

@Entity
@Table(name = "folo_user")
class FoloUserEntity(
    @Id
    @Column(name = "userId", nullable = false)
    val userId: Long,

    @Column(nullable = false)
    var mainId: Long = 0L,
    @Column(nullable = false)
    var anchor: Boolean = false,
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var tag: String = ""
) {
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "foloUserEntity", orphanRemoval = true)
    val foloPidorEntities: MutableSet<FoloPidorEntity> = mutableSetOf()
}

fun FoloUserEntity.toDto(): FoloUserDto = FoloUserDto(
    userId = this.userId,
    mainId = this.mainId,
    anchor = this.anchor,
    name = this.name,
    tag = this.tag
)