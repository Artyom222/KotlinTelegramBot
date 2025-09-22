package org.example

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()
    val dictionary = mutableListOf<Word>()

    wordsFile.readLines()
        .forEach { line ->
            val line = line.split("|")
            val correctAnswersCount: Int = line[2].toIntOrNull() ?: 0
            val word = Word(line[0], line[1], correctAnswersCount)
            dictionary.add(word)
        }

    dictionary.forEach { println(it) }
}