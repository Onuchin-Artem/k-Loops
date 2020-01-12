package kLoops.music

import kLoops.internal.Live
import kotlin.math.round

infix fun Int.o(b: Int): NoteLength {
    return NoteLength(this, b)
}

fun Int.bar() = this o 1

fun Int.beat() = NoteLength(this, 4)


fun String.toNoteLength(): NoteLength {
    val split = this.split('/')
    return split[0].toInt() o split[1].toInt()
}

private fun gcd(a: Int, b: Int): Int {
    if (b == 0) return a
    return gcd(b, a % b)
}

fun randomNoteLength() = (1 + (Math.random() * 1023).toInt()) o 1024

fun Double.toNoteLength() = (round(1024 * this).toInt() o 1024).simplify()

data class NoteLength(val nominator: Int, val denominator: Int): Comparable<NoteLength> {
    init {
        check(nominator >= 0) { "nominator must be non-negative but was $nominator" }
        check(denominator > 0) { "denominator must be positive but was $denominator" }
    }

    fun t() = triplet()

    fun triplet() = NoteLength(nominator * 2, denominator * 3).simplify()

    fun dot() = NoteLength(nominator * 3, denominator * 2).simplify()

    fun simplify(): NoteLength {
        val gcd = gcd(nominator, denominator)
        return NoteLength(nominator / gcd, denominator / gcd)
    }

    fun toBeats() = (nominator.toDouble() / denominator.toDouble()) * 4.0

    fun toMillis() = round((toBeats() / Live.state().bpm.toDouble()) * 60 * 1000)

    operator fun unaryPlus() = dot()

    operator fun plus(other: NoteLength) =
            NoteLength(nominator * other.denominator + other.nominator * denominator,
                    denominator * other.denominator).simplify()

    operator fun minus(other: NoteLength) =
            NoteLength(nominator * other.denominator - other.nominator * denominator,
                    denominator * other.denominator).simplify()

    override operator fun compareTo(other: NoteLength) =
            (nominator * other.denominator).compareTo(other.nominator * denominator)

    fun beatInBar() =
            (nominator % denominator) o denominator
}

val _8th = 1 o 8
val _16th = 1 o 16
val _32nd = 1 o 32
val _quarter = 1 o 4
val _q = 1 o 4
val _4th = 1 o 4
val _3rd = 1 o 3
val _half = 1 o 2
val _h = 1 o 2
val _whole = 1 o 1
val _w = 1 o 1
val _zero = 0 o 1