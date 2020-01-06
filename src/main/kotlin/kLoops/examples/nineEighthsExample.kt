package kLoops.examples

import kLoops.music.*

fun main() {
    setPulsePeriod(9 o 8)
    val runBackgroundTasks = startBackgroundTasks()
    loop("drums") {
        val drums = "k k sn k k sn k sn co".split(' ')
        track("drums")
                .play(drums.tick(), _8th, 0.5)
    }
    Thread.sleep(5000)

    loop("keys") {
        silence(8 o 8)
        track("keys 2")
                .playChord(listOf("a3", "c4", "e4"), _8th, 0.5)
    }

    runWhenEvent("bass", triggerEvents = listOf("loop_pulse")) {
        val notes = "e0 e0 a0 e0 e0 a0 e0 a0".split(' ')
        notes.forEach { note ->
            track("bass").play(note, _16th, 0.5)
            silence(_16th)
        }
    }

    runWhenEvent("pad", triggerEvents = listOf("loop_pulse")) {
        track("pad").play(listOf("a2", "e2").tick(), _half, 0.5)
    }

    runBackgroundTasks.forEach { it.join() }
}