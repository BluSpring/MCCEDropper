package xyz.bluspring.mccedropper.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.level.LevelManager

object LevelFinishListCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        sender.sendMessage("${ChatColor.GOLD}--- Finishing points for ${ChatColor.GREEN}Level ${ChatColor.YELLOW}${level.id} ${ChatColor.GOLD}---")
        sender.sendMessage("")

        level.finishPoints.forEach {
            sender.sendMessage("${ChatColor.AQUA}${level.finishPoints.indexOf(it)}. ${ChatColor.BLUE}${it[0].blockX} ${it[0].blockY} ${it[0].blockZ} ${ChatColor.GOLD}- ${ChatColor.DARK_AQUA}${it[1].blockX}${it[1].blockY}${it[1].blockZ}")
        }
    }
}