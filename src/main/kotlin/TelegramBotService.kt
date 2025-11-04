package org.example

import java.net.URLEncoder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val STATISTICS_CLICKED = "statistics_clicked"
const val API_TELEGRAM = "https://api.telegram.org/bot"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

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

    fun sendQuestion(chatId: Int, question: Question) {
        val urlSendMessage = "$API_TELEGRAM$botToken/sendMessage"
        val text = "Выбери правильный перевод слова:\n${question.correctAnswer.original}"
        val sendQuestionBody = """
            {
                "chat_id": $chatId,
                "text": "$text",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "${question.variants[0].translate}",
                                "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}0"
                            },
                            {
                                "text": "${question.variants[1].translate}",
                                "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}1"
                            },
                            {
                                "text": "${question.variants[2].translate}",
                                "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}2"
                            },
                            {
                                "text": "${question.variants[3].translate}",
                                "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}3"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()

        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendQuestionBody))
            .build()
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