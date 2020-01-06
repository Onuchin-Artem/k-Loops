package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("keys") {
        val notes = listOf("c4", "g4", "e4")
        val synths = listOf("keys 1", "keys 2")
        track(synths.tick())
                .play(notes.look(), _8th, 0.5)
        silence(3 o 4)
        track("keys 1")
                .play(notes.tick(), _8th, 0.5)
    }

    loop("keys2") {
        val notes = listOf("c4", "c3", "e3", "d3")
        track("keys 2")
                .play(notes.tick(), _4th, 0.5)
        silence(_half)
    }

    loop("bass") {
        val notes = listOf("c1", "g1", "e1", "b1")
        val durations = listOf(2 o 5, 1 o 5, 2 o 5)
        track("bass")
                .play(notes.tick(), _8th, 0.5)
        track("bass")
                .play("c0", _16th, 0.5)
        silence(durations.look() - _16th - _8th)
    }

    loop("bass2") {
        track("bass")
                .playAsync("g1", 1 o 32, 0.5)
        silence(_quarter)
    }

    loop("pads") {
        val length = listOf(_zero, _8th).reflect().tick()
        silence(length)
        listOf("c3", "e4", "a5").reversed().forEach {
            track("pad").playAsync(it, 2.bar(), 0.3 + Math.random() * 0.1)
            silence(_4th.triplet())
        }
        silence(_4th - length)
    }

    loop("drums") {
        // play is not super convenient for playing drums
        val isSnare = listOf(1.0, 0.0).tick()
        track("drums").playAsync("kick", _16th, 1.0)
        track("drums").playAsync("snare", _16th, isSnare)
        silence(_8th)
        val hhNote = listOf("hihat closed", "hihat closed", "hihat open").look()
        track("drums").play(hhNote, _16th, 0.9 * Math.random() * 0.2)
        if (1 in 4 && isSnare == 0.0) track("drums").play(38, _16th, 0.5)
        else silence(_16th)
    }

    loop("random_HH") {
        if (1 in 4) track("drums").play("hihat closed", _16th.t(), 0.5)
        silence(randomNoteLength())
    }


    runBackgroundTasks.forEach { it.join() }
}