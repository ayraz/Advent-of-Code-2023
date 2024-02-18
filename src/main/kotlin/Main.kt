package org.aoc

import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() {
    measureTime {
        println("\nResult: ${IfYouGiveASeedAFertilizer.second("src/main/resources/5.txt")}")
    }.also { println("Running time: ${it.toString(DurationUnit.SECONDS, 2)}") }
}