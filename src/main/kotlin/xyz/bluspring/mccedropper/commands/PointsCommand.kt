package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.points.PointsManager

object PointsCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}You are not a player!")
            return
        }

        val trackedPlayer = PointsManager.createTrackedPlayer(sender)

        sender.sendMessage("${ChatColor.GOLD}Points >> ${ChatColor.WHITE}You currently have ${ChatColor.GOLD}${trackedPlayer.points} ${ChatColor.WHITE}points.")
    }
}