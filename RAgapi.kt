package TeraAI

import TeraAI.GIGAchatAI.NotificationSender
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

import okhttp3.*

import javax.net.ssl.*

import okhttp3.*
import org.json.JSONObject


    fun PostRAG(prompt: String): String {
        val tokenCommander = NotificationSender()
        val token = tokenCommander.getToken()

        val trustAllCertificates: Array<TrustManager> = arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, trustAllCertificates, java.security.SecureRandom())
        }

        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCertificates[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        val jsonBody = """
            {
                "query": "$prompt"
            }
        """.trimIndent()

        val body = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("https://18c1-88-201-202-184.ngrok-free.app/query")
            .post(body)
            .build()

        return try {
            val response: Response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "Нет тела ответа"

            if (response.isSuccessful) {
                // Парсинг JSON и извлечение строки "response"
                val jsonObject = JSONObject(responseBody)
                jsonObject.getString("response")  // Возвращаем только значение поля "response"
            } else {
                throw Exception("Ошибка: ${response.code} - $responseBody")
            }
        } catch (e: Exception) {
            throw Exception("Запрос не удался: ${e.message}", e)
        }
    }


fun main() {
    embeddedServer(Netty, port = 8047) {
        routing {
            get("/ask") {
                try {
                    // Получаем тело запроса как строку
                    val requestBody = "Где найти информацию о социальных услугах в Санкт-Петербурге?"

                    // Вызываем функцию Postquashen с телом запроса
                    val response = PostRAG(requestBody)

                    // Отправляем ответ клиенту
                    call.respondText(response, status = HttpStatusCode.OK)
                } catch (e: Exception) {
                    // Возвращаем ошибку клиенту в виде строки
                    call.respondText(
                        "Ошибка: ${e.message}",
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }
    }.start(wait = true)
}