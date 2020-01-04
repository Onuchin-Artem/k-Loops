package kLoops.music

import kLoops.internal.Live
import kLoops.internal.MusicPhraseRunners
import kLoops.internal.checkRatio

class LoopContext(val loopName: String, val events: List<String>) {
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

    fun silence(length: NoteLength) {
        MusicPhraseRunners.getMusicPhrase(this).addWait(length)
    }

    fun triggerEvent(event: String) {
        MusicPhraseRunners.getMusicPhrase(this).addEvent(event)
    }
}


open class Track(val context: LoopContext, val id: Int) {
}

class MidiTrack(context: LoopContext, id: Int) : Track(context, id) {

    private fun playCommandTemplate(note: Int, length: NoteLength, velocity: Double): String {
        checkRatio("velocity", velocity)
        val midiVelocity = velocity.toMidiRange()
        val lengthMillis = length.toMillis()
        return "$id add {time} note $note $midiVelocity $lengthMillis"
    }

    fun playAsync(note: Any, length: NoteLength, velocity: Double): MidiTrack {
        MusicPhraseRunners.getMusicPhrase(context)
                .addCommand(_zero, playCommandTemplate(note.toNote().value, length, velocity))
        return this
    }
}