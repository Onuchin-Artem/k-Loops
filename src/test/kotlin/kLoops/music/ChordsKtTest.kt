package kLoops.music

import org.junit.Assert.assertEquals
import org.junit.Test

class ChordsKtTest {
    @Test
    fun toNote() {
        val note = "c0".toNote()
        assertEquals(24, note)
        val note2 = "a3".toNote()
        assertEquals(69, note2)
        val note3 = "3".toNote()
        assertEquals(3, note3)
        val note4 = "127".toNote()
        assertEquals(127, note4)

    }
}