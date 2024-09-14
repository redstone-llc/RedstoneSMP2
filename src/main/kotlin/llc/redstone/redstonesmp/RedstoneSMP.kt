package llc.redstone.redstonesmp

import llc.redstone.redstonesmp.commands.createGlobalChatCommand
import llc.redstone.redstonesmp.commands.createLocalChatCommand
import net.fabricmc.api.ModInitializer
import java.util.*
import kotlin.collections.HashMap

class RedstoneSMP : ModInitializer {
    companion object {
        @JvmStatic val playerChatMap: HashMap<UUID, String> = HashMap()
    }
    override fun onInitialize() {
        createGlobalChatCommand()
        createLocalChatCommand()
    }
}
