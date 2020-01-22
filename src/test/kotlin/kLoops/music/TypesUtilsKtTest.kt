package kLoops.music

import org.junit.Assert.assertTrue
import org.junit.Test

class TypesUtilsKtTest {

    @Test
    fun contains() {
        val size = (1..10000).filter { 4 in 10 }.size
        assertTrue("strange size $size", size in 3500..4500)
    }
}