package xyz.bluspring.mccedropper.events

import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent
import xyz.bluspring.mccedropper.level.LevelManager
import xyz.bluspring.mccedropper.points.PointsManager

object PlayerOnJoinEvent : Listener {
    @EventHandler
    fun onPlayerJoin(ev: PlayerJoinEvent) {
        ev.player.isCollidable = false
        ev.player.isInvisible = false
        ev.player.isInvulnerable = false

        ev.player.health = 20.0

        if (ev.player.hasPermission("mccedropper.admin")) return
        PointsManager.createTrackedPlayer(ev.player)

        if (!LevelManager.levels.any { it.started }) return

        val level = LevelManager.levels.first { it.started }
        level.bossBar?.addPlayer(ev.player)

        if (level.completed.contains(ev.player.uniqueId)) {
            ev.player.health = 20.0
        } else {
            ev.player.teleport(level.spawn)
            ev.player.health = 0.1

            level.displayInfo(ev.player)
        }
    }

    @EventHandler
    fun onResourcePack(ev: PlayerResourcePackStatusEvent) {
        if (ev.status == PlayerResourcePackStatusEvent.Status.DECLINED) {
            ev.player.kick(Component.text("You must play with the resource pack!"))
        } else if (ev.status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            ev.player.kick(Component.text("Resource pack failed to download! Please attempt rejoining."))
        }
    }
}