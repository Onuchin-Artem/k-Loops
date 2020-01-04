package kLoops.music

import kotlin.math.round

fun Double.toMidiRange() = round(this * 127)

operator fun Int.contains(chances: Int): Boolean =
        Math.random() < (chances.toDouble() / this.toDouble())


