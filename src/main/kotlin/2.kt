package org.example

import java.io.File

enum class Color(val limit: Int) {
    Red(12),
    Green(13),
    Blue(14)
}

data class Game(val id: Int, val set: Set<Pair<Color, Int>>)

fun first(path: String): Int {
    return File(path).readLines().stream()
        .map { line -> line.split(':').run {
            Game(extractDigits(this[0]), this[1].split(';', ',').let { cubes ->
                buildSet { for (cube in cubes) add(
                    Color.entries.first { cube.contains(it.name, true) }
                            to extractDigits(cube)
                ) }
            }) }
        }.filter { game -> game.set
            .stream()
            .map { it.second <= it.first.limit }
            .reduce(Boolean::and)
            .get()
        }.map { game -> game.id }
        .reduce(Int::plus)
        .get()
}

fun extractDigits(string: String): Int {
    return StringBuilder().apply {
        for (char in string) if (char.isDigit()) append(char)
    }.toString().toInt()
}