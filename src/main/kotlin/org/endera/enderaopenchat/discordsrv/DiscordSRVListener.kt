package org.endera.enderaopenchat.discordsrv

import github.scarsz.discordsrv.api.Subscribe
import github.scarsz.discordsrv.api.events.DiscordReadyEvent
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent
import github.scarsz.discordsrv.util.DiscordUtil
import github.scarsz.discordsrv.util.MessageUtil
import org.bukkit.plugin.Plugin
import org.endera.enderaopenchat.EnderaOpenChat

@Suppress("unused")
class DiscordSRVListener(private val plugin: Plugin) {

    @Subscribe
    fun discordReadyEvent(event: DiscordReadyEvent) {
        DiscordUtil.getJda().addEventListener(JDAListener(plugin))
    }

    @Subscribe
    fun onGameChatPreProcess(event: GameChatMessagePreProcessEvent) {
        val cfg = EnderaOpenChat.config

        val legacy = MessageUtil.toLegacy(event.messageComponent)
        val plain = MessageUtil.strip(legacy)

        val matched = cfg.channels
            .filter { it.sendToDiscord && it.prefix.isNotEmpty() }
            .filter { plain.startsWith(it.prefix) }
            .maxByOrNull { it.prefix.length }

        if (matched != null) {
            val msg = plain.substring(matched.prefix.length).trimStart()
            if (msg.isBlank()) {
                event.isCancelled = true
                return
            }

            event.channel = matched.name
            event.messageComponent = MessageUtil.toComponent(msg, true)
            return
        }

        val defaultChannel = cfg.channels.firstOrNull { it.prefix.isEmpty() }
        if (defaultChannel == null || !defaultChannel.sendToDiscord) {
            event.isCancelled = true
        } else {
            event.channel = defaultChannel.name
        }
    }
}
