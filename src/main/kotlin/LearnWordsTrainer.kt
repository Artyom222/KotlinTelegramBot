package org.example

import java.io.File

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
)

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

class LearnWordsTrainer {

    private var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWERS_TO_LEARN }.size
        var percent = 0
        if (totalCount > 0) {
            percent = (learnedCount * 100 / totalCount)
        }
        return Statistics(learnedCount, totalCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < CORRECT_ANSWERS_TO_LEARN }
        if (notLearnedList.isEmpty()) return null
        var questionWords = notLearnedList.shuffled().take(OPTIONS_COUNT)
        val questionWord = questionWords.random()
        if (questionWords.size < OPTIONS_COUNT) {
            val shortageCount = OPTIONS_COUNT - questionWords.size
            val shortageList =
                dictionary.filter { it.correctAnswersCount >= CORRECT_ANSWERS_TO_LEARN }.take(shortageCount)
            questionWords = questionWords.plus(shortageList)
        }
        val shuffledAnswers = questionWords.shuffled()
        question = Question(variants = shuffledAnswers, questionWord)
        return question

    }

    fun checkAnswer(userAnswerInput: Int): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer) + 1
            if (correctAnswerId == userAnswerInput) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else false
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        try {
            val dictionary = mutableListOf<Word>()
            val wordsFile = File("words.txt")
            wordsFile.readLines()
                .forEach { line ->
                    val parts = line.split("|")
                    val correctAnswersCount: Int = parts[2].toIntOrNull() ?: 0
                    val word = Word(parts[0], parts[1], correctAnswersCount)
                    dictionary.add(word)
                }
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException("некорректный файл")
        }

    }

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File("words.txt")
        val content = dictionary.joinToString("\n") { word ->
            "${word.original}|${word.translate}|${word.correctAnswersCount}"
        }
        wordsFile.writeText(content)
    }

}