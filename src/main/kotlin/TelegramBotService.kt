package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val BACK_TO_MENU_CLICKED = "back_to_menu_clicked"
const val STATISTICS_CLICKED = "statistics_clicked"
const val API_TELEGRAM = "https://api.telegram.org/bot"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val RESET_CLICKED = "reset_clicked"

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyBoard>>,
)

@Serializable
data class InlineKeyBoard(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)

class TelegramBotService(private val botToken: String, private val json: Json) {

    val client = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "$API_TELEGRAM$botToken/getUpdates?offset=$updateId"
        val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        return try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            response.body()
        } catch (e: Exception) {
            println("Ошибка. ${e.message}")
            "{\"result\":[]}"
        }

    }

    fun sendMessage(chatId: Long, text: String): String {

        val urlSendMessage = "$API_TELEGRAM$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = text
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        return try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            response.body()
        } catch (e: Exception) {
            println("Ошибка. ${e.message}")
            "{\"result\":[]}"
        }
    }

    fun sendQuestion(chatId: Long, question: Question) {
        val urlSendMessage = "$API_TELEGRAM$botToken/sendMessage"
        val text = "Выбери правильный перевод слова:\n${question.correctAnswer.original}"

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = text,
            replyMarkup = ReplyMarkup(
                listOf(
                    question.variants.mapIndexed { index, word ->
                        InlineKeyBoard(
                            word.translate, "${CALLBACK_DATA_ANSWER_PREFIX}${index + 1}"
                        )
                    },
                    listOf(
                        InlineKeyBoard("Вернуться в меню", BACK_TO_MENU_CLICKED),
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            println("Ошибка. ${e.message}")
            "{\"result\":[]}"
        }

    }

    fun sendMenu(chatId: Long) {
        val urlSendMessage = "$API_TELEGRAM$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Оcновное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyBoard("Изучить слова", LEARN_WORDS_CLICKED),
                        InlineKeyBoard("Статистика", STATISTICS_CLICKED),
                    ),
                    listOf(
                        InlineKeyBoard("Сбросить прогресс", RESET_CLICKED),
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            println("Ошибка. ${e.message}")
            "{\"result\":[]}"
        }
    }

    fun sendStatistics(chatId: Long, statistics: String) {
        val urlSendMessage = "$API_TELEGRAM$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = statistics,
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyBoard("Вернуться в меню", BACK_TO_MENU_CLICKED),
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            println("Ошибка. ${e.message}")
            "{\"result\":[]}"
        }

    }
}