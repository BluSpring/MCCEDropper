package xyz.bluspring.mccedropper.level

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.Main
import java.util.*

data class DropperLevel(
    var id: Int,
    var title: String,
    var description: String,
    val builders: MutableList<UUID>,
    var platformFirstPos: Location,
    var platformSecondPos: Location,
    var spawn: Location,
    //var remaining: Int,
    var chatTitle: String,
    var chatDescription: String,

    val finishPoints: MutableList<List<Location>>,
    // in ticks
    var totalTime: Long = 180L,
    var currentTime: Long = 0L,

    var started: Boolean = false,
    val completed: MutableList<UUID> = mutableListOf(),

    var bossBar: BossBar? = null
){
    fun serialize(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("dataVersion", DATA_VERSION)

        jsonObject.addProperty("id", id)
        jsonObject.addProperty("title", title)
        jsonObject.addProperty("description", description)

        val buildersArray = JsonArray()
        builders.forEach {
            val player = Main.plugin.server.getOfflinePlayer(it)

            buildersArray.add(SerializedPlayer(player.name ?: "NoNameFoundYaBitchFuck", player.uniqueId).serialize())
        }
        jsonObject.add("builders", buildersArray)

        jsonObject.add("platformFirstPos", serializeLocation(platformFirstPos))
        jsonObject.add("platformSecondPos", serializeLocation(platformSecondPos))

        jsonObject.add("spawn", serializeLocation(spawn))

        //jsonObject.addProperty("remaining", remaining)
        jsonObject.addProperty("chatTitle", chatTitle)
        jsonObject.addProperty("chatDescription", chatDescription)

        // version 1
        //jsonObject.add("finishFirstPos", serializeLocation(finishFirstPos))
        //jsonObject.add("finishSecondPos", serializeLocation(finishSecondPos))

        // version 2
        val serializedFinishPoints = JsonArray()
        finishPoints.forEach {
            val serializedFinishPoint = JsonArray()

            serializedFinishPoint.add(serializeLocation(it[0]))
            serializedFinishPoint.add(serializeLocation(it[1]))

            serializedFinishPoints.add(serializedFinishPoint)
        }

        jsonObject.add("finishPoints", serializedFinishPoints)
        jsonObject.addProperty("totalTime", totalTime)

        return jsonObject
    }

    fun displayInfo(player: Player) {
        player.sendTitle(title, description, 10, 150, 20)
        player.sendMessage("${ChatColor.DARK_GRAY}-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-")
        player.sendMessage("")
        player.sendMessage("$chatTitle\n${ChatColor.DARK_GRAY}------------------")
        player.sendMessage("")
        player.sendMessage("${ChatColor.RESET}$chatDescription")
        player.sendMessage("")
        player.sendMessage("${ChatColor.RESET}${ChatColor.GOLD}${ChatColor.BOLD}Builders > ${ChatColor.RESET}${ChatColor.GREEN}${
            if (builders.isEmpty()) "None (wait wtf admins credit your builders)" else builders.map { Main.plugin.server.getOfflinePlayer(it).name }
                .joinToString(", ")
        }")
    }

    private fun serializeLocation(loc: Location): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("world", loc.world.name)
        jsonObject.addProperty("x", loc.blockX.toDouble())
        jsonObject.addProperty("y", loc.blockY.toDouble())
        jsonObject.addProperty("z", loc.blockZ.toDouble())

        return jsonObject
    }

    companion object {
        const val DATA_VERSION = 2

        fun deserialize(data: JsonObject): DropperLevel = DropperLevel(
            data.get("id").asInt,
            data.get("title").asString,
            data.get("description").asString,
            data.get("builders").asJsonArray.mapNotNull { SerializedPlayer.deserialize(it.asJsonObject).uuid }.toMutableList(),
            Location(Main.plugin.server.getWorld(data.get("platformFirstPos").asJsonObject.get("world").asString), data.get("platformFirstPos").asJsonObject.get("x").asDouble, data.get("platformFirstPos").asJsonObject.get("y").asDouble, data.get("platformFirstPos").asJsonObject.get("z").asDouble),
            Location(Main.plugin.server.getWorld(data.get("platformSecondPos").asJsonObject.get("world").asString), data.get("platformSecondPos").asJsonObject.get("x").asDouble, data.get("platformSecondPos").asJsonObject.get("y").asDouble, data.get("platformSecondPos").asJsonObject.get("z").asDouble),
            Location(Main.plugin.server.getWorld(data.get("spawn").asJsonObject.get("world").asString), data.get("spawn").asJsonObject.get("x").asDouble, data.get("spawn").asJsonObject.get("y").asDouble, data.get("spawn").asJsonObject.get("z").asDouble),
            //data.get("remaining").asInt,
            data.get("chatTitle").asString,
            data.get("chatDescription").asString,
            /*Location(Main.plugin.server.getWorld(data.get("finishFirstPos").asJsonObject.get("world").asString), data.get("finishFirstPos").asJsonObject.get("x").asDouble, data.get("finishFirstPos").asJsonObject.get("y").asDouble, data.get("finishFirstPos").asJsonObject.get("z").asDouble),
            Location(Main.plugin.server.getWorld(data.get("finishSecondPos").asJsonObject.get("world").asString), data.get("finishSecondPos").asJsonObject.get("x").asDouble, data.get("finishSecondPos").asJsonObject.get("y").asDouble, data.get("finishSecondPos").asJsonObject.get("z").asDouble)*/
            getFinishPoints(data),
            data.get("totalTime").asLong
        )

        private fun getFinishPoints(data: JsonObject): MutableList<List<Location>> =
            // Convert data v1 to data v2
            if (!data.has("dataVersion") || data.get("dataVersion").asInt == 1) {
                mutableListOf(listOf(
                    Location(Main.plugin.server.getWorld(data.get("finishFirstPos").asJsonObject.get("world").asString), data.get("finishFirstPos").asJsonObject.get("x").asDouble, data.get("finishFirstPos").asJsonObject.get("y").asDouble, data.get("finishFirstPos").asJsonObject.get("z").asDouble),
                    Location(Main.plugin.server.getWorld(data.get("finishSecondPos").asJsonObject.get("world").asString), data.get("finishSecondPos").asJsonObject.get("x").asDouble, data.get("finishSecondPos").asJsonObject.get("y").asDouble, data.get("finishSecondPos").asJsonObject.get("z").asDouble)
                ))
            } else if (data.get("dataVersion").asInt == 2) {
                val locations = data.get("finishPoints").asJsonArray

                locations.map {
                    val finishPoint = it.asJsonArray
                    val firstPos = finishPoint[0].asJsonObject
                    val secondPos = finishPoint[1].asJsonObject

                    listOf(
                        Location(
                            Main.plugin.server.getWorld(firstPos.get("world").asString),
                            firstPos.get("x").asDouble,
                            firstPos.get("y").asDouble,
                            firstPos.get("z").asDouble
                        ),

                        Location(
                            Main.plugin.server.getWorld(secondPos.get("world").asString),
                            secondPos.get("x").asDouble,
                            secondPos.get("y").asDouble,
                            secondPos.get("z").asDouble,
                        )
                    )
                }.toMutableList()
            } else {
                mutableListOf()
            }
    }
}