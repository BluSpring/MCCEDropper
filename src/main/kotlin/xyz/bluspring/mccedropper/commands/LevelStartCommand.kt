package xyz.bluspring.mccedropper.commands

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitRunnable
import xyz.bluspring.mccedropper.Main
import xyz.bluspring.mccedropper.level.LevelManager

object LevelStartCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        if (LevelManager.levels.any { it.started }) {
            sender.sendMessage("${ChatColor.YELLOW}Level ${LevelManager.levels.first { it.started }.id}${ChatColor.RED} is still playing! Be sure to stop that level first.")
            return
        }

        if (level.started) {
            sender.sendMessage("${ChatColor.RED}Level has already started!")

            return
        }

        level.started = true

        val levelBar = Main.plugin.server.createBossBar(NamespacedKey.fromString("level_${level.id}", Main.plugin)!!, "${ChatColor.GREEN}Level ${ChatColor.YELLOW}${level.id} ${ChatColor.BLACK}: ${ChatColor.RESET}${level.title}", BarColor.BLUE, BarStyle.SOLID)
        Main.plugin.server.onlinePlayers.forEach {
            levelBar.addPlayer(it)
            it.health = 0.1

            if (!it.hasPermission("mccedropper.admin")) {
                it.isCollidable = false
            } else {
                val filtered = Main.plugin.server.onlinePlayers.filter{ itt -> !itt.hasPermission("mccedropper.admin") }

                it.sendMessage("${ChatColor.DARK_AQUA}${ChatColor.STRIKETHROUGH}Victims ${ChatColor.RESET}${ChatColor.RED}Participating players: ${ChatColor.GOLD}${
                    filtered.joinToString(
                        ", "
                    ) { itt -> itt.name }
                } ${ChatColor.BLUE}(${filtered.size})")
            }
        }

        level.bossBar = levelBar

        var time = 3

        val runnable = object : BukkitRunnable() {
            override fun run() {
                Main.plugin.server.onlinePlayers.forEach {
                    it.playSound(it.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1000.0F, if (time == 0) 1.0F else 0.5F)
                    it.sendTitle(timeToColour(time), "", 5, 10, 5)
                }

                if (time >= 0)
                    time--

                if (time == -1) {
                    this.cancel()
                    try {
                        val region = CuboidRegion(
                            BukkitAdapter.adapt(level.platformFirstPos.world),
                            BlockVector3.at(
                                level.platformFirstPos.blockX,
                                level.platformFirstPos.blockY,
                                level.platformFirstPos.blockZ
                            ),
                            BlockVector3.at(
                                level.platformSecondPos.blockX,
                                level.platformSecondPos.blockY,
                                level.platformSecondPos.blockZ
                            )
                        )

                        region.forEach {
                            val block = level.platformFirstPos.world.getBlockAt(it.blockX, it.blockY, it.blockZ)
                            block.breakNaturally()
                        }

                        sender.sendMessage("${ChatColor.GREEN}Started!")
                    } catch (e: Exception) {
                        sender.sendMessage("${ChatColor.RED}Failed to destroy platform! Error message: ${ChatColor.YELLOW}${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }

        runnable.runTaskTimer(Main.plugin, 0L, 20L)
    }

    private fun timeToColour(time: Int): String = if (time == 3) "${ChatColor.GREEN}3" else if (time == 2) "${ChatColor.YELLOW}2" else if (time == 1) "${ChatColor.RED}1" else "${ChatColor.DARK_GREEN}GO!"
}