package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.Main
import xyz.bluspring.mccedropper.level.LevelManager

object LevelTeleportCommand : CommandExecutor {
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

        Main.plugin.server.onlinePlayers.forEach {
            level.displayInfo(it)
        }

        Main.plugin.server.onlinePlayers.filter { !it.hasPermission("mccedropper.admin") }.forEach {
            it.teleport(level.spawn)
        }

        sender.sendMessage("${ChatColor.GREEN}Teleported all players to level!")
    }
}