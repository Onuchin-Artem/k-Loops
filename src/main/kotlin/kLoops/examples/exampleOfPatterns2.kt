package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()


    loop("main drum") {
        val sq = sq(nxt("k", fil("k" * 3), fil("k" * 2)), nxt("sn", rnd(3 to "ch", 1 to "clave")) )
        val velocity = listOf(0.2, 0.1, 0.3)
        sq.each { note -> track("drums").play(note, _8th, velocity.tick("vel")); println(note) }
    }


    loop("secondary drum") {
        val velocity = listOf(0.5, 0.3, 0.7)
        euclideanRythm(5, 16, 1 o 8) {
            val sq =  listOf(fil("co" * 3), fil("co-mid" * 2), "co").tick("c")

            sq.each { note -> track("drums").play(note, 1 o 8, velocity.tick("vel")); println(note) }
        }
    }
    loop("keys") {
        val chords = listOf(chord("a3", "m9"), chord("a3", "m9"), chord("a3", "m9"),chord("g3", "m9"))
        track("keys").playChord(chords.tick(), 1 o 1, 0.1)
    }

    runBackgroundTasks.forEach { it.join() }

}
