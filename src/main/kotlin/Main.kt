package org.example

import java.io.File

const val CORRECT_ANSWERS_TO_LEARN = 3
const val OPTIONS_COUNT = 4

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

    fun saveDictionary(dictionary: List<Word>) {
        val content = dictionary.joinToString("\n") { word ->
            "${word.original}|${word.translate}|${word.correctAnswersCount}"
        }
        wordsFile.writeText(content)
    }

    val dictionary = loadDictionary()

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
            "1" -> {
                while (true) {
                    val notLearnedList = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWERS_TO_LEARN }
                    if (notLearnedList.isEmpty()) {
                        println("Все слова выучены!")
                        continue
                    }
                    var questionWords = notLearnedList.shuffled().take(OPTIONS_COUNT)
                    val questionWord = questionWords.random()
                    if (questionWords.size < OPTIONS_COUNT) {
                        val shortageCount = OPTIONS_COUNT - questionWords.size
                        val shortageList =
                            dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWERS_TO_LEARN }.take(shortageCount)
                        questionWords = questionWords.plus(shortageList)
                    }
                    val shuffledAnswers = questionWords.map { it.translate }.shuffled()
                    val correctAnswerId = shuffledAnswers.indexOf(questionWord.translate) + 1
                    println("${questionWord.original}:")
                    for (i in 0 until OPTIONS_COUNT) {
                        println("${i + 1} - ${shuffledAnswers[i]}")
                    }
                    println("----------\n" + "0 - Меню")
                    val userAnswerInput = readln().toIntOrNull() ?: 0
                    if (userAnswerInput == correctAnswerId) {
                        println("Правильно!")
                        dictionary[dictionary.indexOf(questionWord)].correctAnswersCount++
                        saveDictionary(dictionary)
                    } else if (userAnswerInput == 0) {
                        break
                    } else {
                        println("Неправильно! ${questionWord.original} – это ${questionWord.translate}")
                    }
0
                }

            }

            "2" -> {
                val totalCount = dictionary.size
                val learnedCount = dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWERS_TO_LEARN }.size
                if (totalCount > 0) {
                    val percent = (learnedCount * 100 / totalCount)
                    println("Выучено $learnedCount из $totalCount слов | $percent%")
                    continue
                } else println("Словарь пустой")
            }

            "0" -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }

}