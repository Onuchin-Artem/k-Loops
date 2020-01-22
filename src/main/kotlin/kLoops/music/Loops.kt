package kLoops.music

import kLoops.internal.*
import kotlin.math.round

open class LoopContext(val loopName: String, val events: List<String>) {
    constructor(context: LoopContext) : this(context.loopName, context.events)

    var trigger: String = ""
    var parameter: Any = Any()

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

    open inner class Generator(compute: (stepInPeriod: Int) -> Double = { 0.0 }) {

        protected open val compute: (stepInPeriod: Int) -> Double = compute

        fun tick(tickId: String = globalCounter): Double {
            return compute(Counters.tick("$loopName/$tickId"))
        }

        fun look(tickId: String = globalCounter): Double {
            return compute(Counters.look("$loopName/$tickId"))
        }
    }

    inner class LFO(
            private val from: Double, private val to: Double,
            private val period: Int, private val phase: Double,
            private val jitter: Double,
            private val computeLfo: (stepInPeriod: Int) -> Double) : Generator() {
        override val compute: (stepInPeriod: Int) -> Double = this::doCompute

        private fun doCompute(step: Int): Double {
            val zeroToOne = computeLfo((step + round(phase * period).toInt()) % period)
            return (from + (to - from) * zeroToOne).addJitter(jitter)
        }
    }

    fun setLoopVelocity(velocity: Double) {
        checkRatio("velocity", velocity)
        MusicPhraseRunners.getMusicPhrase(this).addChangeLoopVelocity(velocity)
    }

    fun setLoopVelocity(loop: String, velocity: Double) {
        checkRatio("velocity", velocity)
        MusicPhraseRunners.getMusicPhrase(this).addChangeLoopVelocity(loop, velocity)
    }

    fun setLoopVelocity(loop: String, gen: Generator) = setLoopVelocity(loop, gen.look())

    fun setLoopVelocity(gen: Generator) = setLoopVelocity(gen.look())

    fun silence(length: NoteLength) =
            MusicPhraseRunners.getMusicPhrase(this).addWait(length)

    fun triggerEvent(event: String, parameter: Any = Any()) =
            MusicPhraseRunners.getMusicPhrase(this).addEvent(event, parameter)

    fun broadcastParameter(parameter: String, value: Any) =
            MusicPhraseRunners.getMusicPhrase(this).addBroadcastParameter(parameter, value)

    fun <T> receiveParameter(parameterValue: String, block: LoopContext.(parameter: T) -> Unit) {
        val parameter = MusicPhraseRunners.readParameter(parameterValue) as T?
        if (parameter != null) {
            this.block(parameter)
        }
    }

    fun <T> receiveTriggerParameter(block: LoopContext.(parameter: T) -> Unit) {
        this.block(parameter as T)
    }

    fun Parameter.setValue(value: Double) {
        checkRatio("value", value)
        val command = "add {time} set ${this.id} $value"
        MusicPhraseRunners.getMusicPhrase(this@LoopContext).addCommand(command)
    }

    fun Parameter.setValue(generator: Generator) = setValue(generator.look())
}


open class TrackWrapper(val context: LoopContext, val track: Track) {

    fun pan() = track.parameter("panning")
    fun volume() = track.parameter("volume")
    fun parameter(parameter: String): Parameter = track.mainDevice?.parameter(parameter)!!
    fun sends(returnTrack: String) = track.parameter(returnTrack)
    fun device(device: String) = track.device(device)
}