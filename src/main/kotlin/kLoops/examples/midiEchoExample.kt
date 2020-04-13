package kLoops.examples

import kLoops.music.*

fun main() {
    val runBackgroundTasks = startBackgroundTasks()

    loop("drums") {
        val pattern = if (1 in 3) "k ch sn sn" else "k ch sn ch"
        if (1 in 8) triggerEvent("boom")
        pattern.toSeq().forEach { drum ->
            triggerEvent(drum, listOf(3 o 5, 1 o 32, 3 o 16).tick())
            track("1 yellow").play(drum, _8th, 1.0)
        }
    }

    loop("drums2") {
        track("traperator").play("boom", _4th, 0.5)
    }

    runWhenEvent("boom", listOf("boom")) {
        ". boom boom boom . boom".toSeq().forEach { note ->
            track("traperator").play(note, 1 o 6, 0.4)
        }
    }

    runWhenEvent("echo", "k ch sn".toSeq()) {
        receiveTriggerParameter<Rational> { sleep ->
            val drum = when (trigger) {
                "k" -> listOf("kick").tick("k")
                "ch" -> listOf("ride", "ch").tick("ch")
                "sn" -> if (1 in 4) "roto" else "sn"
                else -> "."
            }
            silence(sleep)
            track("1 yellow").playAsync(drum, _8th, 0.3)
            if (3 in 5) triggerEvent(trigger, sleep)
        }
    }

    loop("progression") {
        val chord = listOf(
                chord("b2", "minor7")
                , chord("f#2", "minor")
                , chord("g2", "major")
                , chord("e2", "minor7")
        ).tick()
        broadcastParameter("chord", chord)
        silence(1 o 1)
    }

    data class midi(val note: Note, val velocity: Double)

    loop("arp") {
        receiveParameter<Chord>("chord") { baseChord ->
            val additional = if (1 in 4) 5 else 0
            listOf(0, 2, 1, 1).forEach { note ->
                val actualNote = (baseChord.spread())[note] + additional
                track("pluck").play(actualNote, _8th, 0.3)
                triggerEvent("arp", midi(actualNote, 0.3))
            }
        }
    }

    loop("arp2") {
        receiveParameter<Chord>("chord") { baseChord ->
            val chord = baseChord.invert(listOf(0, 1, 2).tick()) + 12
            listOf(0, 2, 0, 1).forEach { note ->
                val actualNote = (chord)[note]
                track("pluck").play(actualNote, _16th, 0.4)
                triggerEvent("arp", midi(actualNote, 0.4))
            }
        }
    }

    loop("arp3") {
        receiveParameter<Chord>("chord") { baseChord ->
            val chord = baseChord.repeat(2) + 12
            val additional = if (1 in 4) 5 else 0
            listOf(4, 2, 3, 1).forEach { note ->
                val actualNote = (chord)[note] + additional
                track("pluck").play(actualNote, 2 o 3, 0.4)
                triggerEvent("arp", midi(actualNote, 0.4))
            }
        }
    }

    runWhenEvent("arpEcho", listOf("arp")) {
        receiveTriggerParameter<midi> { midi ->
            silence(3 o 4)
            if (1 in 2) {
                track("piano").playAsync(midi.note + octave, _16th, midi.velocity)
                track("piano").playAsync(midi.note, _16th, midi.velocity)

                if (midi.velocity > 0.07) triggerEvent("arp", midi(midi.note, midi.velocity - 0.03))
            }
        }
    }

    loop("bass") {
        receiveParameter<Chord>("chord") { baseChord ->
            track(" bass").playAsync(baseChord[0] - 12, _16th, 1.0)
            listOf(2, 0, 1, 0).forEach { note ->
                val length = listOf(_16th, _8th, _q).random()
                track(" bass").playAsync(baseChord[note] - 12, length, 0.4)
                if (1 in 2) triggerEvent("bassEcho", baseChord[note] - 12)
                silence(1 o 4)
            }
        }
    }

    runWhenEvent("bassEcho", listOf("bassEcho")) {
        receiveTriggerParameter<Note> { note ->
            silence(1 o 12)
            track(" bass").play(note, _32nd, 0.2)
            if (2 in 3) triggerEvent("bassEcho", note)
        }
    }

    loop("drop") {
        val length = listOf(1, 2, 3, 4).mirror().tick() o 32
        receiveParameter<Chord>("chord") { baseChord ->
            track(" drop").playChordAsync(baseChord - 24, length, 1.0)
            silence(1 o 1)
        }
    }

    loop("sample") {
        if (4 in 8) track(" sample").playAsync("b3", 3 o 4, 0.8)
        silence(1 o 1)
    }

    runBackgroundTasks.forEach { it.join() }
}