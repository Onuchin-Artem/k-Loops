package kLoops.internal

fun checkRatio(name: String, number: Double) {
    check(number in 0.0..1.0) { "$name should be from 0 to 1 but was $number" }
}

fun String.asResource(work: (String) -> Unit) {
    val content = Class::class.java.getResource(this).readText()
    work(content)
}
