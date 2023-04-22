package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.level.LevelManager

object LevelChangeCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        if (args.size == 1) {
            sender.sendMessage("${ChatColor.RED}You're missing the new ID!")
            return
        }

        level.id = args[1] as Int
        LevelManager.save()
        sender.sendMessage("${ChatColor.GREEN}The level ID has successfully been changed to ${ChatColor.YELLOW}${level.id}${ChatColor.GREEN}!")
    }
}