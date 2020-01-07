package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("techno") {
        sequencer(_16th) {
            val d = track("drums")
            val b = track("bass")
            "k . . sn k . .".toSeq()
                    .play(d, 0.7)
            ". . oh ch .".toSeq()
                    .play(d, 0.5)
            "k . co . cl . co co".toSeq()
                    .play(d, 0.5)
            "clave clave . . clave .".toSeq()
                    .play(d, 0.3)
            val bass1Length = listOf(1, 2, 3, 6).look() o 64
            val bass2Length = listOf(2, 3, 4).look() o 64
            ". e1 . . e1".toSeq()
                    .play(b, 0.4, bass1Length)
            "a0 . . a0 a0 . . .".toSeq()
                    .play(b, 0.4, bass2Length)
        }
    }
    runBackgroundTasks.forEach { it.join() }
}