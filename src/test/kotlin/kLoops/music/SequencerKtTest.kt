package kLoops.music

import org.junit.Test

import org.junit.Assert.*

class SequencerKtTest {

    @Test
    fun euclideanRythm() {
        assertEquals("k . k . .", euclideanRythm(2, 5, "k"))
        assertEquals("x . x . x . x . .", euclideanRythm(4, 9, "x"))
    }
}