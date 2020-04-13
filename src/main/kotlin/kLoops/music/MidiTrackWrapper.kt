package kLoops.music

import kLoops.internal.MusicPhraseRunners
import kLoops.internal.Track
import kLoops.internal.checkRatio

class MidiTrackWrapper(context: LoopContext, track: Track) : TrackWrapper(context, track) {

    private fun playCommandTemplate(note: Int, length: Rational, velocity: Double): String? {
        val loopVelocity = MusicPhraseRunners.getMusicPhrase(context).loopVelocity
        if (loopVelocity < 0.00000001) return null
        checkRatio("velocity", velocity)
        val midiVelocity = (velocity * loopVelocity).toMidiRange()
        val lengthMillis = length.toMillis()
        return "${track.id} add {time} note $note $midiVelocity $lengthMillis"
    }

    fun playAsync(note: Any, length: Rational, velocity: Double = 1.0) {
        if (note.toString() == ".") return
        val note = note.toNoteOrDrum()
        val commandTemplate = playCommandTemplate(note.note, length * note.lengthRescale, velocity * note.velocity) ?: return
        MusicPhraseRunners.getMusicPhrase(context).addCommand(commandTemplate)
    }

    private fun Any.toNoteOrDrum(): Note = when (this) {
        is String -> {
            when {
                noteRegex.matches(this) -> parseNote(this) { it.toNoteOrDrum() }
                musicalNoteRegex.matches(this) -> toNote(this)
                else -> toDrum(this)
            }
        }
        is Char -> this.toString().toNoteOrDrum()
        is Int -> Note(this)
        is Note -> this
        else -> throw IllegalArgumentException("not a note $this")
    }

    private fun toDrum(drum: String): Note {
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
        return Note(track.lookupDrumNote(drumSynonym))
    }

    fun play(note: Any, length: Rational, velocity: Double = 1.0) {
        playAsync(note, length, velocity)
        context.silence(length * note.toNoteOrDrum().lengthRescale)
    }

    fun playChordAsync(chord: List<Any>, length: Rational, velocity: Double = 1.0) {
        chord.forEach { note -> playAsync(note, length, velocity) }
    }

    fun playChord(chord: List<Any>, length: Rational, velocity: Double = 1.0) {
        playChordAsync(chord, length, velocity)
        context.silence(length * chord[0].toNoteOrDrum().lengthRescale)
    }


}
