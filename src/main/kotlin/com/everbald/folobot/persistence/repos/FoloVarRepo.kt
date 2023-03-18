package com.everbald.folobot.persistence.repos

import com.everbald.folobot.persistence.entity.FoloVarEntity
import com.everbald.folobot.persistence.entity.FoloVarId
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoloVarRepo : CrudRepository<FoloVarEntity, FoloVarId> {
    fun findVarById(foloVarId: FoloVarId): FoloVarEntity?
}