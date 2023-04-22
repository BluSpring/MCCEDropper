package xyz.bluspring.mccedropper.commands

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.bukkit.WorldEditPlugin
import com.sk89q.worldedit.regions.CuboidRegion
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.Main
import xyz.bluspring.mccedropper.level.LevelManager

object LevelSetPlatformWorldEditCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        val level = LevelManager.levels.find { it.id == args[0] }
        if (level == null) {
            sender.sendMessage("${ChatColor.RED}Level does not exist!")
            return
        }

        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}You're not a player!")
            return
        }

        try {
            val worldEditPlugin = Main.plugin.server.pluginManager.getPlugin("WorldEdit") as WorldEditPlugin?
            if (worldEditPlugin == null) {
                sender.sendMessage("${ChatColor.RED}WorldEdit is not installed!")
                return
            }

            val selection = worldEditPlugin.getSession(sender).getSelection(BukkitAdapter.adapt(sender.world))

            if (selection == null) {
                sender.sendMessage("${ChatColor.RED}Please make a selection using WorldEdit!")
                return
            }

            if (selection !is CuboidRegion) {
                sender.sendMessage("${ChatColor.RED}Your selection is not a cuboid region!")
                return
            }

            level.platformFirstPos = Location(BukkitAdapter.adapt(selection.world), selection.minimumPoint.blockX.toDouble(), selection.minimumPoint.blockY.toDouble(), selection.minimumPoint.blockZ.toDouble())
            level.platformSecondPos = Location(BukkitAdapter.adapt(selection.world), selection.maximumPoint.blockX.toDouble(), selection.maximumPoint.blockY.toDouble(), selection.maximumPoint.blockZ.toDouble())

            LevelManager.save()

            sender.sendMessage("${ChatColor.GREEN}Successfully set platform!")
        } catch (_: IncompleteRegionException) {
            sender.sendMessage("${ChatColor.RED}Please complete your selection in WorldEdit!")
        }
    }
}