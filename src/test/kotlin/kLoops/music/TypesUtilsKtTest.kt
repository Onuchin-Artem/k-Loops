package kLoops.music

import org.junit.Test

import org.junit.Assert.*

class TypesUtilsKtTest {

    @Test
    fun contains() {
        val size = (1..10000).filter { 4 in 10 }.size
        assertTrue("strange size $size",size in 3500..4500)
    }
}