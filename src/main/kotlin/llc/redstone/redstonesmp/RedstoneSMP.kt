package llc.redstone.redstonesmp

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.netty.handler.codec.json.JsonObjectDecoder
import llc.redstone.redstonesmp.networking.ModPacketsC2S
import net.fabricmc.api.ModInitializer
import net.minecraft.util.math.Vec3d
import java.io.File


class RedstoneSMP : ModInitializer {
    companion object {
        var originsLocations: Map<String, Vec3d> = mapOf()
    }
    override fun onInitialize() {
        //load originlocations.json
        val originsLocationsFile = File("config/originlocations.json")
        if (originsLocationsFile.exists()) {
            val originsLocationsJson = Gson().fromJson(originsLocationsFile.readText(), JsonObject::class.java)
            originsLocations = originsLocationsJson.entrySet().associate { it.key to Vec3d(it.value.asJsonObject["x"].asDouble, it.value.asJsonObject["y"].asDouble, it.value.asJsonObject["z"].asDouble) }
        }

        ModPacketsC2S.register()
    }
}
