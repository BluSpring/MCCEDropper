package xyz.bluspring.mccedropper.points

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import xyz.bluspring.mccedropper.Main

class PointsExpansion : PlaceholderExpansion() {
    override fun getIdentifier(): String = "mccedropper"

    override fun getAuthor(): String = Main.plugin.description.authors.toString()

    override fun getVersion(): String = Main.plugin.description.version

    override fun onPlaceholderRequest(player: Player?, identifier: String): String? {
        if (player == null) return ""

        if (identifier == "points") {
            if (player.hasPermission("mccedropper.admin"))
                return "Yes"

            val trackedPlayer = PointsManager.createTrackedPlayer(player)

            return trackedPlayer.points.toString()
        }

        return null
    }
}