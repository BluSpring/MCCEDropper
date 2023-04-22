package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.Main
import xyz.bluspring.mccedropper.level.LevelManager

object LevelBuildersAddCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        if (args.size == 1) {
            sender.sendMessage("${ChatColor.RED}You're missing the player!")
            return
        }

        val player = if (args[1] is Player) args[1] as OfflinePlayer else Main.plugin.server.getOfflinePlayerIfCached(args[1] as String)
        if (player == null) {
            sender.sendMessage("${ChatColor.RED}The player does not exist!")
            return
        }

        if (level.builders.contains(player.uniqueId)) {
            sender.sendMessage("${ChatColor.RED}This player is already a builder!")
            return
        }

        level.builders.add(player.uniqueId)
        sender.sendMessage("${ChatColor.GOLD}${player.name} ${ChatColor.GREEN}has been added to the builders list.")
    }
}