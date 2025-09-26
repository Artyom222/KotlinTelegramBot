package org.example

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun main() {
    val wordsFile = File("words.txt")

    fun loadDictionary(): List<Word> {
        val dictionary = mutableListOf<Word>()
        wordsFile.readLines()
            .forEach { line ->
                val parts = line.split("|")
                val correctAnswersCount: Int = parts[2].toIntOrNull() ?: 0
                val word = Word(parts[0], parts[1], correctAnswersCount)
                dictionary.add(word)
            }
        return dictionary
    }

    val dictionary = loadDictionary()

    dictionary.forEach { println(it) }

    while (true) {
        println(
            """
            Введите пункт меню:
            1 – Учить слова
            2 – Статистика
            0 – Выход
        """.trimIndent()
        )

        when (readln()) {
            "1" -> println("Учить слова")
            "2" -> println("Статистика")
            "0" -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }

}