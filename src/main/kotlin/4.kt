package org.aoc

import java.io.File
import kotlin.math.pow

object Scratchcards {

    fun first(path: String): Int {
        return File(path).readLines().stream()
            .map { line ->
                line.split('|').run {
                    val winning = mutableSetOf<Int>()
                    val scratched = mutableSetOf<Int>()
                    this[0].split(':').run {
                        parseToSet(winning, this[1])
                    }
                    parseToSet(scratched, this[1])
                    winning.intersect(scratched)
                }
            }
            .map { it.count() }
            .map { 2.0.pow(it - 1).toInt() }
            .reduce(Int::plus).get()
    }

    private fun parseToSet(winning: MutableSet<Int>, numbers: String) =
        numbers.split(' ')
            .mapNotNull {
                try {
                    it.toInt()
                } catch (e: NumberFormatException) {
                    null
                }
            }
            .toCollection(winning)
}