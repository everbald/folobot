package com.everbald.folobot.domain.type

enum class UpdateField(val fieldName: String) {
    UPDATEID("update_id"),
    MESSAGE("message"),
    INLINEQUERY("inline_query"),
    CHOSENINLINEQUERY("chosen_inline_result"),
    CALLBACKQUERY("callback_query"),
    EDITEDMESSAGE("edited_message"),
    CHANNELPOST("channel_post"),
    EDITEDCHANNELPOST("edited_channel_post"),
    SHIPPING_QUERY("shipping_query"),
    PRE_CHECKOUT_QUERY("pre_checkout_query"),
    POLL("poll"),
    POLLANSWER("poll_answer"),
    MYCHATMEMBER("my_chat_member"),
    CHATMEMBER("chat_member"),
    CHATJOINREQUEST("chat_join_request"),
    MESSAGE_REACTION("message_reaction"),
    MESSAGE_REACTION_COUNT("message_reaction_count"),
    CHAT_BOOST("chat_boost"),
    REMOVED_CHAT_BOOST("removed_chat_boost"),
}