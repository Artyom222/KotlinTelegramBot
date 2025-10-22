package org.example

fun main(args: Array<String>) {
    val botToken = args[0]
    val telegramBotService = TelegramBotService()
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates = telegramBotService.getUpdates(botToken, updateId)
        println(updates)

        if (updates.contains("\"result\":[]")) {
            continue
        }

        val updateIdRegex: Regex = "\"update_id\":(\\d+),".toRegex()
        val matchResultUpdateId: MatchResult? = updateIdRegex.find(updates)
        val groupsUpdateId = matchResultUpdateId?.groups
        val updateIdString = groupsUpdateId?.get(1)?.value
        updateId = updateIdString?.toInt()?.plus(1) ?: 0

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val textMessage = groups?.get(1)?.value
        println(textMessage)

        val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
        val matchResultChatId: MatchResult? = chatIdRegex.find(updates)
        val groupsChatId = matchResultChatId?.groups
        val chatIdString = groupsChatId?.get(1)?.value
        val chatId = chatIdString?.toInt() ?: 0

        if (textMessage.equals("Hello", true) && chatId != 0) {
            telegramBotService.sendMessage(botToken, chatId, "Hello")
        }
    }
}