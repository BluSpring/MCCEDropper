package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.level.LevelManager

object LevelCreateCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        if (LevelManager.levels.any { it.id == args[0] }) {
            sender.sendMessage("${ChatColor.RED}Level already exists!")
            return
        }

        LevelManager.createLevel(args[0] as Int, if (sender is Player) sender else null)
        sender.sendMessage("${ChatColor.GREEN}Successfully created level!")

        LevelManager.save()
    }
}