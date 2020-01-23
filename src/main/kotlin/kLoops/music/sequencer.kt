package kLoops.music

fun LoopContext.sequencer(stepLength: NoteLength, id: String = globalCounter, block: SequenceContext.() -> Unit) {
    val seqContext = SequenceContext(stepLength, this, id)
    var i = 0
    do {
        seqContext.block()
        silence(stepLength)
        Counters.tick("${loopName}/$id")
        i++
    } while (i < seqContext.largestLoopSize)
}

class SequenceContext(
        val stepLength: NoteLength,
        context: LoopContext,
        val id: String = globalCounter) : LoopContext(context) {
    internal var largestLoopSize = 0

    fun <T : Any> MidiTrackWrapper.playSequence(notes: List<T>, velocity: Double, length: NoteLength = stepLength) {
        largestLoopSize = maxOf(largestLoopSize, notes.size)
        playAsync(notes.look(id), length, velocity)
    }

    fun <T : Any> List<T>.play(track: MidiTrackWrapper, velocity: Double, length: NoteLength = stepLength) =
            track.playSequence(this, velocity, length)

    fun String.play(track: MidiTrackWrapper, velocity: Double, length: NoteLength = stepLength) =
            this.toSeq().play(track, velocity, length)
}

fun euclideanRythm(onsets: Int, pulses: Int) : List<Boolean> {
    val spread = pulses.toDouble() / onsets.toDouble()
    val resultArray = MutableList(pulses) { false }
    (0 until onsets)
            .map { (it * spread).toInt() }
            .forEach { resultArray[it] = true }
    return resultArray.toList()
}

fun euclideanRythm(onsets: Int, pulses: Int, onsetNote: String) =
        euclideanRythm(onsets, pulses).map { if (it) onsetNote else "." }.joinToString(" ")

