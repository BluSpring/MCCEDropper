package xyz.bluspring.mccedropper.commands
/*
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import xyz.bluspring.mccedropper.level.LevelManager

object LevelSetRemainingCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        level.remaining = args[1] as Int

        LevelManager.save()

        sender.sendMessage("${ChatColor.GREEN}Successfully set remaining to ${ChatColor.YELLOW}${level.remaining}")
    }
}*/