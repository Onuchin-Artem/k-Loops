package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("techno") {
        sequencer(_16th) {
            val d = track("drums")
            val b = track("bass")
            "k . co . cl . co co".play(d, velocity = 0.5)
            val bass1Length = listOf(1, 2, 3, 6).look() o 64
            val bass2Length = listOf(2, 3, 4).look() o 64
            ". . . . g1" .play(b, velocity = 0.2, length = bass1Length)
            "a1 . . a1 a1 . . .".play(b, velocity = 0.2, length = bass2Length)
        }
    }

    loop("techno2") {
        sequencer(_16th) {
            setLoopVolume(trapezoid(period = 16 * 16, t1 = 4 * 16).look())
            val d = track("drums")
            "k . . sn k . .".play(d, velocity = 0.7)
            ". . oh ch . ".play(d, velocity = 0.3)
            "clave clave . . clave .".play(d, velocity = 0.5)
        }
    }

    loop("techno3") {
        sequencer(_16th) {
            setLoopVolume(trapezoid(period = 16 * 16, t1 = 4 * 16, phase = 0.5).look())
            val d = track("drums")
            "mid . rm mid . .".play(d, velocity = 0.5)
            ". . sn sn . .".play(d, velocity = 0.5)
        }
    }

    runBackgroundTasks.forEach { it.join() }
}