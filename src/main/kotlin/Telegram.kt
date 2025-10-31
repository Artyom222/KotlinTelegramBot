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

        if (data.equals(STATISTICS_CLICKED, true) && chatId != 0) {
            val statistics =
                "Выучено ${trainer.getStatistics().learned} из" +
                        " ${trainer.getStatistics().total} слов | " +
                        "${trainer.getStatistics().percent}%"
            telegramBotService.sendMessage(chatId, statistics)
        }
    }
}