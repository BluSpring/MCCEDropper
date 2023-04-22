package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.points.PointsManager
import kotlin.math.min

object LeaderboardCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        if (sender is Player && !sender.hasPermission("mccedropper.admin")) {
            CommandAPI.fail("You're not an admin!")

            return
        }

        if (PointsManager.points.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}No one seems to have collected points yet!")
            return
        }

        PointsManager.displayLeaderboard(sender)
    }
}