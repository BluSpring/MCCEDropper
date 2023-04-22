package xyz.bluspring.mccedropper.level

import com.google.gson.JsonObject
import java.util.*

data class SerializedPlayer(
    val name: String,
    val uuid: UUID
) {
    fun serialize(): JsonObject {
        val obj = JsonObject()
        obj.addProperty("name", name)
        obj.addProperty("uuid", uuid.toString())

        return obj
    }

    companion object {
        fun deserialize(data: JsonObject): SerializedPlayer = SerializedPlayer(
            data.get("name").asString,
            UUID.fromString(data.get("uuid").asString)
        )
    }
}