package org.aoc

import java.io.File
import kotlin.math.pow

object Scratchcards {

    data class Card(val number: Int, val wins: Int)

    fun first(path: String): Int {
        return File(path).readLines().stream()
            .map { line -> wins(line) }
            .map { it.count() }
            .map { 2.0.pow(it - 1).toInt() }
            .reduce(Int::plus).get()
    }

    /** ## Bing
    Certainly! Let's analyze the second function and identify potential areas for improvement. I'll provide suggestions to enhance its performance and readability.
    - Caching Optimization:
    - The current implementation uses a mutable map called cache to store previously computed results. While this is a good approach, consider using a more efficient data structure like an array (if the card numbers are within a reasonable range).
    - Instead of using a mutable map, create an array where each index corresponds to a card number. Initialize it with zeros, and update the array as you compute results for each card. This avoids the overhead of map operations.
    - Example:

    val cache = IntArray(maxCardNumber + 1) // Initialize with zeros

    - Tail Recursion Optimization:
    - The count function is recursive, but it's not tail-recursive. Consider rewriting it as a tail-recursive function to avoid stack overflow issues for large inputs.
    - Use an accumulator parameter to accumulate the result during recursion.
    - Example:

    tailrec fun count(card: Card?, accumulator: Int = 0): Int {
    if (card == null) return accumulator
    // Rest of your logic
    // ...
    return count(cache[i], accumulator + countForCard)
    }

    - Avoid Redundant Computations:
    - The count function computes the same value multiple times for the same card number. Optimize this by storing the result in the cache once it's computed.
    - Example:

    val cachedCount = cache[i]
    if (cachedCount != 0) {
    count += cachedCount
    } else {
    count += count(cache[i])
    cache[i] = count // Store the computed result in the cache
    }

    - Streamlining the Pipeline:
    - The pipeline of operations involving map, onEach, and reduce can be simplified.
    - Instead of using mapIndexed followed by onEach, combine them into a single map operation.
    - Example:

    val cards = File(path).readLines()
    .mapIndexed { index, line -> Card(index + 1, wins(line).count()) }
    cards.forEach { cache.putIfAbsent(it.number, it) }
    return cards.sumBy { count(it) }

    - Readability and Naming:
    - Consider using more descriptive variable names. For example, replace count with totalProducedCards.
    - Make sure the function and variable names convey their purpose clearly.
    Remember to test the modified code thoroughly to ensure correctness and performance improvements. 🚀
     */
    fun second(path: String): Int {
        val cache = mutableMapOf<Int, Card>()
        fun count(card: Card?): Int {
            if (card == null) return 0
            println("Counting card ${card.number} with ${card.wins} wins...")
            if (card.wins == 0) return 1
            var count = 1
            val start = card.number + 1
            val end = card.number + card.wins
            for (i in start..end) {
                count += count(cache[i])
            }
            return count
        }
        return File(path).readLines()
            .mapIndexed { index, line -> Card(index + 1, wins(line).count()) }
            .onEach { cache.putIfAbsent(it.number, it) }
            .map {
                count(it).also { produce ->
                    println("Card ${it.number} with ${it.wins} wins produced $produce more card(s)")
                }
            }
            .reduce(Int::plus)
    }


    private fun wins(line: String): Iterable<Int> {
        return line.split('|').run {
            val winning = mutableSetOf<Int>()
            val scratched = mutableSetOf<Int>()
            this[0].split(':').run {
                parseToSet(winning, this[1])
            }
            parseToSet(scratched, this[1])
            winning.intersect(scratched)
        }
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