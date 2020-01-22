package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()
    loop("drums") {
        track("3").playAsync(10, _32nd, 0.4)
        silence(1 o 3)
    }

    loop("drums2") {
        track("2").playAsync(30, _8th, 1.0)
        silence(1 o 4)
    }

    loop("drums3") {
        silence(3 o 8)
        if (1 in 2) track("3").playAsync(0, _32nd, 0.4)
        silence(1 o 8)
        track("3").playAsync(30, _8th, 1.0)
    }

    loop("progression") {
        broadcastParameter("root", listOf("a0", "d1").tick().toNote())
        silence(4 o 1)
    }

    loop("keys1") {
        receiveParameter<Note>("root") {
            track("5").playAsync(chord(it + 2 * octave, "m9").mirror().tick(), _32nd, 0.4)
            silence(listOf(1, 1, 2, 2, 3).look() o 9)
        }
    }

    loop("keys1.2") {
        receiveParameter<Note>("root") {
            silence(3 o 4)
            track("5").playAsync(chord(it + 3 * octave, "minor7").tick(), _8th, 0.15)
            silence(1 o 4)
        }
    }

    loop("keys1.3") {
        receiveParameter<Note>("root") {
            silence(3 o 32)
            track("5").playAsync(chord(it + 4 * octave, "minor7").reversed().tick(), _32nd, 0.1)
            silence(1 o 32)
        }
    }


    loop("keys2") {
        receiveParameter<Note>("root") {
            silence(1 o 4)
            track("4").playAsync(chord(it, "minor7").tick(), 1 o 64, 0.9)
            silence(1 o 4)
        }
    }

    loop("keys2.2") {
        receiveParameter<Note>("root") {

            val velocity = listOf(3, 3, 8, 4, 4, 10, 9, 0).tick() / 10.0
            track("4").playAsync(it + octave, 1 o 64, velocity)
            silence(1 o 16)
        }
    }


    loop("drone") {
        receiveParameter<Note>("root") {
            silence(1 o 8)
            var chord = chord(it  + octave, "m9")
            if (1 in 2) chord = chord.invert(1)
            if (1 in 2) chord = chord.invert(2)

            track("6").playChordAsync(chord, _half.dot(), 0.8)
            if (1 in 4) silence(1 o 16)
            val sleep = listOf(1, 2, 4).random()
            silence(sleep o 32)
        }
    }





    loop("drone2") {
        val lfo = sine("a2".toNote().toDouble(), "d3".toNote().toDouble(), 8)
        track("1").parameter("pitch").setValue(lfo.tick() / 127.0)
        silence(1 o 32)
    }

    loop("drone2.1") {
        val volume = "1 0 0 1 0 0 1 1".toSeq().tick().toDouble() * sine(0.85, 0.95, period = 8).look()
        track("1").parameter("volume").setValue(volume)
        silence(1 o 8)
    }

    loop("drone2.2") {
        track("1").pan().setValue(Math.random())
        silence(1 o 1)
    }


    runWhenEvent("mix", listOf("mix")) {
        master().volume().setValue(0.6)
        silence(1 o 16)
        listOf("drums", "drums3", "keys1", "keys1.2", "keys1.3", "keys2", "keys2.2").forEach {
            setLoopVelocity(it, 0.0)
        }
        track("6").volume().setValue(0.25)
        repeat(8) {
            setLoopVelocity("keys2", line(0.0, 1.0, time = 8).tick("1"))
            track("4").sends("delay").setValue(line(0.0, 0.7, time = 8).look("1"))
            silence(1 o 1)
        }
        setLoopVelocity("drums3", 1.0)
        repeat(8) {
            setLoopVelocity("drums", line(0.0, 1.0, time = 8).tick("2"))
            silence(1 o 1)
        }
        repeat(8) {
            setLoopVelocity("keys1", line(0.0, 1.0, time = 8).tick("3"))
            silence(1 o 1)
        }
        setLoopVelocity("keys1.2", 1.0)
        silence(8 o 1)
        repeat(12) {
            setLoopVelocity("keys1.3", line(0.0, 1.0, time = 8).tick("4"))
            track("6").volume().setValue(line(0.25, 0.1, time = 8).look("4"))

            silence(1 o 1)
        }
        silence(8 o 1)
        track("4").sends("delay").setValue(0.0)
        repeat(12) {
            setLoopVelocity("keys2.2", line(0.0, 1.0, time = 8).tick("5"))
            silence(1 o 1)
            track("6").volume().setValue(line(0.1, 0.0, time = 8).look("5"))

        }
        silence(20 o 1)
        repeat(18) {
            master().volume().setValue(line(0.58, 0.0, time = 16).tick("6"))
            silence(1 o 1)
        }

    }
    triggerEventNextPulse("mix")
    runBackgroundTasks.forEach { it.join() }
}