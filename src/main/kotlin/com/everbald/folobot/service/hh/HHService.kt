package com.everbald.folobot.service.hh

import org.springframework.stereotype.Service

@Service
class HHService(
    private val hhFeignClient: HHFeignClient
) {
    fun getVacancie(): String? =
        hhFeignClient.getVacancies(
            page = 0,
            perPage = 1,
            text = "уборщик завод",
            area = "1",
            orderBy = "distance",
            sortPointLat = 55.885110,
            sortPointLng = 37.492742,
//            metro = "2.558",
            onlyWithSalary = true
        ).body?.items?.firstOrNull()?.alternateUrl
}