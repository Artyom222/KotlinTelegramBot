//package org.example
//
//import kotlinx.serialization.SerialName
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//
//@Serializable
//data class Update(
//    @SerialName("update_id")
//    val updateId: Long,
//    @SerialName("message")
//    val message: Message? = null,
//    @SerialName("callback_query")
//    val callbackQuery: CallbackQuery? = null,
//)
//
//@Serializable
//data class Response(
//    @SerialName("result")
//    val result: List<Update>,
//)
//
//@Serializable
//data class Message(
//    @SerialName("text")
//    val text: String,
//)
//
//@Serializable
//data class CallbackQuery(
//    @SerialName("data")
//    val data: String,
//)
//
//fun main() {
//
//    val json = Json {
//        ignoreUnknownKeys = true
//    }
//
//    val responseString = """
//        {
//          "ok": true,
//          "result": [
//            {
//              "update_id": 686618720,
//              "message": {
//                "message_id": 203,
//                "from": {
//                  "id": 1423123199,
//                  "is_bot": false,
//                  "first_name": "Артемий",
//                  "username": "Artishok722",
//                  "language_code": "ru"
//                },
//                "chat": {
//                  "id": 1423123199,
//                  "first_name": "Артемий",
//                  "username": "Artishok722",
//                  "type": "private"
//                },
//                "date": 1763294196,
//                "text": "/start",
//                "entities": [
//                  {
//                    "offset": 0,
//                    "length": 6,
//                    "type": "bot_command"
//                  }
//                ]
//              }
//            }
//        ,
//        {
//              "update_id": 686618721,
//              "callback_query": {
//                "id": "6112267601539233415",
//                "from": {
//                  "id": 1423123199,
//                  "is_bot": false,
//                  "first_name": "Артемий",
//                  "username": "Artishok722",
//                  "language_code": "ru"
//                },
//                "message": {
//                  "message_id": 204,
//                  "from": {
//                    "id": 8250053219,
//                    "is_bot": true,
//                    "first_name": "English Words Learning Bot",
//                    "username": "EnglishWordsMyProjectBot"
//                  },
//                  "chat": {
//                    "id": 1423123199,
//                    "first_name": "Артемий",
//                    "username": "Artishok722",
//                    "type": "private"
//                  },
//                  "date": 1763294197,
//                  "text": "Основное меню",
//                  "reply_markup": {
//                    "inline_keyboard": [
//                      [
//                        {
//                          "text": "Изучить слова",
//                          "callback_data": "learn_words_clicked"
//                        },
//                        {
//                          "text": "Статистика",
//                          "callback_data": "statistics_clicked"
//                        }
//                      ]
//                    ]
//                  }
//                },
//                "chat_instance": "-6275621436176938343",
//                "data": "statistics_clicked"
//              }
//            }
//          ]
//        }
//    """.trimIndent()
//
//    val response = json.decodeFromString<Response>(responseString)
//    println(response)
//
//}