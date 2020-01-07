package kLoops.music

import kLoops.internal.Live
import kLoops.internal.MusicPhraseRunners
import kLoops.internal.Track
import kLoops.internal.checkRatio
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

    open inner class Generator(compute: (stepInPeriod: Int) -> Double = {0.0}) {

        protected open val compute: (stepInPeriod: Int) -> Double = compute

        fun tick(tickId: String = globalCounter): Double {
            return compute(Counters.tick("$loopName/$tickId"))
        }

        fun look(tickId: String = globalCounter): Double {
            return compute(Counters.look("$loopName/$tickId"))
        }
    }

    inner class LFO(
            private val from: Double, private val to:Double,
            private val period: Int, private val phase: Double,
            private val jitter: Double,
            private val computeLfo: (stepInPeriod: Int) -> Double) : Generator() {
         override val compute: (stepInPeriod: Int) -> Double = this::doCompute

        private fun doCompute(step: Int): Double {
            val zeroToOne = computeLfo((step + round(phase * period).toInt()) % period)
            return (from + (to - from) * zeroToOne).addJitter(jitter)
        }
    }

    fun setLoopVolume(velocity: Double) {
        checkRatio("velocity", velocity)
        MusicPhraseRunners.getMusicPhrase(this).addChangeLoopVelocity(velocity)
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