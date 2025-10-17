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
import llc.redstone.redstonesmp.commands.createGroupCommand
import llc.redstone.redstonesmp.commands.createLocalChatCommand
import llc.redstone.redstonesmp.commands.createVoiceChatCommand
import llc.redstone.redstonesmp.listeners.OnMessageSent
import llc.redstone.redstonesmp.listeners.PlayerListener
import llc.redstone.redstonesmp.schema.GroupSchema
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileReader
import java.util.*


class RedstoneSMP : ModInitializer, VoicechatPlugin {
    companion object {
        @JvmStatic val playerChatMap: HashMap<UUID, String> = HashMap()
        @JvmStatic val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        @JvmStatic lateinit var groupsFile: File
        @JvmStatic lateinit var groupData: HashMap<String, GroupSchema>
        @JvmStatic lateinit var API: VoicechatServerApi
    }
    override fun onInitialize() {
        createGlobalChatCommand()
        createLocalChatCommand()
        createGroupCommand()
        createVoiceChatCommand()

        OnMessageSent.startListening()
        PlayerListener.startListening()

        saveGroups()

        ServerLifecycleEvents.SERVER_STOPPING.register(ServerStopping { server ->
            saveGroups()
        })
    }

    private fun saveGroups() {
        groupsFile = File(FabricLoader.getInstance().configDir.toFile(),"redstonesmp/groups.json")
        if (!groupsFile.exists()) {
            groupsFile.parentFile.mkdirs()
            groupsFile.createNewFile()
            groupsFile.writeText("{}")
        }
        val json = FileReader(groupsFile).use {
            gson.fromJson(it, JsonObject::class.java)
        }
        groupData = HashMap()
        json.entrySet().forEach { (key, value) ->
            groupData[key] = GroupSchema.decode(value as JsonObject)
        }
    }

    override fun registerEvents(registration: EventRegistration) {
        registration.registerEvent<VoicechatServerStartedEvent>(
            VoicechatServerStartedEvent::class.java
        ) { event: VoicechatServerStartedEvent -> this.onServerStarted(event) }

        registration.registerEvent<JoinGroupEvent>(
            JoinGroupEvent::class.java
        ) { event: JoinGroupEvent ->
            this.onJoinGroup(event)
        }
    }

    fun onJoinGroup(event: JoinGroupEvent) {
        val groupName = event.group?.name ?: return
        val uuid = event.connection?.player?.uuid ?: return

        var groups = groupData

        if (!groups.containsKey(groupName)) {
            return
        }

        groups = groups.filter { it.value.players.contains(uuid.toString()) } as HashMap<String, GroupSchema>

        val isInGroup = groups.any { (key, group) ->
            group.name == groupName
        }

        if (!isInGroup) {
            event.cancel()
        }
    }

    fun onServerStarted(event: VoicechatServerStartedEvent) {
        API = event.voicechat
    }

    override fun getPluginId(): String {
        return "redstonesmp"
    }
}
