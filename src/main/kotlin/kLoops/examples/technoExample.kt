package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("techno") {
        val drums1 = "k . . sn k . .".split(' ')
        val drums2 = ". . oh ch .".split(' ')
        val drums3 = "k . co . cl . co co".split(' ')
        val drums4 = "clave clave . . clave .".split(' ')

        val bass = ". e1 . . e1".split(' ')
        val bass2 = "a0 . . a0 a0 . . .".split(' ')

        track("drums")
                .playAsync(drums1.tick(), _16th, 0.7)
        track("drums")
                .playAsync(drums2.look(), _16th, 0.5)
        track("drums")
                .playAsync(drums3.look(), _16th, 0.5)
        track("drums")
                .playAsync(drums4.look(), _16th, 0.3)
        track("bass")
                .playAsync(bass.look(), listOf(1, 2, 3, 6).tick("bass length") o 64, 0.4)
        track("bass")
                .playAsync(bass2.look(), listOf(2, 3, 4).random() o 64, 0.5)

        silence(_16th)
    }

    runBackgroundTasks.forEach { it.join() }
}