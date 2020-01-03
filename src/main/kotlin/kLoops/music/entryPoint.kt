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


private fun makeLoop(block: LoopContext.() -> Unit) : LoopContext.() -> Unit {
    return fun LoopContext.() {
        block.invoke(this)
        MusicPhraseRunners.getMusicPhrase(this).addEvent("loop_$loopName")
    }
}

fun loop(loopName: String, block: LoopContext.() -> Unit) {
    val context = LoopContext(loopName, events = listOf("loop_$loopName"))
    MusicPhraseRunners.registerEventListener(context, makeLoop(block))
    MusicPhraseRunners.getMusicPhrase(context).runCommands()
}

fun trigger(loopName: String, triggerEvents: List<String>,  block: LoopContext.() -> Unit) {
    val context = LoopContext(loopName, triggerEvents)
    MusicPhraseRunners.registerEventListener(context, block)
}