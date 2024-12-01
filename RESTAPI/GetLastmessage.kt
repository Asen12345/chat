package TeraAI.RESTAPI

import DynamicTableGIGACHATManager

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.getMessagesToChat() {
    val tableManager = DynamicTableGIGACHATManager()

    routing {
        get("/get/messages/{uid}/{chatId}") {
            val chatId = call.parameters["chatId"]
            val uid = call.parameters["uid"]
            val tableName = "GIGACHAT_$chatId"

            if (chatId == null || uid == null) {
                call.respond(HttpStatusCode.BadRequest, "chatId или uid не предоставлены.")
                return@get
            }

            try {
                // Извлекаем все сообщения из базы данных
                val messages = tableManager.fetchAllData(tableName)

                // Проверяем, если список сообщений пустой
                if (messages.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent, "Нет сообщений в чате.")
                } else {
                    // Отправляем список сообщений в ответе
                    call.respond(HttpStatusCode.OK, messages)
                }
            } catch (e: Exception) {
                println("Ошибка при извлечении сообщений: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Ошибка при извлечении сообщений.")
            }
        }
    }
}
