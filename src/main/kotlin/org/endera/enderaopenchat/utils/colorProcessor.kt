package org.endera.enderaopenchat.utils

import org.bukkit.entity.Player
import org.endera.enderaopenchat.EnderaOpenChat

// MiniMessage named colors that require individual permissions
private val NAMED_COLORS = setOf(
    "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple",
    "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple",
    "yellow", "white"
)

// MiniMessage formatting tags that require individual permissions
private val FORMATTING_TAGS = setOf(
    "bold", "b", "italic", "i", "underlined", "u", "strikethrough", "st",
    "obfuscated", "obf"
)

// Tags that don't require permissions (structural/closing tags)
private val ALLOWED_TAGS = setOf(
    "reset", "r"
)

fun processColorPermissions(player: Player, message: String): String {
    val config = EnderaOpenChat.config.colorPermissions

    if (!config.enabled) {
        return stripAllTags(message)
    }

    val permissionCache = mutableMapOf<String, Boolean>()
    val hasHexPermission = player.hasPermission(config.hexColorPermission)

    // Regex to match all MiniMessage tags
    val tagRegex = Regex("</?([a-zA-Z_]+|#[0-9a-fA-F]{6}|color:#[0-9a-fA-F]{6})(:[^>]+)?>")

    return tagRegex.replace(message) { matchResult ->
        val fullTag = matchResult.value
        val tagContent = matchResult.groupValues[1].lowercase()

        // Handle closing tags - allow them if opening tag was allowed
        if (fullTag.startsWith("</")) {
            return@replace fullTag
        }

        // Always allow reset tags
        if (tagContent in ALLOWED_TAGS) {
            return@replace fullTag
        }

        // Handle hex colors
        if (tagContent.startsWith("#") || tagContent.startsWith("color:#")) {
            return@replace if (hasHexPermission) fullTag else ""
        }

        // Handle gradient and rainbow (special effects)
        if (tagContent == "gradient" || tagContent == "rainbow") {
            val permission = config.colorPermissionPrefix + tagContent
            val hasPermission = permissionCache.getOrPut(permission) {
                player.hasPermission(permission)
            }
            return@replace if (hasPermission) fullTag else ""
        }

        // Handle named colors and formatting
        if (tagContent in NAMED_COLORS || tagContent in FORMATTING_TAGS) {
            val permission = config.colorPermissionPrefix + tagContent
            val hasPermission = permissionCache.getOrPut(permission) {
                player.hasPermission(permission)
            }
            return@replace if (hasPermission) fullTag else ""
        }

        // Unknown tag - strip it for safety
        ""
    }
}

private fun stripAllTags(message: String): String {
    return message.replace(Regex("</?([a-zA-Z_]+|#[0-9a-fA-F]{6}|color:#[0-9a-fA-F]{6})(:[^>]+)?>"), "")
}
