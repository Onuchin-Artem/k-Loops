package kLoops.internal

import kLoops.music.LoopContext
import kLoops.music.NoteLength
import kLoops.music.beat
import kLoops.music.o
import java.util.concurrent.ArrayBlockingQueue

enum class CommandType {
    Message, Event, Nothing, ChangeLoopVelocity
}

data class Command(val beginOfCommand: NoteLength, val type: CommandType, val command: String)

class MusicPhraseRunner(val context: LoopContext, val block: LoopContext.() -> Unit) {
    val commands = mutableListOf<Command>()
    var beginTime = nextBarBit().beat()
    var loopVelocity = 1.0

    fun addCommand(commandTemplate: String) {
        val beginBeats = beginTime.beatInBar().toBeats() + 1.0
        val command = commandTemplate.replace("{time}", beginBeats.toString())
        commands.add(Command(beginTime, CommandType.Message, command))
    }

    fun addWait(noteLength: NoteLength) {
        beginTime += noteLength
        commands.add(Command(beginTime, CommandType.Nothing, ""))
    }

    fun addEvent(triggerEvent: String) {
        commands.add(Command(beginTime, CommandType.Event, triggerEvent))
    }

    fun addChangeLoopVelocity(velocity: Double) {
        commands.add(Command(beginTime, CommandType.ChangeLoopVelocity, velocity.toString()))
    }

    fun processBitUpdate(bit: Int): Int {

        if (commands.isEmpty()) {
            return 0
        }
        var processedNum = 0
        var topCommand: Command? = commands[0]

        while (topCommand != null &&
                topCommand.beginOfCommand >= bit.beat()
                && topCommand.beginOfCommand < (bit + 1).beat()) {
            commands.removeAt(0)
            processedNum++
            when (topCommand.type) {
                CommandType.Message -> commandQueue.offer(topCommand.command)
                CommandType.Event -> MusicPhraseRunners.processEvent(topCommand.command, topCommand.beginOfCommand)
                CommandType.ChangeLoopVelocity -> loopVelocity = topCommand.command.toDouble()
                CommandType.Nothing -> {}
            }
            if (commands.isNotEmpty()) topCommand = commands[0]
            else topCommand = null
        }
        return processedNum
    }

    fun runCommands() {
        block.invoke(context)
    }

    override operator fun equals(other: Any?): Boolean =
            other is MusicPhraseRunner && context.loopName == other.context.loopName

    override fun hashCode(): Int = context.loopName.hashCode()
}

val eventsQueue = ArrayBlockingQueue<String>(1024)
@Volatile var pulsePeriod = 4 o 4

object MusicPhraseRunners {
    private val runnersMap = mutableMapOf<String, MusicPhraseRunner>()
    private val eventsListeners = mutableMapOf<String, MutableSet<MusicPhraseRunner>>()

    init {
        val context = LoopContext("pulse", events = listOf("loop_pulse"))
        val block = makeLoop {
            while (eventsQueue.isNotEmpty()) {
                processEvent(eventsQueue.poll(), getMusicPhrase(context).beginTime)
            }
            silence(pulsePeriod)
        }
        registerEventListener(context, block)
        getMusicPhrase(context).runCommands()
    }
    //called from music loop only
    @Synchronized
    fun processBitUpdate(bit: Int) {
        do {
            val numberOfProcessedCommands = runnersMap.values.map { runner -> runner.processBitUpdate(bit) }.sum()
        } while (numberOfProcessedCommands > 0)
    }

    //called from music loop only
    @Synchronized
    fun getMusicPhrase(context: LoopContext): MusicPhraseRunner = runnersMap[context.loopName]!!

    @Synchronized
    fun registerEventListener(context: LoopContext, block: LoopContext.() -> Unit) {
        val newRunner = MusicPhraseRunner(context, block)
        runnersMap[context.loopName] = newRunner
        eventsListeners.keys.forEach { event -> eventsListeners[event]!!.remove(newRunner) }
        context.events.forEach { event ->
            eventsListeners.computeIfAbsent(event) { mutableSetOf() }
            eventsListeners[event]!!.add(newRunner)
        }
    }


    //called from music loop only
    @Synchronized
    fun processEvent(eventName: String, newBeginTime: NoteLength) {
        eventsListeners[eventName]?.forEach {
            it.beginTime = newBeginTime
            it.commands.clear()
            it.runCommands()
        }
    }
}

fun makeLoop(block: LoopContext.() -> Unit) : LoopContext.() -> Unit {
    return fun LoopContext.() {
        block.invoke(this)
        MusicPhraseRunners.getMusicPhrase(this).addEvent("loop_$loopName")
    }
}