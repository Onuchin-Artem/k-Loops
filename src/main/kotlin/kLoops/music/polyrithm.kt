package kLoops.music

fun sq(vararg notes: String) = notes.joinToString(" ")
fun rnd(vararg notes: String) = notes.toList().random()
fun rnd(vararg notesWithChances: Pair<Int, String>) =
        notesWithChances.flatMap { pair -> (1..pair.first).map{ pair.second } }.random()
operator fun String.times(repeats: Int) = (1..repeats).joinToString(" ") { this }

fun fil(vararg notes: String): String {
    val split = sq(*notes).split(" +".toRegex())
    return split.joinToString(" ") { it + ":" + (1 o split.size).toString() + ":1.0" }
}