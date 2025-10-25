package org.example

fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBotService = TelegramBotService()
    var updateId = 0

    val updateIdRegex: Regex = "\"update_id\":(\\d+),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates = telegramBotService.getUpdates(botToken, updateId)
        println(updates)

        if (updates.contains("\"result\":[]")) {
            continue
        }

        updateId = updateIdRegex.find(updates)?.groups?.get(1)?.value?.toInt()?.plus(1) ?: continue
        val textMessage = messageTextRegex.find(updates)?.groups?.get(1)?.value
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toInt() ?: 0
        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        if (textMessage.equals("Hello", true) && chatId != 0) {
            telegramBotService.sendMessage(botToken, chatId, "Hello")
        }

        if (textMessage.equals("menu", true) && chatId != 0) {
            telegramBotService.sendMenu(botToken, chatId)
        }

        if (data.equals("statistics_clicked", true) && chatId != 0) {
            telegramBotService.sendMessage(botToken, chatId, "Показать статистику")
        }
    }
}