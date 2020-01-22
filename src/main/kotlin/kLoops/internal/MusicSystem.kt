package kLoops.internal

import kLoops.music.LoopContext
import kLoops.music.NoteLength
import kLoops.music.beat
import kLoops.music.o
import java.util.concurrent.ArrayBlockingQueue

sealed class Command : Comparable<Command> {
    abstract val beginOfCommand: NoteLength;
    override operator fun compareTo(other: Command): Int =
            compareBy(Command::beginOfCommand)
                    .thenComparing(compareBy(Command::toString))
                    .compare(this, other)

}

data class Message(override val beginOfCommand: NoteLength, val command: String) : Command()
data class Event(override val beginOfCommand: NoteLength, val event: String, val parameter: Any) : Command()
data class Nothing(override val beginOfCommand: NoteLength) : Command()
data class ChangeLoopVelocity(override val beginOfCommand: NoteLength, val loop: String, val velocity: Double) : Command()
data class BroadcastParameter(override val beginOfCommand: NoteLength, val parameter: String, val value: Any) : Command()


class MusicPhraseRunner(val context: LoopContext, val block: LoopContext.() -> Unit) {
    private val commands = sortedSetOf<Command>()
    var beginTime = nextBarBit().beat()
    var loopVelocity = 1.0

    fun addCommand(commandTemplate: String) {
        val beginBeats = beginTime.beatInBar().toBeats() + 1.0
        val command = commandTemplate.replace("{time}", beginBeats.toString())
        commands.add(Message(beginTime, command))
    }

    fun addWait(noteLength: NoteLength) {
        beginTime += noteLength
        commands.add(Nothing(beginTime))
    }

    fun addEvent(triggerEvent: String, parameter: Any) {
        commands.add(Event(beginTime, triggerEvent, parameter))
    }

    fun addChangeLoopVelocity(velocity: Double) {
        commands.add(ChangeLoopVelocity(beginTime, context.loopName, velocity))
    }

    fun addChangeLoopVelocity(loop: String, velocity: Double) {
        commands.add(ChangeLoopVelocity(beginTime, loop, velocity))
    }


    fun addBroadcastParameter(parameter: String, value: Any) {
        commands.add(BroadcastParameter(beginTime, parameter, value))
    }

    fun processBitUpdate(bit: Int): Int {

        if (commands.isEmpty()) {
            return 0
        }
        var processedNum = 0
        var topCommand: Command? = commands.first()

        while (topCommand != null
                && topCommand.beginOfCommand < (bit + 1).beat()) {
            commands.remove(topCommand)
            processedNum++
            if (topCommand.beginOfCommand >= bit.beat()) {
                when (topCommand) {
                    is Message -> commandQueue.offer(topCommand.command)
                    is Event -> MusicPhraseRunners.processEvent(topCommand)
                    is ChangeLoopVelocity -> MusicPhraseRunners.getMusicPhrase(topCommand.loop).loopVelocity = topCommand.velocity
                    is BroadcastParameter -> MusicPhraseRunners.broadcastParameter(topCommand.parameter, topCommand.value)
                    is Nothing -> {
                    }
                }
            }
            if (commands.isNotEmpty()) topCommand = commands.first()
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
@Volatile
var pulsePeriod = 4 o 4

object MusicPhraseRunners {
    private val runnersMap = mutableMapOf<String, MusicPhraseRunner>()
    private val eventsListeners = mutableMapOf<String, MutableSet<MusicPhraseRunner>>()
    private val broadcastedParameters = mutableMapOf<String, Any>()

    init {
        val context = LoopContext("pulse", events = listOf("loop_pulse"))
        val block = makeLoop {
            while (eventsQueue.isNotEmpty()) {
                processEvent(Event(getMusicPhrase(context).beginTime, eventsQueue.poll(), Any()))
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
    fun getMusicPhrase(loopName: String): MusicPhraseRunner = runnersMap[loopName]!!

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
    fun processEvent(event: Event) {
        eventsListeners[event.event]?.forEach {
            it.beginTime = event.beginOfCommand
            it.context.parameter = event.parameter
            it.context.trigger = event.event
            it.runCommands()
        }
    }

    @Synchronized
    fun readParameter(field: String) = broadcastedParameters[field]

    @Synchronized
    fun broadcastParameter(field: String, value: Any) {
        broadcastedParameters[field] = value
    }
}

fun makeLoop(block: LoopContext.() -> Unit): LoopContext.() -> Unit {
    return fun LoopContext.() {
        block.invoke(this)
        MusicPhraseRunners.getMusicPhrase(this).addEvent("loop_$loopName", Any())
    }
}