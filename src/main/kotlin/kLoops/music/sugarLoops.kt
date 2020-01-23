package kLoops.music

fun MidiTrackWrapper.play(note: Any, length: NoteLength, velocity: Double = 1.0) {
    playAsync(note, length, velocity)
    context.silence(length)
}

fun MidiTrackWrapper.playChordAsync(chord: List<Any>, length: NoteLength, velocity: Double = 1.0) {
    chord.forEach { note -> playAsync(note, length, velocity) }
}

fun MidiTrackWrapper.playChord(chord: List<Any>, length: NoteLength, velocity: Double = 1.0) {
    playChordAsync(chord, length, velocity)
    context.silence(length)
}





