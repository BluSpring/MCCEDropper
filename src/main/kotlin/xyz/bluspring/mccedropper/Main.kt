package xyz.bluspring.mccedropper

import com.sk89q.worldedit.bukkit.WorldEditPlugin
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.StringTooltip
import dev.jorel.commandapi.arguments.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import xyz.bluspring.mccedropper.commands.*
import xyz.bluspring.mccedropper.events.PlayerOnJoinEvent
import xyz.bluspring.mccedropper.events.PlayerOnMovementEvent
import xyz.bluspring.mccedropper.events.PlayerOnRespawnEvent
import xyz.bluspring.mccedropper.level.LevelManager
import xyz.bluspring.mccedropper.points.PointsExpansion
import xyz.bluspring.mccedropper.points.PointsManager
import java.io.File
import java.lang.Long.parseLong
import java.util.concurrent.TimeUnit

class Main : JavaPlugin() {
    override fun onLoad() {
        val self = this

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(StringArgument("action").overrideSuggestionsT { _ ->
                listOf(
                    StringTooltip.of("create", "Creates the level from the given ID/level number."),
                    StringTooltip.of("delete", "Deletes the level."),
                    StringTooltip.of("info", "Gives info about the current level (also previews how it looks upon joining)."),
                    StringTooltip.of("change", "Changes the level ID/number."),
                    StringTooltip.of("tp", "Teleports all players (that are not admin) to the level position."),
                    StringTooltip.of("start", "Starts the level (removes the platform)"),
                    StringTooltip.of("stop", "Stops the level (replaces the platform)"),
                    StringTooltip.of("pause", "Stops the level, but the difference is that no points will be given."),
                    //StringTooltip.of("setremaining", "Sets how many players are required to be remaining before the level ends."),
                    StringTooltip.of("settime", "Sets the time remaining."),
                    StringTooltip.of("setplatform", "Sets where the platform is to remove (optionally uses WorldEdit positioning if you don't specify the coordinates)"),
                    StringTooltip.of("finish", "Sets where the finishing area is. (optionally uses WorldEdit positioning if you don't specify the coordinates)"),
                    StringTooltip.of("setinfo", "Sets title/description info about this level."),
                    StringTooltip.of("setspawn", "Sets the spawn position of the level."),
                    StringTooltip.of("builders", "Adds/Removes who worked on this level.")
                ).toTypedArray()
            })

            executes(LevelCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })
            withArguments(LiteralArgument("create"))

            executes(LevelCreateCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("delete"))
            executes(LevelDeleteCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("builders"))
            withArguments(StringArgument("add/remove").overrideSuggestionsT { _, _ ->
                listOf(
                    StringTooltip.of("add", "Adds builder to the list of credits on the info."),
                    StringTooltip.of("remove", "Removes builder from the list of credits on the info.")
                ).toTypedArray()
            })

            executes(LevelCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("builders"))
            withArguments(LiteralArgument("add"))
            withArguments(StringArgument("builder").overrideSuggestions { _, _ ->
                self.server.onlinePlayers.map { it.name }.toTypedArray()
            })

            executes(LevelBuildersAddCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("builders"))
            withArguments(LiteralArgument("remove"))
            withArguments(StringArgument("builder").overrideSuggestions { _, _ ->
                self.server.onlinePlayers.map { it.name }.toTypedArray()
            })

            executes(LevelBuildersRemoveCommand)
        }.register()

        /*CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })
            withArguments(LiteralArgument("setremaining"))
            withArguments(IntegerArgument("remainingPlayerCount"))

            executes(LevelSetRemainingCommand)
        }.register()*/

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })
            withArguments(LiteralArgument("settime"))
            withArguments(StringArgument("time"))

            executes(LevelSetTimeCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })
            withArguments(LiteralArgument("info"))

            executes(LevelInfoCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })
            withArguments(LiteralArgument("pause"))

            executes(LevelStopNoPointsCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("change"))
            withArguments(IntegerArgument("newId"))

            executes(LevelChangeCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("start"))

            executes(LevelStartCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("stop"))

            executes(LevelStopCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("setplatform"))

            withArguments(LocationArgument("pos1"))
            withArguments(LocationArgument("pos2"))

            executes(LevelSetPlatformCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("setplatform"))

            executes(LevelSetPlatformWorldEditCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("finish"))

            withArguments(StringArgument("add/remove/list").overrideSuggestionsT { _ ->
                listOf(
                    StringTooltip.of("add", "Adds a point (you don't need to add coordinates if you're using WorldEdit)"),
                    StringTooltip.of("remove", "Removes a point."),
                    StringTooltip.of("list", "List the points.")
                ).toTypedArray()
            })

            /*withArguments(LocationArgument("pos1"))
            withArguments(LocationArgument("pos2"))*/

            executes(LevelCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("finish"))
            withArguments(LiteralArgument("add"))

            executes(LevelSetFinishWorldEditCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("finish"))
            withArguments(LiteralArgument("add"))
            withArguments(LocationArgument("pos1"))
            withArguments(LocationArgument("pos2"))

            executes(LevelSetFinishCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("finish"))
            withArguments(LiteralArgument("remove"))
            withArguments(IntegerArgument("finishPoint").overrideSuggestions { _, it ->
                if (LevelManager.levels.any { lvl -> lvl.id == it[0] as Int })
                    return@overrideSuggestions arrayOf<String>()

                LevelManager.levels[it[0] as Int].finishPoints.indices.map { itt -> itt.toString() }.toTypedArray()
            })

            executes(LevelFinishRemoveCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("finish"))
            withArguments(LiteralArgument("list"))

            executes(LevelFinishListCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("setinfo"))

            withArguments(StringArgument("infoLine").overrideSuggestionsT { _, _ ->
                listOf(
                    StringTooltip.of("title", "Sets the title of the level."),
                    StringTooltip.of("description", "Sets the description of the level."),
                    StringTooltip.of("chat_title", "Sets the title of the level for the info sent in chat."),
                    StringTooltip.of(
                        "chat_description",
                        "Sets the description of the level for the info sent in chat."
                    )
                ).toTypedArray()
            })

            executes(LevelCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("setinfo"))

            withArguments(LiteralArgument("title"))
            withArguments(GreedyStringArgument("titleText"))

            executes(LevelSetInfoTitleCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("setinfo"))

            withArguments(LiteralArgument("description"))
            withArguments(GreedyStringArgument("descriptionText"))

            executes(LevelSetInfoDescriptionCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("setinfo"))

            withArguments(LiteralArgument("chat_title"))
            withArguments(GreedyStringArgument("chatTitleText"))

            executes(LevelSetInfoChatTitleCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("setinfo"))

            withArguments(LiteralArgument("chat_description"))
            withArguments(GreedyStringArgument("chatDescriptionText"))

            executes(LevelSetInfoChatDescriptionCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("setspawn"))

            withArguments(LocationArgument("spawnLocation"))

            executes(LevelSetSpawnCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("setspawn"))

            executes(LevelSetSpawnFromCurrentLocationCommand)
        }.register()

        CommandAPICommand("level").apply {
            withAliases("lvl", "lv", "dropper")
            withPermission("mccedropper.admin")

            withArguments(IntegerArgument("id").overrideSuggestionsT { _, _ ->
                LevelManager.levels.map {
                    StringTooltip.of(
                        it.id.toString(),
                        it.description
                    )
                }.toTypedArray()
            })

            withArguments(LiteralArgument("tp"))

            executes(LevelTeleportCommand)
        }.register()

        CommandAPICommand("setlobby").apply {
            withPermission("mccedropper.admin")

            withArguments(LocationArgument("lobby_position"))

            executes(DropperSetLobbyCommand)
        }.register()

        CommandAPICommand("setlobby").apply {
            withPermission("mccedropper.admin")

            executes(DropperSetLobbyFromCurrentLocationCommand)
        }.register()

        CommandAPICommand("lobby").apply {
            withPermission("mccedropper.admin")

            executes(DropperLobbyCommand)
        }.register()

        CommandAPICommand("resetpoints").apply {
            withPermission("mccedropper.admin")

            executes(ResetPointsCommand)
        }.register()

        CommandAPICommand("points").apply {
            executes(PointsCommand)
        }.register()

        CommandAPICommand("leaderboard").apply {
            executes(LeaderboardCommand)
        }.register()
    }

    override fun onEnable() {
        plugin = this
        this.saveDefaultConfig()

        if (!this.config.contains("configVersion") || this.config.getInt("configVersion") != 2) {
            this.saveConfig()
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            PointsExpansion().register()
        }

        this.saveResource("config.json", false)
        this.saveResource("points.json", false)

        this.server.pluginManager.getPlugin("WorldEdit") as WorldEditPlugin?
            ?: throw NullPointerException("WorldEdit is not installed!")

        LevelManager.load()
        PointsManager.load()

        this.server.pluginManager.registerEvents(PlayerOnJoinEvent, this)
        this.server.pluginManager.registerEvents(PlayerOnMovementEvent, this)
        this.server.pluginManager.registerEvents(PlayerOnRespawnEvent, this)

        this.server.scheduler.runTaskTimer(this, Runnable {
            if (LevelManager.levels.any { it.started })
                plugin.server.onlinePlayers.filter { !it.hasPermission("mccedropper.admin") && it.fallDistance <= 0.5F }.forEach {
                    it.health = 0.1
                }

            LevelManager.levels.forEach {
                if (!it.started) return@forEach

                it.currentTime++
                val time = it.totalTime - it.currentTime

                it.bossBar?.setTitle("${ChatColor.GREEN}Level ${ChatColor.YELLOW}${it.id} ${ChatColor.BLACK}: ${ChatColor.RESET}${it.title} ${ChatColor.BLACK}: ${if (time > 30) ChatColor.GREEN else if (time > 15) ChatColor.YELLOW else ChatColor.RED}${secondsToFormatted(time)}")

                if (it.currentTime >= it.totalTime) {
                    LevelManager.stopLevel(it)
                }
            }
        }, 0L, 20L) // 1s

        this.server.scheduler.runTaskTimer(this, Runnable {
            // Make sure there's no admins in the point manager.
            if (PointsManager.points.any { it.value.player.isOp || (it.value.player.isOnline && it.value.player is Player && (it.value.player as Player).hasPermission("mccedropper.admin")) }) {
                PointsManager.points.filter { it.value.player.isOp || (it.value.player.isOnline && it.value.player is Player && (it.value.player as Player).hasPermission("mccedropper.admin")) }.forEach {
                    PointsManager.points.remove(it.key)
                }
            }
        }, 0L, 200L) // 10s
    }

    override fun onDisable() {
        plugin.server.onlinePlayers.forEach {
            it.isCollidable = true
            it.isInvisible = false
        }
    }

    companion object {
        lateinit var plugin: JavaPlugin

        fun secondsToTicks(seconds: Long): Long = seconds * 20L

        fun formattedToSeconds(formatted: String): Long {
            var time = 0L

            if (formatted.endsWith("m")) {
                time += parseLong(formatted.replace("m", "")) * 60L

                return time
            }

            if (formatted.endsWith("s") && formatted.contains("m")) {
                val f = formatted.replace("m", " ").replace("s", "")

                time += parseLong(f.split(" ")[1])
                time += parseLong(f.split(" ")[0]) * 60L

                return time
            }

            if (formatted.endsWith("s")) {
                time += parseLong(formatted.replace("s", ""))

                return time
            }

            val split = formatted.split(":")
            time += parseLong(split[split.size - 1])

            if (split.size > 1) {
                time += parseLong(split[split.size - 2]) * 60L
            }

            if (split.size > 2) {
                time += parseLong(split[split.size - 3]) * 60L
            }

            return time
        }

        fun secondsToFormatted(seconds: Long): String {
            val hour = TimeUnit.SECONDS.toHours(seconds)
            val minute = TimeUnit.SECONDS.toMinutes(seconds)
            val second = seconds % 60L

            return "${if (hour > 0L) "${if (hour <= 9) "0" else ""}$hour:" else ""}${if (minute <= 9) "0" else ""}$minute:${if (second <= 9) "0" else ""}$second"
        }
    }
}