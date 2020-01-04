package kLoops.music

import kLoops.internal.*
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

fun startBackgroundTasks(): List<Thread> {
    commandQueue.offer("get_scene")
    return listOf(
            thread { runBlocking { communicateLoop() } },

            thread { stateRequestLoop() },

            thread { musicLoop() })
}


fun loop(loopName: String, block: LoopContext.() -> Unit) {
    val context = LoopContext(loopName, events = listOf("loop_$loopName"))
    MusicPhraseRunners.registerEventListener(context, makeLoop(block))
    triggerEventNextPulse("loop_$loopName")
}

fun runWhenEvent(loopName: String, triggerEvents: List<String>,  block: LoopContext.() -> Unit) {
    val context = LoopContext(loopName, triggerEvents)
    MusicPhraseRunners.registerEventListener(context, block)
}

fun triggerEventNextPulse(event: String) {
    eventsQueue.offer(event)
}

fun setPulsePeriod(length: NoteLength) {
    pulsePeriod = length
}