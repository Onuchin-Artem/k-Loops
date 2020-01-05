package kLoops.examples
import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("keys") {
        val notes = listOf("c4", "a4", "e4")
        track("keys")
                .play(notes.tick(), _8th, 0.5)
        track("keys")
                .play(notes.tick(), _8th.dot(), 0.5)
    }
    Thread.sleep(20000)
    loop("keys") {
        val notes = listOf("a2", "b2", "d2")
        track("keys")
                .play(notes.tick(), _8th, 0.5)
        track("keys")
                .play(notes.tick(), _4th, 0.5)
    }

    runBackgroundTasks.forEach { it.join() }
}