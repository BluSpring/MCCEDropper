package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object LevelCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        sender.sendMessage("${ChatColor.RED}Incorrect usage! ${ChatColor.YELLOW}/level <id> ${ChatColor.RED}(hover over the suggestions. The suggestions may be duplicated, unfortunately.)")
    }
}