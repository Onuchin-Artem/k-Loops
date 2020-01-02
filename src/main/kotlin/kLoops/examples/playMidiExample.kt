package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("keys") {
        val notes = listOf("c4", "g4", "e4")
        val synths = listOf("keys1", "keys2")
        track(synths.tick())
                .play(notes.look(), _8th, 0.5)
        silence(3 o 4)
        track("keys1")
                .play(notes.tick(), _8th, 0.5)
    }

    loop("keys2") {
        val notes = listOf("c4", "c3", "e3", "d3")
        track("keys2")
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

    loop("brass") {
        val length = listOf(_zero, _8th).reflect().tick()
        silence(length)
        listOf("c3", "e4", "a5").reversed().forEach {
            track(2).playAsync(it, 2.bar(), 0.3 + Math.random() * 0.1)
            silence(_4th.triplet())
        }
        silence(_4th - length)
    }

    loop("drums_low") {
        val isSnare = listOf(1.0, 0.0).tick()
        track(1).playAsync(60, _16th, 1.0)
        track(1).playAsync(62, _16th, isSnare)
        silence(_8th)
        val hhNote = listOf(64, 65, 65).look()
        track(1).play(hhNote, _16th.dot(), 0.9 * Math.random() * 0.2)

        if (1 in 4 && isSnare == 0.0) {
            track(1).play(62, _8th - _16th.dot(), 0.5)
        } else {
            silence(_8th - _16th.dot())
        }
    }

    loop("random_kick") {
        if (1 in 5) {
            track(1).play(60, _16th.t(), 0.5)
        }
        silence(randomNoteLength())
    }


    runBackgroundTasks.forEach { it.join() }
}