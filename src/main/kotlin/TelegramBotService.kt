package org.example

import java.net.URLEncoder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val STATISTICS_CLICKED = "statistics_clicked"
const val API_TELEGRAM = "https://api.telegram.org/bot"

class TelegramBotService(private val botToken: String) {

    val trainer = LearnWordsTrainer()
    val client = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$API_TELEGRAM$botToken/getUpdates?offset=$updateId"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: Int, text: String) {
        val encodedText = URLEncoder.encode(text, "UTF-8")
        val urlSendMessage = "$API_TELEGRAM$botToken/sendMessage?chat_id=$chatId&text=$encodedText"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun sendMenu(chatId: Int) {
        val urlSendMessage = "$API_TELEGRAM$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": "$LEARN_WORDS_CLICKED"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$STATISTICS_CLICKED"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    }
}