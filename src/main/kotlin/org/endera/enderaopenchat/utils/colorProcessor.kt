package org.endera.enderaopenchat.utils

import org.bukkit.entity.Player
import org.endera.enderaopenchat.EnderaOpenChat

fun processColorPermissions(player: Player, message: String): String {
    val config = EnderaOpenChat.config.colorPermissions

    if (!config.enabled) {
        return stripAllColors(message)
    }

    val hasDefaultColor = player.hasPermission(config.defaultColorPermission)
    val hasHexColor = player.hasPermission(config.hexColorPermission)

    return when {
        hasDefaultColor && hasHexColor -> message
        hasDefaultColor -> stripHexColors(message)
        else -> stripAllColors(message)
    }
}

private fun stripHexColors(message: String): String {
    // Strip hex color tags like <#RRGGBB> or <color:#RRGGBB>
    return message
        .replace(Regex("<#[0-9a-fA-F]{6}>"), "")
        .replace(Regex("<color:#[0-9a-fA-F]{6}>"), "")
}

private fun stripAllColors(message: String): String {
    // Strip all MiniMessage color/formatting tags
    // This regex matches tags like <red>, <bold>, <#RRGGBB>, etc.
    return message.replace(Regex("</?([a-zA-Z_]+|#[0-9a-fA-F]{6}|color:#[0-9a-fA-F]{6})(:[^>]+)?>"), "")
}
