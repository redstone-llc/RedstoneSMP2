package llc.redstone.redstonesmp

import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.VoicechatServerApi
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent
import llc.redstone.redstonesmp.commands.createGlobalChatCommand
import llc.redstone.redstonesmp.commands.createLocalChatCommand
import llc.redstone.redstonesmp.commands.createRedstoneSMPCommand
import llc.redstone.redstonesmp.commands.createVoiceChatCommand
import llc.redstone.redstonesmp.config.RedstoneSMPConfig
import llc.redstone.redstonesmp.listeners.OnMessageSent
import net.fabricmc.api.ModInitializer
import java.util.*


class RedstoneSMP : ModInitializer, VoicechatPlugin {
    companion object {
        @JvmStatic val playerChatMap: HashMap<UUID, String> = HashMap()
        @JvmStatic lateinit var API: VoicechatServerApi
    }

    override fun onInitialize() {
        RedstoneSMPConfig.INSTANCE.loadOrCreateConfig()

        createGlobalChatCommand()
        createLocalChatCommand()
        createVoiceChatCommand()
        createRedstoneSMPCommand()

        OnMessageSent.startListening()
    }

    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent<VoicechatServerStartedEvent>(
            VoicechatServerStartedEvent::class.java
        ) { event: VoicechatServerStartedEvent -> this.onServerStarted(event) }
    }

    fun onServerStarted(event: VoicechatServerStartedEvent) {
        API = event.voicechat
    }

    override fun getPluginId(): String {
        return "redstonesmp"
    }
}
