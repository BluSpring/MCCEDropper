package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.points.PointsManager

object ResetPointsCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        PointsManager.points.forEach {
            PointsManager.points[it.key]!!.points = 0
        }

        PointsManager.save()

        sender.sendMessage("${ChatColor.GOLD}Points >> ${ChatColor.GREEN}Successfully reset all points.")
    }
}