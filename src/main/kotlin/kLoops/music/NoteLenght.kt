package kLoops.music

import kLoops.internal.Live
import kotlin.math.round

infix fun Int.o(b: Int): Rational {
    return Rational(this, b)
}

fun Int.bar() = this o 1

fun Int.beat() = Rational(this, 4)


fun String.toRational(): Rational {
    val split = this.split('/')
    return split[0].toInt() o split[1].toInt()
}

private fun gcd(a: Int, b: Int): Int {
    if (b == 0) return a
    return gcd(b, a % b)
}

fun randomRational() = (1 + (Math.random() * 1023).toInt()) o 1024

fun Double.toRational() = (round(1024 * this).toInt() o 1024).simplify()

class Rational(private val nominator: Int, private val denominator: Int) : Comparable<Rational> {
    init {
        check(nominator >= 0) { "nominator must be non-negative but was $nominator" }
        check(denominator > 0) { "denominator must be positive but was $denominator" }
    }

    fun t() = triplet()

    fun triplet() = Rational(nominator * 2, denominator * 3).simplify()

    fun dot() = Rational(nominator * 3, denominator * 2).simplify()

    fun simplify(): Rational {
        val gcd = gcd(nominator, denominator)
        return Rational(nominator / gcd, denominator / gcd)
    }

    fun toBeats() = (nominator.toDouble() / denominator.toDouble()) * 4.0

    fun toMillis() = round((toBeats() / Live.state().bpm.toDouble()) * 60 * 1000)

    operator fun unaryPlus() = dot()

    operator fun plus(other: Rational) =
            Rational(nominator * other.denominator + other.nominator * denominator,
                    denominator * other.denominator).simplify()

    operator fun minus(other: Rational) =
            Rational(nominator * other.denominator - other.nominator * denominator,
                    denominator * other.denominator).simplify()

    override operator fun compareTo(other: Rational) =
            (nominator * other.denominator).compareTo(other.nominator * denominator)

    operator fun times(other: Rational) =
            (nominator * other.nominator) o (denominator * other.denominator)

    fun beatInBar() =
            (nominator % denominator) o denominator

    override fun toString() = "$nominator/$denominator"
    override fun equals(other: Any?) : Boolean =
            if (other is Rational) compareTo(other) == 0
            else false

    override fun hashCode() = listOf(nominator, denominator).hashCode()
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