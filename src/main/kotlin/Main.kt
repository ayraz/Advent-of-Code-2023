package org.aoc

import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() {
    measureTime {
        println(Scratchcards.second("src/main/resources/4.txt"))
    }.also { println("Running time: ${it.toString(DurationUnit.SECONDS, 2)}") }
}