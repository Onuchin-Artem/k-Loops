package kLoops.internal

import com.yundom.kache.Builder
import com.yundom.kache.Kache
import com.yundom.kache.config.FIFO
import com.yundom.kache.config.LRU

fun checkRatio(name: String, number: Double) {
    check(number in 0.0..1.0) { "$name should be from 0 to 1 but was $number" }
}

fun String.asResource(work: (String) -> Unit) {
    val content = Class::class.java.getResource(this).readText()
    work(content)
}

private val nonLetter = "[^a-z0-9]+".toRegex()
private fun String.normalize() = this.toLowerCase().replace(nonLetter, " ").trim()

private data class NonUniquePair<T>(val name: String, val duplicateNo : Int, val value: T) {
    fun key() = name.normalize() + " $duplicateNo"
}

class Search<T>(collectionOfPairs: List<Pair<String, T>>) {
    private val listOfPairs : List<NonUniquePair<T>>
    private val cache: Kache<String, T> = Builder.build {
        policy = LRU
        capacity = 100
    }

    init {
        listOfPairs = collectionOfPairs
                .sortedBy { pair -> pair.first }
                .fold(mutableListOf<NonUniquePair<T>>()) { list, pair ->
                    if (list.isEmpty()) {
                        list.add(NonUniquePair(pair.first, 1, pair.second))
                    } else if (list.last().name.normalize() == pair.first.normalize()) {
                        list.add(NonUniquePair(pair.first, list.last().duplicateNo + 1, pair.second))
                    } else {
                        list.add(NonUniquePair(pair.first, 1, pair.second))
                    }
                    list
                }.toList()
    }

    @Synchronized fun findOrElse(substring : String, block: () -> T): T {
        if (cache.exist(substring)) return cache.get(substring)!!
        val allMatched = listOfPairs.filter { pair ->
            substring.normalize().split(" ").all {  pair.key().contains(it) }
        }
        if (allMatched.isEmpty()) return block.invoke()
        cache.put(substring, allMatched.first().value)
        return allMatched.first().value
    }
}

fun <T> List<Pair<String, T>>.toSearch() = Search(this)