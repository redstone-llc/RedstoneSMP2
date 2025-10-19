package llc.redstone.redstonesmp

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import de.maxhenkel.voicechat.api.VoicechatPlugin
import de.maxhenkel.voicechat.api.VoicechatServerApi
import de.maxhenkel.voicechat.api.events.EventRegistration
import de.maxhenkel.voicechat.api.events.JoinGroupEvent
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent
import llc.redstone.redstonesmp.commands.createGlobalChatCommand
import llc.redstone.redstonesmp.commands.createLocalChatCommand
import llc.redstone.redstonesmp.commands.createVoiceChatCommand
import llc.redstone.redstonesmp.listeners.OnMessageSent
import net.fabricmc.api.ModInitializer
import java.util.*


class RedstoneSMP : ModInitializer, VoicechatPlugin {
    companion object {
        @JvmStatic val playerChatMap: HashMap<UUID, String> = HashMap()
        @JvmStatic lateinit var API: VoicechatServerApi
    }
    override fun onInitialize() {
        createGlobalChatCommand()
        createLocalChatCommand()
        createVoiceChatCommand()

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
