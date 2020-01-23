package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("drums") {
        val sq = nxt(
                sq(nxt("k" * 3, "k" * 2), nxt("sn", "ch" * 2)),
                nxt(sq("k" * 3, "co" * 5), "sn")
                )
        sq.each { note -> track("drums").play(note, _16th, 0.5) }
    }
    loop("drums2") {
        val sq = sq(nxt("kick_4", "clap"), rnd(2 to "conga_low", 1 to "conga_mid"))
        sq.each { note -> track("drums").play(note, _8th, 0.1) }
    }

    loop("keys") {
        euclideanRythm(3, 8, _8th) {
            track("keys 2").playChordAsync(chord("a4", "7sus2"), _16th, 0.1)
        }
    }

    loop("keys 2") {
        euclideanRythm(7, 16, 1 o 12) {
            track("keys 1").playChordAsync(chord("a5", "7sus2"), _16th, 0.7)
        }
    }

    loop("bass") {
        euclideanRythm(3, 13, 1 o 13) {
            track("bass").playAsync(chord("a1", "sus4").tick(), _16th)
        }
    }

    runBackgroundTasks.forEach { it.join() }

}
