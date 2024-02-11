package org.aoc

import java.io.File

object GearRatios {

    private val symbols = setOf(
        '*', '/', '=', '$', '%', '#', '@',
        "!", '^', '&', '(', ')', '-', '+'
    )

    // TODO: Big-O analysis / optimizations
    fun first(path: String): Int {
        return buildList {
            File(path).readLines().let { lines ->
                for (i in lines.indices) {
                    lines[i].run {
                        var ptr = 0
                        var start: Int // inclusive
                        var end: Int // exclusive
                        while (ptr < length) {
                            if (this[ptr].isDigit()) {
                                val digit = StringBuilder().append(this[ptr])
                                start = ptr++
                                while (ptr < length && this[ptr].isDigit()) {
                                    digit.append(this[ptr])
                                    ptr++
                                }
                                end = ptr

                                // collect adjacent chars
                                if (buildList {
                                        // above
                                        lines.getOrNull(i - 1)?.run {
                                            for (j in start - 1..end) {
                                                if (checkSymbol(j)) {
                                                    add(this[j])
                                                }
                                            }
                                        }
                                        // center
                                        if (checkSymbol(start - 1)) {
                                            add(this@run[start - 1])
                                        }
                                        if (checkSymbol(end)) {
                                            add(this@run[end])
                                        }
                                        // below
                                        lines.getOrNull(i + 1)?.run {
                                            for (j in start - 1..end) {
                                                if (checkSymbol(j)) {
                                                    add(this[j])
                                                }
                                            }
                                        }
                                    }.any { symbols.contains(it) }) {
                                    add(digit.toString().toInt())
                                }
                            } else {
                                ptr++
                            }
                        }
                    }
                }
            }
        }.reduce(Int::plus)
    }

    private fun String.checkSymbol(index: Int) = getOrNull(index)?.let { symbols.contains(it) } == true

    fun second(path: String): Int {
        return buildList {
            File(path).readLines().also { lines ->
                for (i in lines.indices) {
                    lines[i].let { line ->
                        for (j in line.indices) {
                            if (line[j] == '*') {
                                println("Found '*' at ${j + 1}, ${i + 1}")
                                val gears = mutableListOf<Int>()
                                val start = j - 1;
                                val end = j + 1
                                // above
                                lines.getOrNull(i - 1)?.let {
                                    gears.addAll(extractGears(j, it))
                                }
                                // center
                                if (line[start].isDigit()) {
                                    gears.addAll(extractGears(start, line))
                                }
                                if (line[end].isDigit()) {
                                    gears.addAll(extractGears(end, line))
                                }
                                // below
                                lines.getOrNull(i + 1)?.let {
                                    gears.addAll(extractGears(j, it))
                                }
                                if (gears.size != 2) continue
                                add(gears.reduce(Int::times).also { println("Add gear ratio: $it") })
                            }
                        }
                    }
                }
            }
        }.reduce(Int::plus)
    }

    private fun extractGears(k: Int, line: String): List<Int> {
        var l = k - 1;
        var r = k + 1
        while (line.getOrNull(l)?.isDigit() == true) {
            l--
        }
        while (line.getOrNull(r)?.isDigit() == true) {
            r++
        }
        return line.substring(l + 1, r)
            .split('.')
            .mapNotNull { extractDigits(it) }
            .also { if (it.isNotEmpty()) println("Found gears: $it") }
    }
}