package kLoops.internal

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import java.util.concurrent.ArrayBlockingQueue


val commandQueue = ArrayBlockingQueue<String>(1024)
val stateQueue = ArrayBlockingQueue<String>(1024)
val bitsQueue = ArrayBlockingQueue<Int>(1024)


suspend fun communicateLoop() {
    val client = HttpClient {
        install(WebSockets)
    }

    client.ws(
            method = HttpMethod.Get,
            host = "127.0.0.1",
            port = 8081,
            path = "/"
    ) {
        println("Connected to Ableton")
        while (true) {
            try {
                val message = commandQueue.poll()
                if (message != null) {
                    send(message)
                }
                val frame = incoming.poll()
                if (frame != null && frame is Frame.Text) {
                    val messageReceived = frame.readText()
                    if (messageReceived.startsWith("{")) {

                        stateQueue.offer(messageReceived)
                    }
                    if (messageReceived.contains(" seq ")) {
                        bitsQueue.offer(parseBit(messageReceived))
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}

private fun parseBit(messageReceived: String) =
        messageReceived.split(" seq ")[1].toInt() - 1


fun stateRequestLoop() {
    var lastRequested = 1000L
    while (true) {
        val state = stateQueue.poll()
        if (state != null) {
            Live.state(parseState(state))
        }
        if (System.currentTimeMillis() - lastRequested > 30000) {
            commandQueue.offer("get_scene")
            lastRequested = System.currentTimeMillis()
        }
    }
}

@Volatile var bar = 0

fun musicLoop() {
    while (true) {
        var lastBit = 0
        try {
            val bit = bitsQueue.poll() ?: continue
            if (bit in 1 until lastBit) continue
            if (bit == 0) {
                bar++
            }
            lastBit = bar * Live.state().numberOfBeatsInBar + bit
            MusicPhraseRunners.processBitUpdate(lastBit)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}

fun nextBarBit() = (bar + 1) * Live.state().numberOfBeatsInBar