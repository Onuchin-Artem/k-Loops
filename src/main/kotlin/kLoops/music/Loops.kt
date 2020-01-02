package kLoops.music

import kLoops.internal.Live
import kLoops.internal.LoopRunners
import kLoops.internal.checkRatio

class LoopContext(val loopName: String) {
    fun track(name: String): MidiTrack {
        return MidiTrack(this, Live.state().lookupTrackId(name))
    }

    fun returnTrack(name: String): Track {
        return Track(this, Live.state().lookupTrackId(name))
    }

    fun master(): Track {
        return Track(this, Live.state().lookupTrackId("master"))
    }

    fun track(number: Int): MidiTrack {
        return MidiTrack(this, Live.state().lookupTrackId(number))
    }

    fun returnTrack(number: Int): Track {
        return Track(this, Live.state().lookupTrackId(number))
    }

    fun <T> List<T>.tick(tickId: String): T = this[Counters.tick("$loopName/$tickId") % this.size]
    fun <T> List<T>.look(tickId: String): T = this[Counters.look("$loopName/$tickId") % this.size]

    fun <T> List<T>.look(): T = look("global_tick")
    fun <T> List<T>.tick(): T = tick("global_tick")

    fun silence(length: kLoops.music.NoteLength) {
        LoopRunners.getLoop(this).addWait(length)
    }
}


open class Track(val context: LoopContext, val id: Int) {
}

class MidiTrack(context: LoopContext, id: Int) : Track(context, id) {

    fun playCommandTemplate(note: Int, length: kLoops.music.NoteLength, velocity: Double): String {
        checkRatio("velocity", velocity)
        val midiVelocity = velocity.toMidiRange()
        val lengthMillis = length.toMillis()
        return "$id add {time} note $note $midiVelocity $lengthMillis"
    }

    fun play(note: Int, length: kLoops.music.NoteLength, velocity: Double): MidiTrack {
        LoopRunners.getLoop(context)
                .addCommand(length, playCommandTemplate(note, length, velocity))
        return this
    }

    fun play(note: String, length: kLoops.music.NoteLength, velocity: Double): MidiTrack {
        return play(note.toNote(), length, velocity)
    }

    fun playAsync(note: Int, length: kLoops.music.NoteLength, velocity: Double): MidiTrack {
        LoopRunners.getLoop(context)
                .addCommand(kLoops.music._zero, playCommandTemplate(note, length, velocity))
        return this
    }

    fun playAsync(note: String, length: kLoops.music.NoteLength, velocity: Double): MidiTrack {
        return playAsync(note.toNote(), length, velocity)
    }
}
