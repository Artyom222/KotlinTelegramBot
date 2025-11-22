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
    val botToken = args.firstOrNull() ?: run {
        println("Не указан токен бота.")
        return
    }
    val json = Json { ignoreUnknownKeys = true }
    val telegramBotService = TelegramBotService(botToken, json)
    var lastUpdateId = 0L
    val trainers = HashMap<Long, LearnWordsTrainer>()
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val responseString = telegramBotService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, json, telegramBotService, trainers) }
        lastUpdateId = sortedUpdates.last().updateId + 1

    }
}

fun handleUpdate(
    update: Update,
    json: Json,
    telegramBotService: TelegramBotService,
    trainers: HashMap<Long, LearnWordsTrainer>,
) {

    val textMessage = update.message?.text
    val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id
    if (chatId == null) {
        println("Не удалось определить chatId для update: ${update.updateId}")
        return
    }
    val data = update.callbackQuery?.data

    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }

    if (textMessage.equals("/start", true)) {
        telegramBotService.sendMenu(chatId)
    }

    if (data == STATISTICS_CLICKED) {
        val statistics =
            with(trainer.getStatistics()) {
                "Выучено $learned из $total слов | $percent%"
            }
        telegramBotService.sendStatistics(chatId, statistics)
    }

    if (data == LEARN_WORDS_CLICKED) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            telegramBotService.sendMessage(chatId, "Все слова выучены!")
            return
        }
        telegramBotService.sendQuestion(chatId, question)

    }

    if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
        val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
        if (trainer.checkAnswer(userAnswerIndex)) {
            telegramBotService.sendMessage(chatId, "Правильно!")
        } else {
            telegramBotService.sendMessage(
                chatId, "Неправильно! ${
                    trainer.question?.correctAnswer?.original
                } – это ${
                    trainer.question?.correctAnswer?.translate
                }"
            )
        }
        val nextQuestion = trainer.getNextQuestion()
        if (nextQuestion == null) {
            telegramBotService.sendMessage(chatId, "Все слова выучены!")
        } else {
            telegramBotService.sendQuestion(chatId, nextQuestion)
        }

    }

    if (data == BACK_TO_MENU_CLICKED) {
        telegramBotService.sendMenu(chatId)
    }

    if (data == RESET_CLICKED) {
        trainer.resetProgress()
        telegramBotService.sendMessage(chatId, "Прогрессс сброшен")
    }
}