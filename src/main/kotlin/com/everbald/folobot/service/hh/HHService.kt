package com.everbald.folobot.service.hh

import mu.KLogging
import org.springframework.stereotype.Service

@Service
class HHService(
    private val hhFeignClient: HHFeignClient
) : KLogging() {
    fun getVacancy(): String? =
        try {
            hhFeignClient.getVacancies(
                page = 0,
                perPage = 5,
                text = "уборщик завод",
                area = "1",
                orderBy = "distance",
                sortPointLat = 55.885110,
                sortPointLng = 37.492742,
                onlyWithSalary = true
            ).body?.items?.random()?.alternateUrl
        } catch (ex: Exception) {
            logger.warn(ex) { "Error occurred while getting vacancies" }
            null
        }
}