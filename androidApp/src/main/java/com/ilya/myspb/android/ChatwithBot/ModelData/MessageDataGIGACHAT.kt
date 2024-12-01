package com.ilya.myspb.android.ChatwithBot.ModelData

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val username: String,
    val message: String,
    var gptResponse: String,
    val timestamp: String
)