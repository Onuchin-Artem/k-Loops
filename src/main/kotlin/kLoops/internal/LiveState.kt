package kLoops.internal

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.plus
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kLoops.music.o
import kLoops.music.toNoteLength
import java.util.concurrent.CountDownLatch

data class State(
        val bpm: Int,
        val signature: kLoops.music.NoteLength,
        val tracks: List<Track>
) {
    private val tracksSearch = tracks.map { it.name to it }.toSearch()

    init {
        check(signature == 4 o 4) {
            "Please set signature to 4/4! " +
                    "Different signatures are not supported yet\n" +
                    "Notice that you can control signature in k-Loops by setting setPulsePeriod(noteLength)"
        }
    }

    fun lookupTrackId(name: String): Track =
            tracksSearch.findOrElse(name) { throw IllegalArgumentException("No track $name") }

    val numberOfBeatsInBar = 4
}


data class DrumPad(val name: String, val id: Int, val note: Int)

interface HasParameters {
    fun device(device: String): HasParameters
    fun parameter(parameter: String): Parameter
}

private fun traverse(devices: List<Device>): List<Device> =
        devices.flatMap { device -> traverse(device.devices) + listOf(device) }

data class Track(
        val name: String,
        val id: Int,
        val devices: List<Device>,
        val mixParameters: List<Parameter>
) : HasParameters {
    private val drumsSearch = traverse(devices)
            .flatMap { it.drumpads }
            .map { it.name to it.note }
            .toSearch()

    private val devicesSearch = devices
            .map { it.name to it }
            .toSearch()

    private val mixParametersSearch = mixParameters
            .map { it.name to it }
            .toSearch()

    val mainDevice = devices.filter { it.isInstrument }.firstOrNull()

    fun lookupDrumNote(name: String): Int =
            drumsSearch.findOrElse(name) { throw IllegalArgumentException("No drum $name") }

    override fun device(device: String): HasParameters =
            devicesSearch.findOrElse(device) { throw IllegalArgumentException("No device $device") }

    override fun parameter(parameter: String) =
            mixParametersSearch.findOrElse(parameter) { throw IllegalArgumentException("No device $parameter") }
}


data class Device(
        val name: String,
        val id: Int,
        val parameters: List<Parameter>,
        val drumpads: List<DrumPad>,
        val devices: List<Device>,
        val isInstrument: Boolean) : HasParameters {
    private val devicesSearch = devices
            .map { it.name to it }
            .toSearch()
    private val mixParametersSearch = parameters
            .map { it.name to it }
            .toSearch()

    override fun device(device: String): HasParameters =
            devicesSearch.findOrElse(device) { throw IllegalArgumentException("No device $device") }

    override fun parameter(parameter: String) =
            mixParametersSearch.findOrElse(parameter) { throw IllegalArgumentException("No device $parameter") }

}

data class Parameter(val name: String, val id: Int)


fun parseState(json: String): State {
    val gson = Gson()
    val jsonObj = gson.fromJson<JsonObject>(json)
    return State(
            bpm = jsonObj["bpm"].asInt,
            signature = jsonObj["sig"].asString.toNoteLength(),
            tracks = parseTracks(
                    jsonObj["tracks"].asJsonArray +
                            jsonObj["returns"].asJsonArray +
                            listOf(jsonObj["master"].asJsonObject))
    )
}

fun parseTracks(jsonArray: JsonArray) = jsonArray.map { parseTrack(it.asJsonObject) }

fun parseTrack(jsonObj: JsonObject) = Track(
        id = jsonObj["id"].asInt,
        name = jsonObj["name"].asString,
        devices = parseDevices(jsonObj),
        mixParameters = parseParameters(
                jsonObj["sends"].asJsonArray +
                        listOf(jsonObj["volume"].asJsonObject) +
                        listOf(jsonObj["panning"].asJsonObject)

        )
)

fun parseParameters(jsonArray: JsonArray) = jsonArray.map { parseParameter(it.asJsonObject) }


fun parseParameter(jsonObj: JsonObject) = Parameter(
        id = jsonObj["id"].asInt,
        name = jsonObj["name"].asString
)

fun parseDevices(jsonObj: JsonObject): List<Device> = jsonObj["devices"].asJsonArray.map { parseDevice(it.asJsonObject) }

fun parseDevice(jsonObj: JsonObject) = Device(
        id = jsonObj["id"].asInt,
        name = jsonObj["title"].asString,
        parameters = parseParameters(jsonObj["parameters"].asJsonArray),
        drumpads = jsonObj["drumpads"].asJsonArray.map { parseDrumpad(it.asJsonObject) },
        devices = parseDevices(jsonObj),
        isInstrument = jsonObj["type"].asInt == 1)


fun parseDrumpad(jsonObj: JsonObject) = DrumPad(
        id = jsonObj["id"].asInt,
        name = jsonObj["name"].asString,
        note = jsonObj["note"].asInt)

object Live {
    private var state: State? = null;
    private val countDownLatch = CountDownLatch(1)

    fun state(): State {
        countDownLatch.await()
        return state!!
    }

    fun state(newState: State) {
        state = newState
        countDownLatch.countDown()
    }
}