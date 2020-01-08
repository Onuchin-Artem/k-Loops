package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("techno") {
        sequencer(_16th) {
            val d = track("drums")
            val b = track("bass")
            "k . co . cl . co co".play(d, velocity = 0.5)
            val bass2Length = listOf(2, 5, 7, 1, 3).look() o 64
            val velocity = listOf(0.2, 0.0, 0.1, 0.1).look()
            "a1 . . g1".play(b, velocity = velocity, length = bass2Length)

            val cutoff = triag(from = 0.01, to = 0.25, period = 12, t1 = 8)
            val mod = sine(from = 0.6, to = 0.7, period = 4, jitter = 0.1)
            val delay = trapezoid(from = 0.0, to = 1.0, period = 16 * 32, t1 = 7 * 16, t2 = 20 * 16)
            val midGain = rect(from = 0.5, to = 0.60, period = 8, phase = 0.5)
            master().device("master").parameter("mid gain").setValue(midGain)
            b.parameter("mod").setValue(mod)
            b.parameter("cutoff").setValue(cutoff)
            b.sends("Delay").setValue(delay)
        }
    }

    loop("techno2") {
        sequencer(_16th) {
            setLoopVelocity(trapezoid(period = 16 * 16, t1 = 4 * 16))
            val d = track("drums")
            "k . . sn k . .".play(d, velocity = 0.7)
            ". . rd ch . .".play(d, velocity = 0.1, length = _32nd)
            "clave clave . . clave .".play(d, velocity = 0.5)
        }
    }

    loop("techno3") {
        sequencer(_16th) {
            setLoopVelocity(trapezoid(period = 16 * 16, t1 = 4 * 16, phase = 0.5))
            val d = track("drums")
            "mid . rm mid . .".play(d, velocity = 0.5)
            ". . sn sn . .".play(d, velocity = 0.5)
        }
    }

    runBackgroundTasks.forEach { it.join() }
}