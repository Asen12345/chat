package TeraAI.GIGAchatAI

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

 fun Postquashen(promt: String, heracter: String, model:String): String {
    val tokencomander = NotificationSender()

    var token = tokencomander.getToken() // Создание кастомного TrustManager, который игнорирует ошибки сертификатов
    val trustAllCertificates: TrustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            // Пропускаем проверку
        }

        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            // Пропускаем проверку
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    // Создание SSLContext с кастомным TrustManager
    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(null, arrayOf(trustAllCertificates), java.security.SecureRandom())

    // Создание SSLSocketFactory для клиента
    val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

    // Создание OkHttpClient с игнорированием SSL-сертификатов
    val client = OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustAllCertificates as X509TrustManager)
        .hostnameVerifier { _, _ -> true } // Отключаем проверку имени хоста
        .build()

    // Формирование тела запроса с параметром scope
    val jsonBody = """
{
  "model": "$model",
  "messages": [
    {
      "role": "system",
      "content": "$heracter"
    },
    {
      "role": "user",
      "content": "$promt"
    }
  ],
  "stream": false,
  "update_interval": 0
}
    """.trimIndent()




    val mediaType = "application/json; charset=utf-8".toMediaType()
    val body = jsonBody.toRequestBody(mediaType)

    // Создание запроса
    val request = Request.Builder()
        .url("https://gigachat.devices.sberbank.ru/api/v1/chat/completions") // URL для получения токена
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .addHeader("Accept", "application/json")
        .addHeader("Authorization", "Bearer $token") // Замените на ваш ключ авторизации
        .post(body)
        .build()



    try {
        // Отправка запроса и получение ответа
        val response: Response = client.newCall(request).execute()


        // Логируем тело ответа (даже если оно неуспешно)
        val responseBody = response.body?.string() ?: "Нет тела ответа"


        // Если ответ успешен
        if (response.isSuccessful) {
            // Логирование успешного ответа

            return responseBody
        } else {
            // Обработка ошибки, если ответ не успешен

            throw Exception("Ошибка: ${response.code} - $responseBody")
        }
    } catch (e: Exception) {
        // Логирование ошибок

        throw Exception("Запрос не удался: ${e.message}", e)
    }
}



fun main() {
    embeddedServer(Netty, port = 8068) {
        routing {
            get("/test") {
                val prompt = call.request.queryParameters["prompt"] ?: "Привте как дела кто ты " // Получаем параметр "prompt" из запроса, если он есть

                // Вызываем функцию Postquashen
                try {
                    val responseBody = Postquashen(prompt, "ты секретарь ", "GigaChat")
                    call.respondText(responseBody, contentType = ContentType.Text.Plain)
                } catch (e: Exception) {
                    call.respondText("Error: ${e.message}", status = HttpStatusCode.InternalServerError)
                }
            }
        }
    }.start(wait = true)
}
