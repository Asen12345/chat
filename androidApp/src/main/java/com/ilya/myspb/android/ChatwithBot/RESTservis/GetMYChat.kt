package com.ilya.myspb.android.ChatwithBot.RESTservis

import android.util.Log

import com.ilya.myspb.android.ChatwithBot.Interface.ApiService
import com.ilya.myspb.android.ChatwithBot.ModelData.ChatInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

suspend fun getPublicMarker(uid: String): List<ChatInfo> {
    // Логирование запросов для диагностики
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Клиент с увеличенными тайм-аутами
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS) // время подключения
        .readTimeout(30, TimeUnit.SECONDS)    // время чтения ответа
        .writeTimeout(30, TimeUnit.SECONDS)   // время записи данных
        .build()

    // Инициализация Retrofit с OkHttpClient и базовым URL
    val retrofit = Retrofit.Builder()
        .baseUrl("https://meetmap.up.railway.app/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    Log.d("MapMarker_getMarker", "URL запроса: https://meetmap.up.railway.app/get/chats/$uid")

    // Создание экземпляра интерфейса API
    val apiService = retrofit.create(ApiService::class.java)

    // Выполнение запроса в фоновом потоке
    return withContext(Dispatchers.IO) {
        try {
            val markerData = apiService.getChatInfo(uid)
            Log.d("MapMarker_getMarker", "Полученные данные маркера: $markerData")
            markerData
        } catch (e: HttpException) {
            Log.e("MapMarker_getMarker", "HTTP ошибка: ${e.code()} - ${e.response()?.errorBody()?.string()}", e)
            throw e
        } catch (e: IOException) {
            Log.e("MapMarker_getMarker", "Сетевая ошибка", e)
            throw e
        } catch (e: Exception) {
            Log.e("MapMarker_getMarker", "Неизвестная ошибка", e)
            throw e
        }
    }
}