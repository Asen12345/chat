package TeraAI.GIGAchatAI

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jdk.internal.net.http.common.Log
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import javax.net.ssl.X509TrustManager


import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

@Serializable
data class Formattoken(
    val access_token: String,
    val expires_at: Long
)


@Serializable
data class Message(
    val content: String
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class GptResponse(
    val choices: List<Choice>
)

fun extractContent(jsonResponse: String): String {
    // Локальная настройка конфигуратора JSON
    val json = Json {
        ignoreUnknownKeys = true  // Игнорировать неизвестные ключи
    }

    // Парсим JSON в объект GptResponse с учетом настройки
    val response = json.decodeFromString<GptResponse>(jsonResponse)

    // Извлекаем содержимое первого выбора (первый элемент в choices)
    return response.choices.firstOrNull()?.message?.content ?: "Ответ не найден"
}





class NotificationSender {

    private val client: OkHttpClient
    private val gson = Gson()

    init {
        val trustAllCertificates = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCertificates, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        client = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCertificates[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    fun sendNotificationToCloud(): String? {
        val formBody = "scope=GIGACHAT_API_PERS"
            .toRequestBody("application/x-www-form-urlencoded".toMediaType())

        val request = Request.Builder()
            .url("https://ngw.devices.sberbank.ru:9443/api/v2/oauth")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Accept", "application/json")
            .addHeader("RqUID", "6f0b1291-c7f3-43c6-bb2e-9f3efb2dc98e")
            .addHeader(
                "Authorization",
                "Basic ZmY3MWUyMWQtMjNjMi00ZWNhLWI0MzYtMmVjZGJmZTFjNjY0OjAyMWZhNDRlLWU2N2MtNDgwYi04ZWNjLWNjMzRjMGU3MjRhMA=="
            )
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: return null
            if (response.isSuccessful) {
                val jsonResponse = gson.fromJson(responseBody, JsonObject::class.java)
                val token = jsonResponse["access_token"]?.asString
                TokenManager.accessToken = token  // Сохраняем токен в глобальный объект
                return token
            }
            return null
        }
    }

    fun getToken(): String? {
        // Получение токена с помощью метода sendNotificationToCloud()
        val token = sendNotificationToCloud()
        return token
    }


}

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/getToken") {
                val sender = NotificationSender()
                val accessToken = sender.sendNotificationToCloud()  // Получаем access_token
                if (accessToken != null) {
                    call.respondText("Access Token: $accessToken", ContentType.Text.Plain)
                } else {
                    call.respondText("Failed to retrieve access token", ContentType.Text.Plain)
                }
            }
        }
    }.start(wait = true)
}
