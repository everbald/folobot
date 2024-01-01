package com.everbald.folobot.extensions

import com.everbald.folobot.utils.FoloId.ANDREW_ID
import com.everbald.folobot.utils.FoloId.FOLOMKIN_ID
import com.everbald.folobot.utils.FoloId.VASYA_ID
import com.everbald.folobot.utils.FoloId.VITALIK_ID
import org.telegram.telegrambots.meta.api.objects.User

val User.name: String get() = "${ this.firstName }${ this.lastName?.let { " $it" } ?: "" }"
val User?.isFo: Boolean get() = this?.id == FOLOMKIN_ID
val User?.isAndrew: Boolean get() = this?.id == ANDREW_ID
val User?.isVitalik: Boolean get() = this?.id == VITALIK_ID
val User?.isVasya: Boolean get() = VASYA_ID.contains(this?.id)
val User?.isLikesToDelete: Boolean get() = this.isAndrew || this.isFo || this.isVasya
val User?.premiumPrefix: String get() = if (this?.isPremium == true) "премиум фолопидор " else "уважаемый фолопидор "