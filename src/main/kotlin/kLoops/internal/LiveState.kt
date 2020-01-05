package kLoops.internal

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.plus
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kLoops.music.o
import kLoops.music.toNoteLength
import java.util.concurrent.CountDownLatch

data class State(
        val bpm: Int,
        val signature: kLoops.music.NoteLength,
        val tracks: List<Track>
) {
    val reverseNameMap = tracks.map { it.name to it.id }.toSearch()

    init {
        check(signature == 4 o 4) {
            "Please set signature to 4/4! " +
                    "Different signatures are not supported yet\n" +
                    "Notice that you can control signature in k-Loops by setting setPulsePeriod(noteLength)"
        }
    }

    fun lookupTrackId(name: String): Int =
            reverseNameMap.findOrElse(name) { throw IllegalArgumentException("No track $name") }

    fun lookupTrackId(number: Int): Int =
            tracks[number - 1].id

    val numberOfBeatsInBar = 4
}

data class Parameter(val name: String, val id: Int)

data class DrumPad(val name: String, val id: Int, val note: Int)

data class Device(val name: String, val id: Int, val parameters: List<Parameter>, val drumpads: List<DrumPad>)


data class Track(
        val name: String,
        val id: Int,
        val devices: List<Device>,
        val mixParameters: List<Parameter>
)

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
        devices = jsonObj["devices"].asJsonArray.map { parseDevice(it.asJsonObject) },
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

fun parseDevice(jsonObj: JsonObject) = Device(
        id = jsonObj["id"].asInt,
        name = jsonObj["title"].asString,
        parameters = parseParameters(jsonObj["parameters"].asJsonArray),
        drumpads = jsonObj["drumpads"].asJsonArray.map { parseDrumpad(it.asJsonObject)}
)

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