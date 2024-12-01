package TeraAI

import org.jetbrains.exposed.sql.Table

object BanUser : Table("gigachat.Usertable") {
    val user_uid = varchar("uid", 128)
    val chatId = varchar("chatId", 256)

}

