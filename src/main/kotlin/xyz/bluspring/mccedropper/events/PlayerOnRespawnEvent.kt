package xyz.bluspring.mccedropper.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import xyz.bluspring.mccedropper.Main
import xyz.bluspring.mccedropper.level.LevelManager

object PlayerOnRespawnEvent : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerRespawn(ev: PlayerRespawnEvent) {
        if (ev.player.hasPermission("mccedropper.admin")) return
        if (!LevelManager.levels.any { it.started }) return

        val level = LevelManager.levels.first { it.started }
        if (level.completed.contains(ev.player.uniqueId)) return

        ev.respawnLocation = level.spawn
        ev.player.health = 0.1
    }

    @EventHandler
    fun onPlayerHealthUpdate(ev: EntityRegainHealthEvent) {
        if (ev.entity !is Player) return
        if (!LevelManager.levels.any { it.started }) return
        if (ev.entity.hasPermission("mccedropper.admin")) return

        ev.isCancelled = true
    }

    @EventHandler
    fun onPlayerDeath(ev: PlayerDeathEvent) {
        if (!LevelManager.levels.any { it.started }) return
        ev.isCancelled = true

        if (LevelManager.levels.any { it.completed.contains(ev.entity.uniqueId) }) {
            ev.entity.health = 20.0
            ev.entity.isInvulnerable = true
            ev.entity.teleport(Main.plugin.config.getLocation("lobby")!!)
        } else {
            ev.entity.health = 0.1
            ev.entity.fallDistance = 0.0F
            ev.entity.teleport(LevelManager.levels.first { it.started }.spawn)
        }


    }
}