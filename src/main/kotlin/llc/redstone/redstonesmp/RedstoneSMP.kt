package llc.redstone.redstonesmp

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import llc.redstone.redstonesmp.commands.createGlobalChatCommand
import llc.redstone.redstonesmp.commands.createGroupCommand
import llc.redstone.redstonesmp.commands.createLocalChatCommand
import llc.redstone.redstonesmp.listeners.OnMessageSent
import llc.redstone.redstonesmp.schema.GroupSchema
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileReader
import java.util.*
import kotlin.collections.HashMap

class RedstoneSMP : ModInitializer {
    companion object {
        @JvmStatic val playerChatMap: HashMap<UUID, String> = HashMap()
        @JvmStatic val gson: Gson = GsonBuilder().setPrettyPrinting().create()
        @JvmStatic lateinit var groupsFile: File
        @JvmStatic lateinit var groupData: HashMap<String, GroupSchema>
    }
    override fun onInitialize() {
        createGlobalChatCommand()
        createLocalChatCommand()
        createGroupCommand()

        OnMessageSent.startListening()

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
}
