package com.everbald.folobot.extensions

import com.everbald.folobot.utils.FoloId.ANDREW_ID
import com.everbald.folobot.utils.FoloId.FOLOMKIN_ID
import com.everbald.folobot.utils.FoloId.VASYA_ID
import com.everbald.folobot.utils.FoloId.VITALIK_ID
import org.telegram.telegrambots.meta.api.objects.User

fun User.getName() = "${ this.firstName }${ this.lastName?.let { " $it" } ?: "" }"
fun User?.isFo() = this?.id == FOLOMKIN_ID
fun User?.isAndrew() = this?.id == ANDREW_ID
fun User?.isVitalik() = this?.id == VITALIK_ID
fun User?.isVasya() = VASYA_ID.contains(this?.id)
fun User?.isLikesToDelete() = this.isAndrew() || this.isFo() || this.isVasya()
fun User?.getPremiumPrefix() = if (this?.isPremium == true) "премиум фолопидор " else "уважаемый фолопидор "