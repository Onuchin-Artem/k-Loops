package kLoops.music

import kLoops.internal.MusicPhraseRunners
import kLoops.internal.Track
import kLoops.internal.checkRatio

class MidiTrackWrapper(context: LoopContext, track: Track) : TrackWrapper(context, track) {

    private fun playCommandTemplate(note: Int, length: NoteLength, velocity: Double): String? {
        val loopVelocity = MusicPhraseRunners.getMusicPhrase(context).loopVelocity
        if (loopVelocity < 0.00000001) return null
        checkRatio("velocity", velocity)
        val midiVelocity = (velocity * loopVelocity).toMidiRange()
        val lengthMillis = length.toMillis()
        return "${track.id} add {time} note $note $midiVelocity $lengthMillis"
    }

    fun playAsync(note: Any, length: NoteLength, velocity: Double = 1.0) {
        if (note.toString() == ".") return
        val commandTemplate = playCommandTemplate(note.toNoteOrDrum(), length, velocity) ?: return
        MusicPhraseRunners.getMusicPhrase(context).addCommand(commandTemplate)
    }

    private fun Any.toNoteOrDrum(): Int = when (this) {
        is String -> {
            if (noteRegex.matches(this)) toNote(this)
            else toDrum(this)
        }
        is Char -> this.toString().toNoteOrDrum()
        is Int -> this
        else -> throw IllegalArgumentException("not a note $this")
    }

    fun toDrum(drum: String): Int {
        val drumSynonym = when (drum) {
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
            else -> drum
        }
        return track.lookupDrumNote(drumSynonym).toNote()
    }
}
