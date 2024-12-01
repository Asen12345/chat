package TeraAI.RESTAPI

import DynamicTableGIGACHATManager
import example.com.DB.RedisConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Application.deleteChat() {
    val tableManager = DynamicTableGIGACHATManager()
    routing {
        delete("/delete/Chat/{uid}/{chatId}") {
            val redisClient = RedisConfig.getClient()
            val uid = call.parameters["uid"]!!
            val chatId = call.parameters["chatId"]!!

            // Удаляем chatId из хеша Redis
            val redisKey = "user_chats:$uid"
            redisClient.hdel(redisKey, chatId)  // Удаление поля из хеша

            // Также можно удалить сам ключ, если он больше не нужен
            // redisClient.del(redisKey)  // Раскомментируйте, если хотите удалить сам ключ

            // Удаление таблицы
            val tableName = "GIGACHAT_$chatId"
            tableManager.dropTable(tableName)

            call.respond(HttpStatusCode.OK, "Chat deleted successfully")
        }
    }
}

