package org.aoc

import java.io.File

object GearRatios {

    private val symbols = setOf('*', '/', '=', '$', '%', '#', '@',
        "!", '^', '&', '(', ')', '-', '+')

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
}