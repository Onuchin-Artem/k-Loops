package kLoops.internal

import org.junit.Assert.assertNotNull
import org.junit.Test

class LiveStateKtTest {

    @Test
    fun parseState() {
        "/state.json".asResource {
            assertNotNull(parseState(it))
        }
    }
}