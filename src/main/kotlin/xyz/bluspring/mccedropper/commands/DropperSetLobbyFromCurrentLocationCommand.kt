package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.Main

object DropperSetLobbyFromCurrentLocationCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val loc = (sender as Player).location
        Main.plugin.config.set("lobby", loc)

        Main.plugin.saveConfig()

        sender.sendMessage("${ChatColor.GREEN}Successfully set lobby to ${ChatColor.YELLOW}${loc.blockX} ${loc.blockY} ${loc.blockZ}")
    }
}