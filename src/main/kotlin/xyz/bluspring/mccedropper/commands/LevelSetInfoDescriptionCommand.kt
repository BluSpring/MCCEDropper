package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.level.LevelManager

object LevelSetInfoDescriptionCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        level.description = ChatColor.translateAlternateColorCodes('&', args[1] as String)
        sender.sendMessage("${ChatColor.GREEN}Description has successfully been changed to ${ChatColor.YELLOW}\"${level.description}\"")

        LevelManager.save()
    }
}