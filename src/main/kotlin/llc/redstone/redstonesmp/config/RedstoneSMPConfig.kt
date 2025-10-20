package llc.redstone.redstonesmp.config

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.File

class RedstoneSMPConfig {
    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().create()
        val INSTANCE = RedstoneSMPConfig()
    }

    var localChatRadius: Radius = Radius()
    var localChatPrefix: String = "§aLOCAL "
    var voiceChatPrefix: String = "§aVOICE "
    var globalChatPrefix: String = ""

    fun loadOrCreateConfig() {
        val configDir = FabricLoader.getInstance().configDir.resolve("redstonesmp").toFile()
        if (!configDir.exists()) {
            configDir.mkdirs()
        }
        val configFile = configDir.resolve("config.json")
        if (!configFile.exists()) {
            saveConfig(configFile)
        } else {
            loadConfig(configFile)
        }
    }

    private fun loadConfig(file: File) {
        val loadedConfig = GSON.fromJson(file.readText(), RedstoneSMPConfig::class.java)
        localChatRadius = loadedConfig.localChatRadius
        localChatPrefix = loadedConfig.localChatPrefix
        voiceChatPrefix = loadedConfig.voiceChatPrefix
        globalChatPrefix = loadedConfig.globalChatPrefix
    }

    private fun saveConfig(file: File) {
        file.writeText(GSON.toJson(this))
    }

    data class Radius(
        var x: Double = 128.0,
        var y: Double = 128.0,
        var z: Double = 128.0
    )
}