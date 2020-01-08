package kLoops.music

import kLoops.internal.*
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

var listOfThreads: List<Thread>? = null

@Synchronized fun startBackgroundTasks(): List<Thread> {
    if (listOfThreads != null) {
        return listOfThreads!!
    }
    println("started")
    commandQueue.offer("get_scene")
    listOfThreads = listOf(
            thread { runBlocking { communicateLoop() } },
            thread { stateRequestLoop() },
            thread { musicLoop() })
    return listOfThreads!!
}
@Synchronized fun reset() {
    if (listOfThreads == null) return
    listOfThreads!!.forEach { it.stop() }
    listOfThreads = null
    println("stopped")
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