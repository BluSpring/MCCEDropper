package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.Main

object DropperLobbyCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val lobby = Main.plugin.config.getLocation("lobby")!!

        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}You ain't a player.")
            return
        }

        sender.teleport(lobby)
        sender.sendMessage("Sent you to the lobby.")
    }
}