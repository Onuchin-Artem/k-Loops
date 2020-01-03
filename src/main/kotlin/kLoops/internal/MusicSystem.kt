package kLoops.internal

import kLoops.music.LoopContext
import kLoops.music.NoteLength
import kLoops.music.beat

enum class CommandType {
    Message, Event, Nothing
}

data class Command(val beginOfCommand: NoteLength, val type: CommandType, val command: String) {
}

class MusicPhraseRunner(val context: LoopContext, val block: LoopContext.() -> Unit) {
    val commands = mutableListOf<Command>()
    var beginTime = nextBarBit().beat()

    fun addCommand(noteLength: NoteLength, commandTemplate: String) {

        val beginBeats = beginTime.beatInBar().toBeats() + 1.0
        val command = commandTemplate.replace("{time}", beginBeats.toString())
        commands.add(Command(beginTime, CommandType.Message, command))
        addWait(noteLength)
    }

    fun addWait(noteLength: NoteLength) {
        beginTime += noteLength
        commands.add(Command(beginTime, CommandType.Nothing, ""))
    }

    fun addEvent(triggerEvent: String) {
        commands.add(Command(beginTime, CommandType.Event, triggerEvent))
    }

    fun processBitUpdate(bit: Int) {
        if (commands.isEmpty()) {
            return
        }
        var topCommand : Command? = commands[0]

        while (topCommand != null &&
                topCommand.beginOfCommand >= bit.beat()
                && topCommand.beginOfCommand < (bit + 1).beat()) {
            commands.removeAt(0)
            when (topCommand.type) {
                CommandType.Message -> commandQueue.offer(topCommand.command)
                CommandType.Event -> MusicPhraseRunners.triggerEvent(topCommand.command)
                CommandType.Nothing -> {}
            }
            if (commands.isNotEmpty()) topCommand = commands[0]
            else topCommand = null
        }
    }

    fun runCommands() {
        block.invoke(context)
    }

    override operator fun equals(other: Any?): Boolean =
            other is MusicPhraseRunner && context.loopName == other.context.loopName

    override fun hashCode(): Int = context.loopName.hashCode()
}

object MusicPhraseRunners {
    val runnersMap = mutableMapOf<String, MusicPhraseRunner>()
    val eventsListeners = mutableMapOf<String, MutableSet<MusicPhraseRunner>>()

    //called from music loop only
    @Synchronized fun processBitUpdate(bit: Int) {
        println(bit)
        for (runner in runnersMap.values) {
            runner.processBitUpdate(bit)
        }
    }

    //called from music loop only
    @Synchronized fun getMusicPhrase(context: LoopContext): MusicPhraseRunner = runnersMap[context.loopName]!!

    @Synchronized fun registerEventListener(context: LoopContext, block: LoopContext.() -> Unit) {
        val newRunner = MusicPhraseRunner(context, block)
        runnersMap[context.loopName] = newRunner
        eventsListeners.forEach { (event, listeners) -> listeners.remove(newRunner) }
        context.events.forEach { event ->
            eventsListeners.computeIfAbsent(event) { mutableSetOf() }
            eventsListeners[event]!!.add(newRunner)
        }
    }

    //called from music loop only
    @Synchronized fun triggerEvent(eventName: String) {
        eventsListeners[eventName]?.forEach {
            it.runCommands()
        }
    }
}
