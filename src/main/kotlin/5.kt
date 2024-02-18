package org.aoc

import java.io.File
import java.util.stream.Stream
import kotlin.jvm.internal.Ref


object IfYouGiveASeedAFertilizer {

    fun first(path: String): Long {
        val (seeds, pipeline) = buildPipeline(path)
        println("Mapping seeds...")
        return seeds.stream().map { seed ->
            fold(seed, pipeline)
        }.min(Long::compareTo).get()
    }

    /**
     * buildPipeline - O(n)
     * buildList - O(s) where s is the number of seed ranges
     * let m be a constant number of maps in the pipeline,
     * let s2 be the number of seed ranges after splitting the ranges with splitMap,
     * splitMap will produce s*3 ranges in the worst case, and thus it can be considered O(s),
     * then the time complexity of the second function is O(s),
     * neglecting n in favor of higher order term and m being a constant independent of the input size.
     */
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
            }.also { println("Seed ranges: $it, count ${it.count()}") }
        }.stream().flatMap { range ->
            var stream = Stream.of(range)
            for (map in pipeline) {
                stream = splitMap(stream, map)
            }
            stream
        }.map { it.first }.min(Long::compareTo).get()
    }

    /**
     * Fold the seed through the pipeline
     */
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

    private fun splitMap(stream: Stream<LongRange>, map: Map<LongRange, (Long) -> Long>): Stream<LongRange> {
        return stream.flatMap { seeds ->
            println("\nSeeds: $seeds")
            val full = map.keys.firstOrNull { seeds.isWithin(it) } // try to find a range that contains the seeds
            if (full != null) {
                val mapper = map[full.also { println("Using $it") }]!! // get the map function,
                Stream.of(seeds.shift(mapper).also { println("Full map from $seeds to $it") })
            } else {
                // try to find an intersection
                var partial: LongRange? = null
                var key: LongRange? = null
                for (mapping in map.keys) {
                    partial = mapping.intersect(seeds)
                    if (partial != null) {
                        key = mapping
                        break
                    }
                }
                if (partial != null) {
                    partial.also { println("Using $it") }
                    val out = Stream.builder<LongRange>()
                    if (seeds.first < partial.first) { // each partial range has a hole which is not mapped
                        out.accept((seeds.first..<partial.first).also { println("Hole $it")})
                    }
                    if (seeds.last > partial.last) {
                        out.accept((partial.last + 1..seeds.last).also { println("Hole $it")})
                    }
                    // get the map function and apply it to the intersection
                    out.accept(partial.shift(map[key]!!).also { println("Partial map from $seeds to $it") })
                    out.build()
                } else {
                    Stream.of(seeds.also { println("No map $it") }) // if no range is found, return the seeds
                }
            }
        }
    }

    private fun LongRange.isWithin(other: LongRange): Boolean {
        return this.first >= other.first && this.last < other.last
    }

    private fun LongRange.shift(operation: (Long) -> Long): LongRange {
        return operation(start)..operation(last)
    }

    /**
     * A function that returns the intersection of two LongRanges, or null if they do not overlap
     */
    private fun LongRange.intersect(other: LongRange): LongRange? {
        // Find the maximum of the lower bounds and the minimum of the upper bounds
        val lower = maxOf(first, other.first)
        val upper = minOf(last, other.last)
        // If the lower bound is less than or equal to the upper bound, return a new LongRange
        return if (lower <= upper) {
            LongRange(lower, upper)
        } else {
            // Otherwise, return null
            null
        }
    }

    private fun buildPipeline(path: String): Pair<List<Long>, List<Map<LongRange, (Long) -> Long>>> {
        println("Building pipeline...")
        val ptr = Ref.IntRef().apply { element = 0 } // line ptr reference
        val lines = File(path).readLines()
        val seeds = lines[ptr.element++].split(" ").mapNotNull { extractDigits<Long>(it) }
        val pipeline = buildList {
            do {
                add(parseMap(lines, ptr)?.also { println("Added mapping $it\n") } ?: break)
            } while (true)
        } // parse maps as a chain
        return Pair(seeds, pipeline)
    }

    private fun parseMap(lines: List<String>, ptr: Ref.IntRef): Map<LongRange, (Long) -> Long>? {
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