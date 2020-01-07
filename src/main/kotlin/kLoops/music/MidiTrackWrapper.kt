package kLoops.music

import kLoops.internal.MusicPhraseRunners
import kLoops.internal.Track
import kLoops.internal.checkRatio

class MidiTrackWrapper(context: LoopContext, track: Track) : TrackWrapper(context, track) {

    private fun playCommandTemplate(note: Int, length: NoteLength, velocity: Double): String? {
        val loopVelocity = MusicPhraseRunners.getMusicPhrase(context).loopVelocity
        if (loopVelocity < 0.00000001) return null
        checkRatio("velocity", velocity)
        val midiVelocity = velocity.toMidiRange() * loopVelocity
        val lengthMillis = length.toMillis()
        return "${track.id} add {time} note $note $midiVelocity $lengthMillis"
    }

    fun playAsync(note: Any, length: NoteLength, velocity: Double) {
        if (note.toString() == ".") return
        val commandTemplate = playCommandTemplate(note.toNote().value, length, velocity) ?: return
        MusicPhraseRunners.getMusicPhrase(context).addCommand(_zero, commandTemplate)
    }

    data class Note(val value: Int)

    private val noteRegex = "([abcdefg]#?)(-[12]|[0-8])".toRegex(RegexOption.IGNORE_CASE)

    private fun String.toNote(): Note {
        if (noteRegex.matches(this)) {
            val groups = noteRegex.matchEntire(this)!!.groupValues
            val note = groups[1]
            val octave = groups[2].toInt()
            val noteInt = when (note) {
                "c" -> 0
                "c#" -> 1
                "d" -> 2
                "d#" -> 3
                "e" -> 4
                "f" -> 5
                "f#" -> 6
                "g" -> 7
                "g#" -> 8
                "a" -> 9
                "a#" -> 10
                "b" -> 11
                else -> throw IllegalArgumentException("Non-existent note: $note")
            }
            return Note(24 + octave * 12 + noteInt)
        } else {
            val drumSynonym = when (this.toLowerCase()) {
                "k" -> "kick"
                "bd" -> "kick"
                "sn" -> "snare"
                "oh" -> "open hihat"
                "ch" -> "closed hihat"
                "cl" -> "clap"
                "ht" -> "hi tom"
                "lt" -> "low tom"
                "co" -> "conga"
                "rd" -> "ride"
                "rm" -> "rim"
                "cy" -> "cymbal"
                "wd" -> "wood"
                else -> this
            }
            return track.lookupDrumNote(drumSynonym).toNote()
        }
    }

    private fun Any.toNote(): Note = when (this) {
        is String -> toNote()
        is Int -> Note(this)
        else -> throw IllegalArgumentException("not a note $this")
    }
}