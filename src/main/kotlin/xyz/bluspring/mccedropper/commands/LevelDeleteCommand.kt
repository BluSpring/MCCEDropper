package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.level.LevelManager

object LevelDeleteCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist! Technically this means it was already deleted???")
            return
        }

        LevelManager.levels.remove(level)
        sender.sendMessage("${ChatColor.GREEN}Successfully deleted the level!")

        LevelManager.save()
    }
}