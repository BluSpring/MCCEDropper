package xyz.bluspring.mccedropper.points

import com.google.gson.JsonObject
import org.bukkit.OfflinePlayer

data class PointTrackedPlayer(
    val player: OfflinePlayer,
    var points: Int
) {
    fun serialize(): JsonObject {
        val obj = JsonObject()

        obj.addProperty("player", player.uniqueId.toString())
        obj.addProperty("points", points)

        return obj
    }
}