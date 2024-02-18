package com.everbald.folobot.extensions

fun runWithProbability(percent: Int, block: () -> Unit = {}) {
    (1..100)
        .random()
        .let { if (it <= percent) block() }
}