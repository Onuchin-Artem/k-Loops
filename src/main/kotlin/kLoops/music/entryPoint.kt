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


fun loop(name: String, block: LoopContext.() -> Unit) {
    val context = LoopContext(name)
    LoopRunners.registerLoop(context, block)
}