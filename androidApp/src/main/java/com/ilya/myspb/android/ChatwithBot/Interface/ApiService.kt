package com.ilya.myspb.android.ChatwithBot.Interface


import com.ilya.myspb.android.ChatwithBot.ModelData.ChatInfo
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("get/chats/{uid}")
    suspend fun getChatInfo(@Path("uid") uid: String): List<ChatInfo>
}