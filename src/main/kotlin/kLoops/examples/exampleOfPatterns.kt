package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

   loop("drums") {
       setLoopVelocity(0.3)
        val sq = nxt(
                sq(nxt("k" * 3, "k" * 2), nxt("sn", "ch" * 2)),
                nxt(sq("k" * 3, "co" * 5), "sn")
        )
       sq.each { note -> track("drums").play(note, _16th, 0.5); println(note) }
    }


    loop("drums2") {
        val sq = sq(nxt(
                fil(nxt("kick_4", "kick_4" * 3, "kick_4" * 4)),
                "clap"),
                nxt(3 to "conga_low", 1 to "conga_mid", 2 to "conga_low", 2 to "conga_mid"))
        println(sq)

        sq.each { note -> track("drums").play(note, _8th, 0.1) }
    }
    loop("keys") {
        euclideanRythm(3, 8, _8th) {
            track("keys 2").playChordAsync(chord("a4", "minor7"), _16th, 0.1)
        }
    }
    loop("keys 2") {
        euclideanRythm(13, 32, 1 o 16) {
            track("keys 1").playChordAsync(chord("a5", "m9"), _16th, 0.4)
        }
    }
    loop("bass") {
        euclideanRythm(3, 13, 1 o 13) {
            track("bass").playAsync(chord("a1", "minor").tick(), _16th)
        }
    }

    runBackgroundTasks.forEach { it.join() }

}
