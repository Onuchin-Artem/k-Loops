package kLoops.internal

import kLoops.music.LoopContext
import kLoops.music.NoteLength
import kLoops.music.beat
import java.util.concurrent.ConcurrentHashMap


data class Command(val beginOfCommand: NoteLength, val command: String) {
    val isActive = command != ""
}

class LoopRunner(val context: LoopContext, val block: LoopContext.() -> Unit) {
    val commands = mutableListOf<Command>()
    var beginTime = nextBarBit().beat()

    fun addCommand(noteLength: NoteLength, commandTemplate: String) {

        val beginBeats = beginTime.beatInBar().toBeats() + 1.0
        val command = commandTemplate.replace("{time}", beginBeats.toString())
        commands.add(Command(beginTime, command))
        addWait(noteLength)
    }

    fun addWait(noteLength: NoteLength) {
        beginTime += noteLength
        commands.add(Command(beginTime, ""))
    }

    fun processBitUpdate(bit: Int) {
        tryRerunLoop()

        var topCommand = commands.get(0)

        while (topCommand.beginOfCommand >= bit.beat()
                && topCommand.beginOfCommand < (bit + 1).beat()) {
            commands.removeAt(0)
            if (topCommand.isActive) {
                commandQueue.offer(topCommand.command)
            }
            tryRerunLoop()
            topCommand = commands.get(0)
        }
    }

    private fun tryRerunLoop() {
        if (commands.isEmpty()) {
            block.invoke(context)
            check(commands.isEmpty().not()) {
                "Loop " + context.loopName + " should have content"
            }
        }
    }
}

object LoopRunners {
    val loopsMap = ConcurrentHashMap<String, LoopRunner>()

    fun processBitUpdate(bit: Int) {

        for (runner in loopsMap.values) {
            runner.processBitUpdate(bit)
        }

    }

    fun getLoop(context: LoopContext): LoopRunner = loopsMap[context.loopName]!!

    fun registerLoop(context: LoopContext, block: LoopContext.() -> Unit) {
        loopsMap[context.loopName] = LoopRunner(context, block)
    }
}