package org.aoc

import java.io.File
import kotlin.math.max

enum class Color(val limit: Int) {
    Red(12),
    Green(13),
    Blue(14)
}

data class Game(val id: Int, val set: Set<Pair<Color, Int>>)

object CubeConundrum {
    fun first(path: String): Int {
        return File(path).readLines().stream()
            .map { line -> game(line) }
            .filter { game ->
                game.set
                    .stream()
                    .map { it.second <= it.first.limit }
                    .reduce(Boolean::and)
                    .get()
            }.map { game -> game.id }
            .reduce(Int::plus)
            .get()
    }

    fun second(path: String): Int {
        return File(path).readLines().stream()
            .map { line -> game(line) }
            .map { game ->
                println(game)
                buildMap<Color, Int> {
                    for (pair in game.set) {
                        compute(pair.first) { _, v ->
                            v?.let { max(it, pair.second) } ?: pair.second
                        }
                    }
                }.also { println(it) }.values.reduce(Int::times).also { println(it) }
            }
            .reduce(Int::plus)
            .get()
    }

    private fun game(line: String): Game {
        return line.split(':').run {
            Game(extractDigits(this[0])!!, this[1].split(';', ',').let { cubes ->
                buildSet {
                    for (cube in cubes) add(
                        Color.entries.first { cube.contains(it.name, true) }
                                to extractDigits(cube)!!
                    )
                }
            })
        }
    }
}

fun extractDigits(string: String): Int? {
    return StringBuilder().apply {
        for (char in string) if (char.isDigit()) append(char)
    }.let { if (it.isEmpty()) null else it.toString() }?.toInt()
}