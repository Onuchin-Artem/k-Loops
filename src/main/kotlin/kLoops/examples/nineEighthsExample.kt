package kLoops.examples

import kLoops.music.*

fun main() {
    setPulsePeriod(9 o 8)
    val runBackgroundTasks = startBackgroundTasks()
    loop("drums") {
        val drums = "k . k k . k k k ch".split(' ')
        track("drums")
                .play(drums.tick(), _8th, 0.5)
    }
    Thread.sleep(5000)

    loop("keys") {
        val chord = listOf(listOf("e3", "g4", "b4"), listOf("a3", "c4", "e4")).tick()
        track("keys 2").playChordAsync(chord, _16th, 0.3)
        silence(8 o 8)
        track("keys 1").playChord(chord, _16th, 0.2)
    }

    runWhenEvent("bass", triggerEvents = listOf("loop_pulse")) {
        val notes = listOf("e0 . e0 b0 . b0 g0 g0", "a0 . a0 e0 . e0 c0 c0").tick("chord").toSeq()
        notes.forEach { note ->
            track("bass").playAsync(note, _16th, listOf(0.5, 0.2).tick())
            silence(_8th)
        }
    }

    runWhenEvent("pad", triggerEvents = listOf("loop_pulse")) {
        track("pad").playAsync(listOf("e5", "a5").tick(), _half, 0.05)
        silence(_4th)
        track("drums").play("snare", _8th, 0.6)
        track("drums").play("ride", _16th, 0.3)
        track("drums").play("closed hihat", _16th, 0.3)
    }

    runBackgroundTasks.forEach { it.join() }
}