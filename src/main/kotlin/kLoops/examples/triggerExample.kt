package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    runWhenEvent("kick", triggerEvents = listOf("a")) {
        track("drums").play(36, _8th, 0.5)
        if (1 in 2) triggerEvent("d")

        if (3 in 5) triggerEvent("b")
        else triggerEvent("c")
    }

    runWhenEvent("bass", triggerEvents = listOf("a")) {
        silence(_8th)
        track("bass").play("a0", _8th, listOf(0.5, 1.0).tick() )
    }

    runWhenEvent("snare", triggerEvents = listOf("b")) {
        track("drums").play(38, _8th, 0.5)
        triggerEvent("a")
    }

    runWhenEvent("hat", triggerEvents = listOf("c")) {
        track("drums").play(44, _8th, 0.5)
        triggerEvent("b")
    }

    runWhenEvent("keys", triggerEvents = listOf("d")) {
        listOf("a4", "d3", "c5").forEach { note -> track("keys1").play(note, _4th, 0.5)}
    }

    triggerEventNextPulse("a")
    runBackgroundTasks.forEach { it.join() }
}