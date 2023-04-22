package xyz.bluspring.mccedropper.level

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.*
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.Main
import xyz.bluspring.mccedropper.points.PointsManager
import java.io.File

object LevelManager {
    val levels: MutableList<DropperLevel> = mutableListOf()

    var firstFinisher: OfflinePlayer? = null

    fun load() {
        val json = JsonParser().parse(File(Main.plugin.dataFolder, "config.json").readText())

        val droppers = json.asJsonObject.get("droppers").asJsonArray

        droppers.forEach {
            println("Loading level ${it.asJsonObject.get("id").asInt}")

            levels.add(DropperLevel.deserialize(it.asJsonObject))
        }
    }

    fun save() {
        val json = JsonObject()
        val array = JsonArray()

        levels.forEach {
            array.add(it.serialize())
        }

        json.add("droppers", array)

        File(Main.plugin.dataFolder, "config.json").writeText(json.toString())
    }

    fun createLevel(id: Int, creator: Player?): DropperLevel {
        val level = DropperLevel(
            id,
            "${ChatColor.GOLD}Level ${ChatColor.GREEN}$id",
            "This is an example description, admins please fill this in.",
            mutableListOf(),
            Location(Main.plugin.server.worlds.first(), 0.0, 0.0, 0.0),
            Location(Main.plugin.server.worlds.first(), 0.0, 0.0, 0.0),
            creator?.location ?: Location(Main.plugin.server.worlds.first(), 0.0, 0.0, 0.0),
            //1,
            "${ChatColor.GOLD}Level ${ChatColor.GREEN}$id",
            "This is an example description so the admins can fill whatever in. To add more text, you can just use a command block to do the same thing but longer, even temporarily. Or console. That works too. Feel free to write something witty here too. To do newline, just type \\n.",

            mutableListOf(),
            180L
        )

        this.levels.add(level)

        return level
    }

    fun stopLevel(level: DropperLevel) {
        if (!level.started)
            return

        level.started = false
        firstFinisher = null

        val bossBar = Main.plugin.server.getBossBar(NamespacedKey.fromString("level_${level.id}", Main.plugin)!!)
        bossBar?.removeAll()

        val region = CuboidRegion(BukkitAdapter.adapt(level.platformFirstPos.world), BlockVector3.at(level.platformFirstPos.blockX, level.platformFirstPos.blockY, level.platformFirstPos.blockZ), BlockVector3.at(level.platformSecondPos.blockX, level.platformSecondPos.blockY, level.platformSecondPos.blockZ))
        region.forEach {
            val block = level.platformFirstPos.world.getBlockAt(it.blockX, it.blockY, it.blockZ)
            block.type = Material.GLASS
        }

        val lobby = Main.plugin.config.getLocation("lobby")!!

        Main.plugin.server.onlinePlayers.forEach {
            it.teleport(lobby)

            val leaderboard = PointsManager.displayLeaderboard(it)

            if (it.hasPermission("mccedropper.admin")) {
                it.playSound(it.location, Sound.ITEM_TRIDENT_THUNDER, 1000.0F, 2.0F)

                it.sendMessage("")
                it.sendMessage(">> ${ChatColor.GREEN}Completing players: ${ChatColor.GOLD}${level.completed.map { itt -> Main.plugin.server.getOfflinePlayer(itt).name }.joinToString(", ")} ${ChatColor.RESET}(${ChatColor.YELLOW}${level.completed.size} / ${Main.plugin.server.onlinePlayers.filter { itt -> !itt.hasPermission("mccedropper.admin") }.size}${ChatColor.RESET})")
                it.sendMessage(">> ${ChatColor.RED}Other players: ${ChatColor.DARK_AQUA}${
                    Main.plugin.server.onlinePlayers.filter { itt ->
                        !level.completed.contains(
                            itt.uniqueId
                        ) && !itt.hasPermission("mccedropper.admin")
                    }.joinToString(", ") { itt -> itt.name }
                }")

                return@forEach
            }

            it.sendMessage("")
            it.sendMessage(">> ${ChatColor.DARK_PURPLE}Current position: ${ChatColor.GREEN}${leaderboard.indexOfFirst { itt -> itt.first == it } + 1} / ${PointsManager.points.size}")
            it.sendMessage(">> ${ChatColor.GREEN}Total Points: ${ChatColor.GOLD}${PointsManager.points[it.uniqueId]?.points ?: 0} points")

            it.isCollidable = false
            it.isInvisible = false
            it.isInvulnerable = false
            it.health = 20.0

            if (level.completed.contains(it.uniqueId)) {
                it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1000.0F, 1.7F)
                it.sendTitle("${ChatColor.GREEN}Level complete!", "", 0, 30, 10)
            } else {
                it.playSound(it.location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1000.0F, 0.0F)
                it.playSound(it.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1000.0F, 0.0F)
                it.sendTitle("${ChatColor.RED}Level failed!", "${ChatColor.YELLOW}Keep trying, you can do it!", 0, 30, 10)
            }
        }

        level.completed.clear()
        level.currentTime = 0L

        PointsManager.save()
    }
}