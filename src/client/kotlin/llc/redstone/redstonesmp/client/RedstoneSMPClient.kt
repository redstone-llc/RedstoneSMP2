package llc.redstone.redstonesmp.client

import llc.redstone.redstonesmp.networking.ModPacketsS2C
import net.fabricmc.api.ClientModInitializer

class RedstoneSMPClient : ClientModInitializer {

    override fun onInitializeClient() {
        ModPacketsS2C.register()
    }
}
