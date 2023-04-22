package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.level.LevelManager

object LevelSetSpawnCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        level.spawn = args[1] as Location

        LevelManager.save()

        sender.sendMessage("${ChatColor.GREEN}Successfully set spawn to ${ChatColor.YELLOW}${level.spawn.blockX} ${level.spawn.blockY} ${level.spawn.blockZ}")
    }
}