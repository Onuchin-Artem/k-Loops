package kLoops.music

import kLoops.internal.Live
import kLoops.internal.MusicPhraseRunners
import kLoops.internal.Track
import kLoops.internal.checkRatio

class LoopContext(val loopName: String, val events: List<String>) {
    fun track(name: String): MidiTrackWrapper {
        return MidiTrackWrapper(this, Live.state().lookupTrackId(name))
    }

    fun returnTrack(name: String): TrackWrapper {
        return TrackWrapper(this, Live.state().lookupTrackId(name))
    }

    fun master(): TrackWrapper {
        return TrackWrapper(this, Live.state().lookupTrackId("master"))
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


open class TrackWrapper(val context: LoopContext, val track: Track) {
}