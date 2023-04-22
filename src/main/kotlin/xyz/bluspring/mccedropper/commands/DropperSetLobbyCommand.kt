package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.Main

object DropperSetLobbyCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val loc = args[0] as Location
        Main.plugin.config.set("lobby", loc)

        Main.plugin.saveConfig()

        sender.sendMessage("${ChatColor.GREEN}Successfully set lobby to ${ChatColor.YELLOW}${loc.blockX} ${loc.blockY} ${loc.blockZ}")
    }
}