package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("main loop") {
        "c2 a2 c2 a2 e2 f2 c3 c3".toSeq().forEach { n ->
            if (n == "c3") triggerEvent("c3")
            else triggerEvent("c2")
            sequencer(_8th) {
                "k . sn . k k sn .".play(track("drums"), 1.0)
                "c3 g3 e3".play(track("keys 1"), 0.5)
                "$n . $n .".play(track("bass"), 1.0)
            }
        }
    }

    loop("hi hat") {
        silence(_8th)
        track("drums").play("ch", _4th, 0.5)
        triggerEvent("hat")
    }

    runWhenEvent("additional drums", listOf("hat")) {
        sequencer(_16th) {
            "co rm".play(track("drums"), 1.0)
        }
    }

    runWhenEvent("pads 1", listOf("c2")) {
        sequencer(_4th) {
            "e4  . . f4 ".play(track("pad"), velocity = 0.3, length = _q.dot())
        }
    }

    runWhenEvent("pads 2", listOf("c3")) {
        track("pad").playAsync("c4", _half, 0.1)
    }

    runBackgroundTasks.forEach { it.join() }
}