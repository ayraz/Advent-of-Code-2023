package org.aoc

import java.io.File

object WaitForIt {

    fun first(path: String): Int {
        val (times, distances) = File(path).readLines().let {
            buildList {
                it[0].split(" ").filter { it.isNotBlank() }.forEach { s ->
                    val num = extractDigits<Int>(s)
                    if (num != null)
                        add(num)
                }
            } to buildList {
                it[1].split(" ").filter { it.isNotBlank() }.forEach { s ->
                    val num = extractDigits<Int>(s)
                    if (num != null)
                        add(num)
                }
            }
        }
        println("Parsed records: \n$times, \n$distances")
        return times.zip(distances).stream().map { (time, distance) ->
            println("\nCounting solutions for time $time and distance $distance...")
            countSolutions(time.toLong(), distance.toLong()).also { println("Solutions: $it") }
        }.reduce(Long::times).get().toInt()
    }

    // TODO - time complexity can obviously be improved
    fun second(path: String): Long {
        val (times, distances) = File(path).readLines().let {
            buildList {
                add(buildString {
                    it[0].split(" ").filter { it.isNotBlank() }.forEach { s ->
                        val num = extractDigits<String>(s)
                        if (num != null)
                            append(num)
                    }
                }.toLong())
            } to buildList {
                add(buildString {
                    it[1].split(" ").filter { it.isNotBlank() }.forEach { s ->
                        val num = extractDigits<String>(s)
                        if (num != null)
                            append(num)
                    }
                }.toLong())
            }
        }
        println("Parsed records: \n$times, \n$distances")
        return times.zip(distances).stream().map { (time, distance) ->
            println("\nCounting solutions for time $time and distance $distance...")
            countSolutions(time, distance).also { println("Solutions: $it") }
        }.reduce(Long::times).get()
    }

    private fun countSolutions(time: Long, distance: Long): Long {
        var speed = 0
        var count = 0L
        var time = time // redefine time as a mutable variable
        while (time > 0) {
            if (time * speed > distance) {
                count++
            }
            speed++
            time--
        }
        return count
    }
}