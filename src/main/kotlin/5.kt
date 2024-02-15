package org.aoc

import java.io.File
import java.util.concurrent.ForkJoinPool
import kotlin.jvm.internal.Ref
import kotlin.math.min
import kotlin.streams.asStream


object IfYouGiveASeedAFertilizer {

    fun first(path: String): Long {
        val (seeds, pipeline) = buildPipeline(path)
        println("Mapping seeds...")
        return seeds.stream().map { seed ->
            fold(seed, pipeline)
        }.min(Long::compareTo).get()
    }

    fun second(path: String): Long {
        val (seeds, pipeline) = buildPipeline(path)
        println("Mapping seeds...")
        return seeds.let {
            buildList {
                for (i in it.indices step 2) {
                    val start = it[i]
                    val len = it[i + 1]
                    add(start..<start + len)
                }
            }.also { println(it) }
        }.map { range -> range.asSequence() }.let {
            var min = Long.MAX_VALUE
            val pool = ForkJoinPool(14)
            for (i in it) {
                min = pool.submit<Long> {
                    min(i.asStream().parallel().map { seed -> fold(seed, pipeline) }
                        .min(Long::compareTo).get(), min)
                }.get()
            }
            min
        }
    }

    private fun fold(
        seed: Long,
        pipeline: List<Map<LongRange, (Long) -> Long>>
    ): Long {
        println("Seed: $seed")
        return pipeline.fold(seed) { key: Long, map: Map<LongRange, (Long) -> Long> ->
            ((map.keys.firstOrNull() { it.contains(key) }
                ?.let { range -> map[range] }
                ?.let { it(key) }) ?: key).also { println("Mapped $key to $it") }
        }
    }

    private fun buildPipeline(path: String): Pair<List<Long>, List<Map<LongRange, (Long) -> Long>>> {
        val ptr = Ref.IntRef().apply { element = 0 } // line ptr reference
        val lines = File(path).readLines()
        val seeds = lines[ptr.element++].split(" ").mapNotNull { extractDigits<Long>(it) }
        val pipeline = buildList {
            do {
                add(parseMap(lines, ptr) ?: break)
            } while (true)
        }.onEach { println(it) } // parse maps as a chain
        return Pair(seeds, pipeline)
    }

    fun parseMap(lines: List<String>, ptr: Ref.IntRef): Map<LongRange, (Long) -> Long>? {
        return buildMap {
            while (ptr.element < lines.size && lines[ptr.element].isEmpty()) ptr.element++ // skip empty lines
            ptr.element++ // skip the line with map name
            while (ptr.element < lines.size && lines[ptr.element].isNotEmpty()) {
                val (destinationRangeStart,
                    sourceRangeStart,
                    rangeLength) = lines[ptr.element].split(" ").mapNotNull { extractDigits<Long>(it) }.toTypedArray()
                this[sourceRangeStart.rangeUntil(sourceRangeStart + rangeLength)] = { source: Long ->
                    source + (destinationRangeStart - sourceRangeStart)
                }
                ptr.element++
            }
        }.ifEmpty { null }
    }
}