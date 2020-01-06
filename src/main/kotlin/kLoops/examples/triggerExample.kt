package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()
    val listOfEvent = mutableListOf("a", "b", "c", "c", "c")
    runWhenEvent("kick", triggerEvents = listOf("a")) {
        track("drums").play("kick", _8th, 0.5)
        if (1 in 2) triggerEvent("d")
        if (1 in 10) listOfEvent.shuffle()
        triggerEvent(listOfEvent.tick())
    }

    runWhenEvent("bass", triggerEvents = listOf("a")) {
        silence(_8th)
        track("bass").play("a0", _8th, listOf(0.5, 1.0).tick() )
    }

    runWhenEvent("snare", triggerEvents = listOf("b")) {
        track("drums").play("snare", _8th, 0.5)
        triggerEvent("a")
    }

    runWhenEvent("hat", triggerEvents = listOf("c")) {
        track("drums").play("hihat", _8th, 0.5)
        if (1 in 2) triggerEvent("e")
        triggerEvent("b")
    }

    runWhenEvent("hat_random", triggerEvents = listOf("e")) {
        track("drums").play("hihat", _16th.t(), 0.4)
         if (1 in 3) triggerEvent("e")
    }

    runWhenEvent("keys", triggerEvents = listOf("d")) {
        listOf("a4", "d3", "c5").forEach { note -> track("keys").play(note, _4th, 0.5)}
    }

    triggerEventNextPulse("a")
    runBackgroundTasks.forEach { it.join() }
}