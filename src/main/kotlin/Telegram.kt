package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)


fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBotService = TelegramBotService(botToken)
    var lastUpdateId = 0L
    val trainer = LearnWordsTrainer()

    val json = Json {
        ignoreUnknownKeys = true
    }

    while (true) {
        Thread.sleep(2000)
        val responseString = telegramBotService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1

        val textMessage = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val data = firstUpdate.callbackQuery?.data

        if (textMessage.equals("Hello", true)) {
            telegramBotService.sendMessage(json, chatId, "Hello")
        }

        if (textMessage.equals("/start", true)) {
            telegramBotService.sendMenu(json, chatId)
        }

        if (data.equals(STATISTICS_CLICKED, true)) {
            val statistics =
                with(trainer.getStatistics()) {
                    "Выучено $learned из $total слов | $percent%"
                }
            telegramBotService.sendStatistics(json, chatId, statistics)
        }

        if (data.equals(LEARN_WORDS_CLICKED, true)) {
            val question = trainer.getNextQuestion()
            if (question == null) {
                telegramBotService.sendMessage(json, chatId, "Все слова выучены!")
                break
            }
            telegramBotService.sendQuestion(json, chatId, question)

        }

        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (trainer.checkAnswer(userAnswerIndex)) {
                telegramBotService.sendMessage(json, chatId, "Правильно!")
            } else {
                telegramBotService.sendMessage(
                    json, chatId, "Неправильно! ${
                        trainer.question?.correctAnswer?.original
                    } – это ${
                        trainer.question?.correctAnswer?.translate
                    }"
                )
            }
            val nextQuestion = trainer.getNextQuestion()
            if (nextQuestion == null) {
                telegramBotService.sendMessage(json, chatId, "Все слова выучены!")
                break
            } else {
                telegramBotService.sendQuestion(json, chatId, nextQuestion)
            }

        }

        if (data.equals(BACK_TO_MENU_CLICKED, true)) {
            telegramBotService.sendMenu(json, chatId)
        }

    }
}