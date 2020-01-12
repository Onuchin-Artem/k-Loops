package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("drums") {
       val pattern = if (1 in 3) "k ch sn sn" else "k ch sn ch"
        if (1 in 8) triggerEvent("boom")
        pattern.toSeq().forEach { drum ->
            triggerEvent(drum, listOf(3 o 5, 1 o 32, 3 o 16).tick())
            track("1 yellow").play(drum, _8th, 1.0)
        }
    }

    loop("drums2") {
        track("traperator").play("boom", _4th, 0.5)
    }
    runWhenEvent("boom", listOf("boom")) {
        ". boom boom boom . boom".toSeq().forEach {note ->
            track("traperator").play(note, 1 o 6, 0.4)

        }
    }

    runWhenEvent("echo", "k ch sn".toSeq()) {
        val sleep = parameter
        if (sleep is NoteLength) {
            val drum = when (trigger) {
                "k" -> listOf( "kick").tick("k")
                "ch" -> listOf("ride", "ch").tick("ch")
                "sn" -> if (1 in 4) "roto" else "sn"
                else -> "."
            }
            silence(sleep)
            track("1 yellow").playAsync(drum, _8th, 0.7)
        }
        if (3 in 5) triggerEvent(trigger, sleep)
    }


    loop("progression") {
        val chord = listOf(chord("a3", "minor"), chord("e3", "minor"))
        silence(2 o 1)
    }

    runBackgroundTasks.forEach { it.join() }
}