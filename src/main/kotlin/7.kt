package org.aoc

import java.io.File
import java.util.PriorityQueue
import java.util.stream.IntStream

object CamelCards {

    private val cardValues = mapOf(
        '2' to 2,
        '3' to 3,
        '4' to 4,
        '5' to 5,
        '6' to 6,
        '7' to 7,
        '8' to 8,
        '9' to 9,
        'T' to 10,
        'J' to 11,
        'Q' to 12,
        'K' to 13,
        'A' to 14
    )

    enum class Rank {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND,
    }

    data class Hand(val cards: String, val bid: Int, var rank: Rank)

    fun parseHand(hand: String): Hand {
        val (cards, bid) = hand.split(" ").let { it[0] to it[1].toInt() }
        return Hand(cards, bid, rank(cards))
    }

    private fun rank(hand: String): Rank {
        val cardCounts = hand.groupingBy { it }.eachCount()
        val sortedCardCounts = cardCounts.toList().sortedByDescending { it.second }
        return when (sortedCardCounts.size) {
            5 -> {
                Rank.HIGH_CARD
            }

            4 -> {
                Rank.ONE_PAIR
            }

            3 -> {
                if (sortedCardCounts[0].second == 3) {
                    Rank.THREE_OF_A_KIND
                } else {
                    Rank.TWO_PAIR
                }
            }

            2 -> {
                if (sortedCardCounts[0].second == 4) {
                    Rank.FOUR_OF_A_KIND
                } else {
                    Rank.FULL_HOUSE
                }
            }

            else -> {
                Rank.FIVE_OF_A_KIND
            }
        }
    }

    private val handComparator: java.util.Comparator<Hand>
        get() {
            val comparator = Comparator<Hand> { a, b ->
                println("Comparing hands a: ${a.cards} b: ${b.cards}")
                if (a.rank != b.rank) {
                    val compareTo = a.rank.compareTo(b.rank)
                    println("Different ranks a: ${a.rank} b: ${b.rank}, result: ${if (compareTo > 0) "a" else "b"} wins")
                    compareTo
                } else {
                    println("Same ranks a: ${a.rank}, b: ${b.rank}, comparing cards...")
                    val aCards = a.cards.toCharArray()
                    val bCards = b.cards.toCharArray()
                    for (i in 0 until 5) {
                        if (cardValues[aCards[i]] != cardValues[bCards[i]]) {
                            val compareTo = cardValues[aCards[i]]!!.compareTo(cardValues[bCards[i]]!!)
                            println("${i + 1} cards (a: ${aCards[i]}, b: ${bCards[i]}) are different, result: ${if (compareTo > 0) "a" else "b"} wins")
                            return@Comparator compareTo
                        }
                    }
                    println("All cards are the same, result: tie")
                    0
                }
            }
            return comparator
        }

    fun first(path: String): Long {
        return PriorityQueue(handComparator).apply {
            addAll(File(path).readLines().map { parseHand(it) })
        }.also {
            println(" - - - Hands: $it\n")
        }.let {
            IntStream.range(0, it.size).mapToObj { i -> (i + 1) * it.poll()
                .also { hand -> println("- - - Card $i: $hand") }.bid.toLong() }
        }.reduce(Long::plus).get()
    }
}