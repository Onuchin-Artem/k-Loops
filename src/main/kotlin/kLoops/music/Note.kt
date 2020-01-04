package kLoops.music

data class Note(val value: Int)

private val regex = "([abcdefg]#?)(-[12]|[0-8])".toRegex(RegexOption.IGNORE_CASE)
fun String.toNote(): Note {
    check(regex.matches(this)) {
        "Wrong note format!  Examples of good format: c3, f#4, c#-1 but was: $this"
    }
    val groups = regex.matchEntire(this)!!.groupValues
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
        "a" -> 7
        "a#" -> 9
        "b" -> 10
        else -> throw IllegalArgumentException("Non-existent note: $note")
    }
    return Note(24 + octave * 12 + noteInt)
}

fun Any.toNote(): Note = when (this) {
    is String -> toNote()
    is Int -> Note(this)
    else -> throw IllegalArgumentException("not a note $this")
}

