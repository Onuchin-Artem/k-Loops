package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("techno") {
        sequencer(_16th) {
            "k . co . cl . co co".play(track("drums"), velocity = 0.5)
            val bass2Length = listOf(2, 5, 7, 1, 3).look() o 64
            val velocity = listOf(0.2, 0.0, 0.1, 0.1).look()
            "a1 . . g1".play(track("bass"), velocity = velocity, length = bass2Length)
            val cutoff = triag(from = 0.01, to = 0.25, period = 12, t1 = 8)
            val mod = sine(from = 0.6, to = 0.7, period = 4, jitter = 0.1)
            val delay = trapezoid(from = 0.0, to = 1.0, period = 16 * 32, t1 = 7 * 16, t2 = 20 * 16)
            val midGain = rect(from = 0.5, to = 0.60, period = 8, phase = 0.5)
            master().device("master").parameter("mid gain").setValue(midGain)
            track("bass").parameter("mod").setValue(mod)
            track("bass").parameter("cutoff").setValue(cutoff)
            track("bass").sends("Delay").setValue(delay)
        }
    }

    loop("techno2") {
        sequencer(_16th) {
            setLoopVelocity(trapezoid(period = 16 * 16, t1 = 4 * 16))
            "k . . sn k . .".play(track("drums"), velocity = 0.7)
            ". . rd ch . .".play(track("drums"), velocity = 0.1, length = _32nd)
            "clave clave . . clave .".play(track("drums"), velocity = 0.5)
        }
    }

    loop("techno3") {
        sequencer(_16th) {
            setLoopVelocity(trapezoid(period = 16 * 16, t1 = 4 * 16, phase = 0.5))
            "mid . rm mid . .".play(track("drums"), velocity = 0.5)
            ". . sn sn . .".play(track("drums"), velocity = 0.5)
        }
    }

    runBackgroundTasks.forEach { it.join() }
}