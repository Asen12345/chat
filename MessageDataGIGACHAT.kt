package TeraAI

import kotlinx.serialization.Serializable

@Serializable
data class MessageDataGIGACHAT(
    val username: String,
    val message: String,
    var gptResponse: String,
    val timestamp: String
)

@Serializable
data class DatatoaskGIGACHAT(
    val username: String,
    val message: String,
    var gptResponse: String,
    val timestamp: String,
    val character: String,
    val model: String,
)

