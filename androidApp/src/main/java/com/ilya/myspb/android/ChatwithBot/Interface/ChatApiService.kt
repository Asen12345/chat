package com.ilya.myspb.android.ChatwithBot.Interface

import com.ilya.myspb.android.ChatwithBot.ModelData.ChatMessage
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApiService {
    @GET("get/messages/{uid}/{database}")
    suspend fun getMessages(
        @Path("uid") uid: String,
        @Path("database") database: String
    ): Response<List<ChatMessage>>
}