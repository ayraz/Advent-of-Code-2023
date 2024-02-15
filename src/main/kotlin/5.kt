package org.aoc

import java.io.File
import kotlin.jvm.internal.Ref

object IfYouGiveASeedAFertilizer {

    fun first(path: String): Long {
        val ptr = Ref.IntRef().apply { element = 0 } // line ptr reference
        val lines = File(path).readLines()
        val seeds = lines[ptr.element++].split(" ").mapNotNull { extractDigits<Long>(it) }
        val pipeline = buildList {
            do {
                add(parseMap(lines, ptr) ?: break)
            } while (true)
        }.onEach { println(it) } // parse maps as a chain
        println("Mapping seeds...")
        return seeds.stream().map { seed ->
            println("Seed: $seed")
            pipeline.fold(seed) { key: Long, map: Map<LongRange, (Long) -> Long> ->
                ((map.keys.firstOrNull() { it.contains(key) }
                    ?.let { range -> map[range] }
                    ?.let { it(key) }) ?: key).also { println("Mapped $key to $it") }
            }
        }.min(Long::compareTo).get()
    }

    fun parseMap(lines: List<String>, ptr: Ref.IntRef): Map<LongRange, (Long) -> Long>? {
        return buildMap {
            while (ptr.element < lines.size && lines[ptr.element].isEmpty()) ptr.element++ // skip empty lines
            ptr.element++ // skip the line with map name
            while (ptr.element < lines.size && lines[ptr.element].isNotEmpty()) {
                val (destinationRangeStart,
                    sourceRangeStart,
                    rangeLength) = lines[ptr.element].split(" ").mapNotNull { extractDigits<Long>(it) }.toTypedArray()
                this[sourceRangeStart.rangeUntil(sourceRangeStart + rangeLength)] = {
                    source: Long -> source + (destinationRangeStart - sourceRangeStart)
                }
                ptr.element++
            }
        }.ifEmpty { null }
    }
}