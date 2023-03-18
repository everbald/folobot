package com.everbald.folobot.event

import org.telegram.telegrambots.meta.api.objects.Update

data class UpdateReceivedEvent(val update: Update)