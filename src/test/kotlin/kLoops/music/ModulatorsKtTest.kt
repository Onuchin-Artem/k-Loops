package kLoops.music

import org.junit.Assert.assertEquals
import org.junit.Test

class ModulatorsKtTest {

    val context = LoopContext("test", listOf())

    @Test
    fun triag() {
        val triag = context.triag(period = 8)
        val actual = (0..8).map { triag.tick("tr1") }
        val expected = listOf(0.0, 0.25, 0.5, 0.75, 1.0, 0.75, 0.5, 0.25, 0.0)
        assertEquals(expected, actual)

        val triag2 = context.triag(period = 8, t1 = 3)
        val actual2 = (0..8).map { triag2.tick("tr2") }
        val expected2 = listOf(0.0, 0.3333333333333333, 0.6666666666666666, 1.0, 0.8, 0.6, 0.4, 0.2, 0.0)
        assertEquals(expected2, actual2)
    }

    @Test
    fun rect() {
        val rect = context.rect(period = 8)
        val actual = (0..8).map { rect.tick("r1") }
        val expected = listOf(1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.00)
        assertEquals(expected, actual)

        val rect2 = context.rect(period = 8, t1 = 3, phase = 0.5)
        val actual2 = (0..8).map { rect2.tick("r2") }
        val expected2 = listOf(0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0)
        assertEquals(expected2, actual2)
    }
}