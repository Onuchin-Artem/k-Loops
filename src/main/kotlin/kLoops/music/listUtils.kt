package kLoops.music

import java.util.concurrent.ConcurrentHashMap

object Counters {
    private val values = ConcurrentHashMap<String, Int>()
    fun tick(id: String): Int =
            values.compute(id) { key, counter ->
                when (counter) {
                    null -> 0; else -> counter + 1
                }
            }!!

    fun look(id: String): Int = values.getOrDefault(id, 0)
}

fun <T> List<T>.mirror() = this + this.asReversed()
fun <T> List<T>.reflect() = this + this.dropLast(1).asReversed()
