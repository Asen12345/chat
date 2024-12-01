package TeraAI.RESTAPI


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import redis.clients.jedis.Jedis
import service.example.com.Chats.DB.RedisConfig

fun Application.getChats() {
    val redisClient = RedisConfig.getClient()

    routing {
        // GET запрос для получения всех чатов пользователя
        get("/get/chats/{uid}") {
            val uid = call.parameters["uid"]

            if (uid == null) {
                call.respond(HttpStatusCode.BadRequest, "uid не предоставлен.")
                return@get
            }

            // Извлекаем все чаты для пользователя из Redis
            val redisKey = "user_chats:$uid"
            try {
                val userChats = redisClient.hgetAll(redisKey)

                if (userChats.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound, "Чаты для пользователя '$uid' не найдены.")
                } else {
                    // Возвращаем список чатов
                    val chatList = userChats.map { (chatId, chatName) ->
                        ChatInfo(chatId, chatName)
                    }
                    call.respond(HttpStatusCode.OK, chatList)
                }
            } catch (e: Exception) {
                println("Ошибка при извлечении данных из Redis: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Ошибка при извлечении чатов.")
            }
        }
    }
}

// Data class для отображения информации о чате
@Serializable
data class ChatInfo(val chatId: String, val chatName: String)


