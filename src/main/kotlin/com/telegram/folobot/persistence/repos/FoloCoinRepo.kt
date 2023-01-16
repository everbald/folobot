package com.telegram.folobot.persistence.repos

import com.telegram.folobot.persistence.entity.FoloCoinEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FoloCoinRepo : CrudRepository<FoloCoinEntity, Long> {
    fun findCoinByUserId(userId: Long): FoloCoinEntity?
    fun findTop10ByOrderByCoinsDescPointsDesc(): List<FoloCoinEntity>
    fun findByPointsGreaterThanEqual(points: Int): List<FoloCoinEntity>
    @Query(value = "select sum(coins) from folo_coin", nativeQuery = true)
    fun getSumCoins(): Int?
}