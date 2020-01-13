package kLoops.music

val chords = mapOf(
        "5" to listOf(0, 7),
        "+5" to listOf(0, 4, 8),
        "m+5" to listOf(0, 3, 8),
        "sus2" to listOf(0, 2, 7),
        "sus4" to listOf(0, 5, 7),
        "6" to listOf(0, 4, 7, 9),
        "m6" to listOf(0, 3, 7, 9),
        "7sus2" to listOf(0, 2, 7, 10),
        "7sus4" to listOf(0, 5, 7, 10),
        "7-5" to listOf(0, 4, 6, 10),
        "halfdiminished" to listOf(0, 3, 6, 10),
        "7+5" to listOf(0, 4, 8, 10),
        "m7+5" to listOf(0, 3, 8, 10),
        "9" to listOf(0, 4, 7, 10, 14),
        "m9" to listOf(0, 3, 7, 10, 14),
        "m7+9" to listOf(0, 3, 7, 10, 14),
        "maj9" to listOf(0, 4, 7, 11, 14),
        "9sus4" to listOf(0, 5, 7, 10, 14),
        "6*9" to listOf(0, 4, 7, 9, 14),
        "m6*9" to listOf(0, 3, 9, 7, 14),
        "7-9" to listOf(0, 4, 7, 10, 13),
        "m7-9" to listOf(0, 3, 7, 10, 13),
        "7-10" to listOf(0, 4, 7, 10, 15),
        "7-11" to listOf(0, 4, 7, 10, 16),
        "7-13" to listOf(0, 4, 7, 10, 20),
        "9+5" to listOf(0, 10, 13),
        "m9+5" to listOf(0, 10, 14),
        "7+5-9" to listOf(0, 4, 8, 10, 13),
        "m7+5-9" to listOf(0, 3, 8, 10, 13),
        "11" to listOf(0, 4, 7, 10, 14, 17),
        "m11" to listOf(0, 3, 7, 10, 14, 17),
        "maj11" to listOf(0, 4, 7, 11, 14, 17),
        "11+" to listOf(0, 4, 7, 10, 14, 18),
        "m11+" to listOf(0, 3, 7, 10, 14, 18),
        "13" to listOf(0, 4, 7, 10, 14, 17, 21),
        "m13" to listOf(0, 3, 7, 10, 14, 17, 21),
        "add2" to listOf(0, 2, 4, 7),
        "add4" to listOf(0, 4, 5, 7),
        "add9" to listOf(0, 4, 7, 14),
        "add11" to listOf(0, 4, 7, 17),
        "add13" to listOf(0, 4, 7, 21),
        "madd2" to listOf(0, 2, 3, 7),
        "madd4" to listOf(0, 3, 5, 7),
        "madd9" to listOf(0, 3, 7, 14),
        "madd11" to listOf(0, 3, 7, 17),
        "madd13" to listOf(0, 3, 7, 21),
        "major" to listOf(0, 4, 7),
        "minor" to listOf(0, 3, 7),
        "major7" to listOf(0, 4, 7, 11),
        "dom7" to listOf(0, 4, 7, 10),
        "minor7" to listOf(0, 3, 7, 10),
        "augmented" to listOf(0, 4, 8),
        "aug" to listOf(0, 4, 8),
        "diminished" to listOf(0, 3, 6),
        "dim" to listOf(0, 3, 6),
        "diminished7" to listOf(0, 3, 6, 9),
        "dim7" to listOf(0, 3, 6, 9),
        "halfdim" to listOf(0, 3, 6, 10),
        "m7-5" to listOf(0, 3, 6, 10)
)
val noteRegex = "([abcdefg]#?)(-[12]|[0-8])".toRegex(RegexOption.IGNORE_CASE)

fun Any.toNote(): Int =
    if (this is String && noteRegex.matches(this)) toNote(this.toString())
    else if (this is Int) this
    else throw IllegalArgumentException("not a note $this")



fun toNote(midiNote: String): Int {
        val groups = noteRegex.matchEntire(midiNote)!!.groupValues
        val note = groups[1]
        val octave = groups[2].toInt()
        val noteInt = when (note) {
            "c" -> 0
            "c#" -> 1
            "d" -> 2
            "d#" -> 3
            "e" -> 4
            "f" -> 5
            "f#" -> 6
            "g" -> 7
            "g#" -> 8
            "a" -> 9
            "a#" -> 10
            "b" -> 11
            else -> throw IllegalArgumentException("Non-existent note: $note")
        }
        return 24 + octave * 12 + noteInt
}

operator fun List<Int>.plus(value: Int) = this.map { it + value }
operator fun Int.plus(value: List<Int>) = value + this
fun List<Int>.invert(invert: Int) = this.mapIndexed { i, note -> if (i < invert) note + 12 else note }.sortedBy { it }
fun List<Int>.spread(octaves: Int = 1) = this.mapIndexed { i, note -> note + 12 * i  * octaves }
fun List<Int>.repeat(octaves: Int) = (0 until octaves).flatMap { this + 12 * it }


fun chord(note: Any, chord: String) = (chords[chord] ?: error("Chord $chord is not defined!")) + note.toNote()

typealias Chord = List<Int>
typealias Note = Int

val octave = 12