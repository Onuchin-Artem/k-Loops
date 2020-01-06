package kLoops.music

fun MidiTrackWrapper.play(note: Any, length: NoteLength, velocity: Double) {
    playAsync(note, length, velocity)
    context.silence(length)
}

fun MidiTrackWrapper.playChord(chord: List<Any>, length: NoteLength, velocity: Double) {
    chord.forEach { note -> playAsync(note, length, velocity) }
    context.silence(length)
}