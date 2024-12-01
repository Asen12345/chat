package TeraAI.RESTAPI

import DynamicTableGIGACHATManager
import TeraAI.DatatoaskGIGACHAT

import TeraAI.GIGAchatAI.Postquashen
import TeraAI.GIGAchatAI.extractContent
import TeraAI.MessageDataGIGACHAT
import TeraAI.PostRAG

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.system.measureTimeMillis

// hello
fun Application.postMessagesToChat() {
    // Подключение к базе данных (нужно предварительно настроить соединение с базой данных)

    val tableManager = DynamicTableGIGACHATManager()




    routing {
        post("/create/messages/{uid}/{chatId}") {
            val request = call.receive<DatatoaskGIGACHAT>()
            val chatId = call.parameters["chatId"]
            val uid = call.parameters["uid"]
            val tableName = "GIGACHAT_$chatId"

            if (chatId == null || uid == null) {
                call.respond(HttpStatusCode.BadRequest, "chatId или uid не предоставлены.")
                return@post
            }



            // Преобразуем каждый элемент из запроса в MessageDataGIGACHAT и вставляем в базу данных
            try {



                val rag = async {
                    // Выполняем запрос PostRAG
                    PostRAG(request.message)
                }.await()  // Ожидаем выполнения операции и получения результата

                // После выполнения PostRAG, выполняем Postquashen
                val answera = Postquashen(
                     "На основе полученной информации оставьте вопрос: ${rag}. Не упустите номера и адреса: ${request.message}",
                     request.character,
                    request.model
                )

                // Выводим результат или используем дальше

                val answear = MessageDataGIGACHAT(
                    username = request.username,
                    message = request.message,
                    gptResponse = "$answera",
                    timestamp = request.timestamp,
                )

                    tableManager.insertData(tableName, answear)

                call.respond(HttpStatusCode.Created, "$answear Сообщения успешно добавлены в таблицу.")
            } catch (e: Exception) {
                println("Ошибка при добавлении сообщений: ${e.message}")
                call.respond(HttpStatusCode.InternalServerError, "Ошибка при добавлении сообщений.")
            }
        }
    }
}