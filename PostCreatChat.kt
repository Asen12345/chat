package TeraAI

import DynamicTableGIGACHATManager
import TeraAI.GIGAchatAI.Postquashen
import TeraAI.GIGAchatAI.extractContent
import com.google.auto.value.extension.serializable.SerializableAutoValue
import example.com.DB.RedisConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlin.system.measureTimeMillis


import io.ktor.server.response.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import redis.clients.jedis.exceptions.JedisConnectionException
import kotlin.system.measureTimeMillis

@Serializable
data class CreateChatRequest(
    val categories: String,
    val model: String
)

// Функция для создания таблицы через HTTP POST запрос
fun Application.postCreateChat() {

    val redisClient = RedisConfig.getClient()

    val tableManager = DynamicTableGIGACHATManager()

    routing {
        post("/create/chat/{uid}/{chatId}") {
            val request = call.receive<CreateChatRequest>()
            val chatId = call.parameters["chatId"]
            val uid = call.parameters["uid"]

            if (chatId == null || uid == null) {
                call.respond(HttpStatusCode.BadRequest, "chatId или uid не предоставлены.")
                return@post
            }


            val tableName = "GIGACHAT_$chatId"

            // Замеряем время выполнения операции для логирования
            val tableCheckTime = measureTimeMillis {
                try {

                    val redisKey = "user_chats:$uid"

                    var chatName= extractContent( Postquashen(request.categories, "Ты играешь выгру твоя задача , определить кратко название чата по его Категории: нужно уложиться от 2 до 6 слов ",  request.model))

                    // Сохраняем chatId и chatName в хеш по ключу redisKey
                    redisClient.hset(redisKey, chatId, chatName)

                    // Устанавливаем срок жизни для этого ключа (3 дня = 259200 секунд)
                    redisClient.expire(redisKey, 259200)

                    // Проверяем и создаем таблицу асинхронно
                    val tableCreated = withContext(Dispatchers.IO) {
                        tableManager.createTable(tableName)
                    }

                    if (tableCreated) {
                        println("[$tableName] Таблица не существовала и была создана.")
                        call.respond(HttpStatusCode.Created, "Таблица $tableName успешно создана.")
                    } else {
                        println("[$tableName] Таблица уже существует.")
                        call.respond(HttpStatusCode.OK, "Таблица $tableName уже существует.")
                    }
                } catch (e: Exception) {
                    println("[$tableName] Ошибка проверки/создания таблицы: ${e.message}")
                    call.respond(HttpStatusCode.InternalServerError, "Ошибка создания таблицы: ${e.message}")
                }
            }

            println("Время выполнения проверки/создания таблицы: $tableCheckTime мс")
        }
    }
}

