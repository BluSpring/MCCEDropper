package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.Main
import xyz.bluspring.mccedropper.level.LevelManager
import xyz.bluspring.mccedropper.points.PointsManager

object LevelStopNoPointsCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }

        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        if (!level.started) {
            sender.sendMessage("${ChatColor.RED}Level has not started yet!")
            return
        }

        PointsManager.points.forEach {
            if (LevelManager.firstFinisher == Main.plugin.server.getOfflinePlayer(it.key)) {
                PointsManager.points[it.key]!!.points -= 2
            } else if (level.completed.contains(it.key)) {
                PointsManager.points[it.key]!!.points -= 1
            }
        }

        LevelManager.stopLevel(level)

        sender.sendMessage("${ChatColor.GREEN}Stopped the level!")
    }
}