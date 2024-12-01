package com.ilya.myspb.android.ChatwithBot.RESTservis

import com.ilya.myspb.android.ChatwithBot.Interface.ChatApiService
import com.ilya.myspb.android.ChatwithBot.ModelData.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Функция для получения списка сообщений с созданием Retrofit
suspend fun fetchMessages(uid: String, database: String): List<ChatMessage>? {
    return withContext(Dispatchers.IO) {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://meetmap.up.railway.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(ChatApiService::class.java)
            val response = apiService.getMessages(uid, database)
            if (response.isSuccessful) {
                response.body() // Возвращаем список сообщений
            } else {
                println("Error: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}