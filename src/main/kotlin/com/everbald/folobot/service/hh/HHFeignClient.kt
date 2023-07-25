package com.everbald.folobot.service.hh

import org.springframework.cloud.openfeign.FeignClient

@FeignClient(
    url = "https://api.hh.ru",
    name = "hh-client"
)
interface HHFeignClient: HHApi