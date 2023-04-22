package xyz.bluspring.mccedropper.events

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import xyz.bluspring.mccedropper.Main
import xyz.bluspring.mccedropper.level.LevelManager
import xyz.bluspring.mccedropper.points.PointsManager

object PlayerOnMovementEvent : Listener {
    @EventHandler
    fun onPlayerMove(ev: PlayerMoveEvent) {
        if (ev.isCancelled) return
        if (ev.player.hasPermission("mccedropper.admin")) return
        if (!LevelManager.levels.any { it.started }) return

        val level = LevelManager.levels.first { it.started }
        if (level.completed.contains(ev.player.uniqueId)) return

        // v1
        //if (!isInCuboid(ev.to, level.finishFirstPos, level.finishSecondPos)) return

        if (!level.finishPoints.any { isInCuboid(ev.to, it[0], it[1]) })
            return

        level.completed.add(ev.player.uniqueId)
        ev.player.playSound(ev.player.location, Sound.ENTITY_PLAYER_LEVELUP, 1000.0F, 1.0F)
        ev.player.sendTitle("", "${ChatColor.GREEN}You reached the end! Great job!", 2, 20, 10)

        ev.player.isInvisible = false
        ev.player.health = 20.0
        ev.player.isInvulnerable = true

        ev.player.teleport(Main.plugin.config.getLocation("lobby")!!)

        val trackedPlayer = PointsManager.createTrackedPlayer(ev.player)

        if (level.completed.size == 1) {
            Main.plugin.server.onlinePlayers.forEach {
                it.sendMessage(">>> ${ChatColor.GOLD}${ev.player.name} ${ChatColor.YELLOW}${ChatColor.BOLD}was the first one to complete the level!")
            }

            ev.player.sendMessage("${ChatColor.WHITE}[${ChatColor.GOLD}+2${ChatColor.WHITE}] ${ChatColor.BOLD}${ChatColor.WHITE}Congratulations, you completed ${ChatColor.GOLD}${level.title}${ChatColor.RESET} first!")
            trackedPlayer.points += 2
        } else {
            ev.player.sendMessage("${ChatColor.WHITE}[${ChatColor.GOLD}+1${ChatColor.WHITE}] ${ChatColor.BOLD}${ChatColor.WHITE}Congratulations, you completed ${ChatColor.GOLD}${level.title}!")
            trackedPlayer.points++
        }

        val bossBar = Main.plugin.server.getBossBar(NamespacedKey.fromString("level_${level.id}", Main.plugin)!!)
        bossBar?.setTitle("${ChatColor.GREEN}Level ${ChatColor.YELLOW}${level.id} ${ChatColor.BLACK}: ${ChatColor.RESET}${level.title}")

        Main.plugin.server.onlinePlayers.filter { it.hasPermission("mccedropper.admin") }.forEach {
            it.playSound(it.location, Sound.BLOCK_NOTE_BLOCK_BELL, 1000.0F, 1.0F)
            it.sendMessage("${ChatColor.YELLOW}${ev.player.name} ${ChatColor.GOLD}has completed the level!")
        }

        if (Main.plugin.server.onlinePlayers.none {
                !it.hasPermission("mccedropper.admin") && !level.completed.contains(
                    it.uniqueId
                )
        })
            LevelManager.stopLevel(level)
    }

    private fun isInCuboid(loc: Location, loc1: Location, loc2: Location): Boolean {
        val dim = arrayOf(loc1.blockX, loc2.blockX)

        dim.sort()

        if (loc.blockX > dim[1] || loc.blockX < dim[0])
            return false

        dim[0] = loc1.blockY
        dim[1] = loc2.blockY

        dim.sort()

        if (loc.blockY > dim[1] || loc.blockY < dim[0])
            return false

        dim[0] = loc1.blockZ
        dim[1] = loc2.blockZ

        dim.sort()

        if (loc.blockZ > dim[1] || loc.blockZ < dim[0])
            return false

        return true
    }
}