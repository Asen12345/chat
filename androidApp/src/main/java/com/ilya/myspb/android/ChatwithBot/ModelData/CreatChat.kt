package com.ilya.myspb.android.ChatwithBot.ModelData

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("/create/chat/{uid}/{chatName}")
    suspend fun createChat(
        @Path("uid") uid: String,
        @Path("chatName") chatName: String,
        @Body body: ChatRequest
    ): Response<Unit> // Тип ответа можно подставить в зависимости от ожидаемого
}

object RetrofitClient {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://meetmap.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}


suspend fun sendCreateChatRequest(uid: String, chatName: String, requestBody: ChatRequest) {
    try {
        val response = RetrofitClient.apiService.createChat(uid, chatName, requestBody)
        if (response.isSuccessful) {
            // Успешно отправлено
            println("Чат успешно создан!")
        } else {
            // Ошибка
            println("Ошибка: ${response.errorBody()?.string()}")
        }
    } catch (e: Exception) {
        // Ошибка запроса
        println("Ошибка запроса: ${e.localizedMessage}")
    }
}