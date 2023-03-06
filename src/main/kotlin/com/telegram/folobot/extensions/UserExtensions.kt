package com.telegram.folobot.extensions

import com.telegram.folobot.FoloId.ANDREW_ID
import com.telegram.folobot.FoloId.FOLOMKIN_ID
import com.telegram.folobot.FoloId.VASYA_ID
import com.telegram.folobot.FoloId.VITALIK_ID
import org.telegram.telegrambots.meta.api.objects.User

fun User?.isFo() = this?.id == FOLOMKIN_ID
fun User?.isAndrew() = this?.id == ANDREW_ID
fun User?.isVitalik() = this?.id == VITALIK_ID
fun User?.isVasya() = VASYA_ID.contains(this?.id)
fun User?.isLikesToDelete() = this.isAndrew() || this.isFo() || this.isVasya()
fun User?.getPremiumPrefix() = if (this?.isPremium == true) "премиум " else ""