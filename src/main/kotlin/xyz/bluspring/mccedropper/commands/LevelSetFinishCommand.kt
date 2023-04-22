package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.level.LevelManager

object LevelSetFinishCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        val pos1 = args[1] as Location
        val pos2 = args[2] as Location

        level.finishPoints.add(listOf(pos1, pos2))

        LevelManager.save()

        sender.sendMessage("${ChatColor.GREEN}Successfully added finishing point!")
    }
}