package kLoops.music

fun MidiTrack.play(note: Any, length: NoteLength, velocity: Double): MidiTrack {
    playAsync(note, length, velocity)
    context.silence(length)
    return this
}

fun MidiTrack.playChord(chord: List<Any>, length: NoteLength, velocity: Double): MidiTrack {
    chord.forEach { note -> playAsync(note, length, velocity) }
    context.silence(length)
    return this
}