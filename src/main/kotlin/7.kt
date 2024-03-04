package org.aoc

import java.io.File
import java.util.*
import java.util.stream.IntStream

object CamelCards {

    class Mapper(private val jokers: Boolean = false) {
        operator fun get(index: Char): Int {
            return when (index) {
                '2' -> 2
                '3' -> 3
                '4' -> 4
                '5' -> 5
                '6' -> 6
                '7' -> 7
                '8' -> 8
                '9' -> 9
                'T' -> 10
                'J' -> if (jokers) 1 else 11
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> 0
            }
        }
    }

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

    private fun parseHand(hand: String, rankingStrategy: (String) -> Rank): Hand {
        val (cards, bid) = hand.split(" ").let { it[0] to it[1].toInt() }
        return Hand(cards, bid, rankingStrategy(cards))
    }

    class HandComparator(private val mapper: Mapper) : Comparator<Hand> {
        override fun compare(a: Hand, b: Hand): Int {
            println("Comparing hands a: ${a.cards} b: ${b.cards}")
            var comparison = 0
            if (a.rank != b.rank) {
                comparison = a.rank.compareTo(b.rank)
                println("Different ranks a: ${a.rank} b: ${b.rank}, result: ${if (comparison > 0) "a" else "b"} wins")
            } else {
                println("Same ranks a: ${a.rank}, b: ${b.rank}, comparing cards...")
                val aCards = a.cards.toCharArray()
                val bCards = b.cards.toCharArray()
                for (i in 0 until 5) {
                    if (mapper[aCards[i]] != mapper[bCards[i]]) {
                        comparison = mapper[aCards[i]] - mapper[bCards[i]]
                        println("${i + 1} cards (a: ${aCards[i]}, b: ${bCards[i]}) are different, result: ${if (comparison > 0) "a" else "b"} wins")
                        return comparison
                    }
                }
                println("All cards are the same, result: tie")
            }
            return comparison
        }
    }

    fun first(path: String): Long {
        return run(path)
    }

    private val noJokerRankingStrategy: (String) -> Rank = { hand ->
        val cardCounts = hand.groupingBy { it }.eachCount()
        val sortedCardCounts = cardCounts.toList().sortedByDescending { it.second }
        when (sortedCardCounts.size) {
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

    fun second(path: String): Long {
        return run(path, true)
    }

    private fun run(path: String, jokers: Boolean = false): Long {
        var rankingStrategy: (String) -> Rank = noJokerRankingStrategy
        if (jokers) {
            rankingStrategy = ::jokerRankingStrategy
        }
        return PriorityQueue(HandComparator(Mapper(jokers))).apply {
            addAll(File(path).readLines().map { line ->
                parseHand(line, rankingStrategy)
            })
        }.also {
            println(" - - - Hands: $it\n")
        }.let {
            IntStream.range(0, it.size).mapToObj { i ->
                (i + 1) * it.poll()
                    .also { hand -> println("- - - Card $i: $hand") }.bid.toLong()
            }
        }.reduce(Long::plus).get()
    }

    private fun jokerRankingStrategy(hand: String): Rank {
        val jokerCount = hand.filter { it == 'J' }.length
        if (jokerCount == 0) {
            return noJokerRankingStrategy(hand) // no joker, return the rank of the hand without the joker
        }

        val sortedCardCounts = hand
            .groupingBy { card -> card }
            .eachCount()
            .toList()
            .sortedByDescending { it.second }

        val dominantCardCount = sortedCardCounts.firstOrNull { it.first != 'J' }?.second ?: 0 // excluding jokers
        val exception = IllegalArgumentException("Invalid joker count: $jokerCount in hand: $hand")
        return when(dominantCardCount) {
            1 -> when (jokerCount) {
                1 -> Rank.ONE_PAIR
                2 -> Rank.THREE_OF_A_KIND
                3 -> Rank.FOUR_OF_A_KIND
                4 -> Rank.FIVE_OF_A_KIND
                else -> throw exception
            }

            2 -> when (jokerCount) {
                1 -> Rank.THREE_OF_A_KIND
                2 -> Rank.FOUR_OF_A_KIND
                3 -> Rank.FIVE_OF_A_KIND
                else -> throw exception
            }

            3 -> when (jokerCount) {
                1 -> Rank.FOUR_OF_A_KIND
                2 -> Rank.FIVE_OF_A_KIND
                else -> throw exception
            }

            4 -> when (jokerCount) {
                1 -> Rank.FIVE_OF_A_KIND
                else -> throw exception
            }

            else -> noJokerRankingStrategy(hand)
        }.also { println("Joker rank: $it") }
    }

}