package org.aoc

import java.io.File

val wordsToDigits = mapOf(
    "one" to "o1e",
    "two" to "t2o",
    "three" to "t3e",
    "four" to "f4r",
    "five" to "f5e",
    "six" to "s6x",
    "seven" to "s7n",
    "eight" to "e8t",
    "nine" to "n9n",
)

object Trebuchet {
    fun first(path: String) = File(path).readLines()
        .stream()
        .map {
            var first: Char? = null
            for (i in it.indices)
                if (it[i].isDigit()) {
                    first = it[i]
                    break
                }
            var second: Char? = null
            for (i in it.length - 1 downTo 0)
                if (it[i].isDigit()) {
                    second = it[i]
                    break
                }
            StringBuilder().append(first).append(second).toString().toInt()
        }.reduce(Int::plus).get()

    fun second(path: String): Int = File(path).readLines()
        .stream()
        .map { replaceWords(it) }
        .map {
            var first: Char? = null
            for (i in it.indices)
                if (it[i].isDigit()) {
                    first = it[i]
                    break
                }
            var second: Char? = null
            for (i in it.length - 1 downTo 0)
                if (it[i].isDigit()) {
                    second = it[i]
                    break
                }
            val res = StringBuilder().append(first).append(second).toString().toInt()
            print(it); print(" - "); print(res); println()
            res
        }.reduce(Int::plus).get()

    private fun replaceWords(s: String): String {
        val sb = StringBuilder(s)
        var start = 0
        // replace first from start
        loop@ while (start < sb.length) {
            for (word in wordsToDigits.keys) {
                if (start + word.length < sb.length) {
                    if (sb.substring(start, start + word.length) == word) {
                        sb.replace(start, start + word.length, wordsToDigits[word])
                        break@loop
                    }
                } else {
                    break@loop
                }
            }
            start++
        }
        var end = sb.length
        // replace first from end
        loop@ while (end > 0) {
            for (word in wordsToDigits.keys) {
                if (end - word.length >= 0) {
                    if (sb.substring(end - word.length, end) == word) {
                        sb.replace(end - word.length, end, wordsToDigits[word])
                        break@loop
                    }
                } else {
                    break@loop
                }
            }
            end--
        }
        return sb.toString()
    }
}