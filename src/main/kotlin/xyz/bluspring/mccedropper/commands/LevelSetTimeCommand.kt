package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.Main
import xyz.bluspring.mccedropper.level.LevelManager

object LevelSetTimeCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }

        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        val time = Main.formattedToSeconds(args[1] as String)

        level.totalTime = time

        sender.sendMessage("${ChatColor.GREEN}Successfully set the time to: ${ChatColor.AQUA}${Main.secondsToFormatted(time)}")

        LevelManager.save()
    }
}