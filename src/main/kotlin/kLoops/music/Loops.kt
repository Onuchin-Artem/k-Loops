package kLoops.music

import kLoops.internal.Live
import kLoops.internal.MusicPhraseRunners
import kLoops.internal.Track
import kotlin.math.round

open class LoopContext(val loopName: String, val events: List<String>) {
    constructor(context: LoopContext) : this(context.loopName, context.events)

    fun track(name: String): MidiTrackWrapper {
        return MidiTrackWrapper(this, Live.state().lookupTrackId(name))
    }

    fun returnTrack(name: String): TrackWrapper {
        return TrackWrapper(this, Live.state().lookupTrackId(name))
    }

    fun master(): TrackWrapper {
        return TrackWrapper(this, Live.state().lookupTrackId("master"))
    }

    fun <T> List<T>.tick(tickId: String = globalCounter): T = this[Counters.tick("$loopName/$tickId") % this.size]
    fun <T> List<T>.look(tickId: String = globalCounter): T = this[Counters.look("$loopName/$tickId") % this.size]
    fun <T> iterate(list: List<T>, tickId: String = globalCounter) = list.look(tickId)

    open inner class Generator(compute: (stepInPeriod: Int) -> Double = {0.0}) {

        open val compute: (stepInPeriod: Int) -> Double = compute

        fun tick(tickId: String = globalCounter): Double {
            return compute(Counters.tick("$loopName/$tickId"))
        }

        fun look(tickId: String = globalCounter): Double {
            return compute(Counters.look("$loopName/$tickId"))
        }
    }

    inner class LFO(
            val from: Double, val to:Double,
            val period: Int, val phase: Double,
            val jitter: Double,
            val computeLfo: (stepInPeriod: Int) -> Double) : Generator() {
        override val compute: (stepInPeriod: Int) -> Double = this::doCompute

        fun doCompute(step: Int): Double {
            val zeroToOne = computeLfo((step + round(phase * period).toInt()) % period)
            return (from + (to - from) * zeroToOne).addJitter(jitter)
        }
    }


    fun silence(length: NoteLength) {
        MusicPhraseRunners.getMusicPhrase(this).addWait(length)
    }

    fun triggerEvent(event: String) {
        MusicPhraseRunners.getMusicPhrase(this).addEvent(event)
    }
}


open class TrackWrapper(val context: LoopContext, val track: Track) {
}