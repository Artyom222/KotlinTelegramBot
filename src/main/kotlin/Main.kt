package org.example

const val CORRECT_ANSWERS_TO_LEARN = 3
const val OPTIONS_COUNT = 4

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun Question.questionToString(): String {
    val variants = this.variants.mapIndexed { index, word ->
        "${index + 1} - ${word.translate}"
    }.joinToString("\n")
    return this.correctAnswer.original + "\n" + variants + "\n------------" + "\n0 - Меню"
}

fun main() {

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }


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
                    val question = trainer.getNextQuestion()

                    if (question == null) {
                        println("Все слова выучены!")
                        break
                    }

                    println(question.questionToString())
                    val userAnswerInput = readln().toIntOrNull() ?: 0
                    if (userAnswerInput == 0) break
                    if (trainer.checkAnswer(userAnswerInput)) {
                        println("Правильно!")
                    } else {
                        println("Неправильно! ${question.correctAnswer.original} – это ${question.correctAnswer.translate}")
                    }
                }
            }

            "2" -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%")
            }

            "0" -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }
}