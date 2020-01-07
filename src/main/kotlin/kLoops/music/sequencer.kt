package kLoops.music

import kLoops.internal.Parameter


fun LoopContext.sequencer(stepLength: NoteLength, id: String = globalCounter, block: SequenceContext.() -> Unit) {
    val seqContext = SequenceContext(stepLength, this, id)
    seqContext.block()
    silence(stepLength)
    Counters.tick("${loopName}/$id")

}

class SequenceContext(
        val stepLength: NoteLength,
        context: LoopContext,
        val id: String = globalCounter) : LoopContext(context) {

    fun <T : Any> MidiTrackWrapper.playSequence(notes: List<T>, velocity: Double, length: NoteLength = stepLength) =
            playAsync(notes.look(id), length, velocity)

    fun <T : Any> List<T>.play(track: MidiTrackWrapper, velocity: Double, length: NoteLength = stepLength) =
            track.playSequence(this, velocity, length)

    fun String.play(track: MidiTrackWrapper, velocity: Double, length: NoteLength = stepLength) =
            this.toSeq().play(track, velocity, length)
    }