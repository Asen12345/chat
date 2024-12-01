package com.ilya.myspb.android.ChatwithBot.ModelData

import kotlinx.serialization.Serializable

@Serializable
data class ChatInfo(
    val chatId: String,
    val chatName: String
)

data class ChatRequest(
    val categories: String,
    val model: String
)
