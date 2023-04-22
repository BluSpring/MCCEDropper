package xyz.bluspring.mccedropper.points

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.Main
import java.io.File
import java.util.*
import kotlin.math.min

object PointsManager {
    val points = mutableMapOf<UUID, PointTrackedPlayer>()

    fun load() {
        val json = JsonParser().parse(File(Main.plugin.dataFolder, "points.json").readText()).asJsonObject

        json.entrySet().forEach {
            val offlinePlayer = Main.plugin.server.getOfflinePlayer(UUID.fromString(it.key))
            points[UUID.fromString(it.key)] = PointTrackedPlayer(
                offlinePlayer,
                it.value.asJsonObject.get("points").asInt
            )
        }
    }

    fun save() {
        val json = JsonObject()

        points.forEach {
            json.add(it.key.toString(), it.value.serialize())
        }

        File(Main.plugin.dataFolder, "points.json").writeText(json.toString())
    }

    fun createTrackedPlayer(player: OfflinePlayer): PointTrackedPlayer {
        if (points.containsKey(player.uniqueId))
            return points[player.uniqueId]!!

        points[player.uniqueId] = PointTrackedPlayer(
            player,
            0
        )

        return points[player.uniqueId]!!
    }

    fun displayLeaderboard(sender: CommandSender): List<Pair<OfflinePlayer, Int>> {
        val trueStatsList = points.map { Main.plugin.server.getOfflinePlayer(it.key) to it.value.points }.sortedByDescending { it.second }
        val statsList = trueStatsList.subList(0, min(9.0, points.size.toDouble()).toInt())

        sender.sendMessage("")
        sender.sendMessage("${ChatColor.LIGHT_PURPLE}Total collected points:")
        sender.sendMessage("")

        statsList.forEach {
            sender.sendMessage("    ${statsList.indexOf(it) + 1}. ${if (it.first.isOnline) ChatColor.GREEN else ChatColor.RED}${it.first.name} ${ChatColor.RESET}(${ChatColor.GOLD}${it.second}${ChatColor.RESET})")
        }

        return trueStatsList
    }
}