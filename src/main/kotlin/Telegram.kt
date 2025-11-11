package org.example

fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBotService = TelegramBotService(botToken)
    var updateId = 0
    val trainer = LearnWordsTrainer()

    val updateIdRegex: Regex = "\"update_id\":(\\d+),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates = telegramBotService.getUpdates(updateId)
        println(updates)

        if (updates.contains("\"result\":[]")) {
            continue
        }

        updateId = updateIdRegex.find(updates)?.groups?.get(1)?.value?.toInt()?.plus(1) ?: continue
        val textMessage = messageTextRegex.find(updates)?.groups?.get(1)?.value
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toInt() ?: continue
        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        if (textMessage.equals("Hello", true)) {
            telegramBotService.sendMessage(chatId, "Hello")
        }

        if (textMessage.equals("/start", true)) {
            telegramBotService.sendMenu(chatId)
        }

        if (data.equals(STATISTICS_CLICKED, true)) {
            val statistics =
                with(trainer.getStatistics()) {
                    "Выучено $learned из $total слов | $percent%"
                }
            telegramBotService.sendStatistics(chatId, statistics)
        }

        if (data.equals(LEARN_WORDS_CLICKED, true)) {
            val question = trainer.getNextQuestion()
            if (question == null) {
                telegramBotService.sendMessage(chatId, "Все слова выучены!")
                break
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
            trainer.question = trainer.getNextQuestion()
            if (trainer.question == null) {
                telegramBotService.sendMessage(chatId, "Все слова выучены!")
                break
            }
            telegramBotService.sendQuestion(chatId, trainer.question!!)
        }

        if (data.equals(BACK_TO_MENU_CLICKED, true)) {
            telegramBotService.sendMenu(chatId)
        }

    }
}