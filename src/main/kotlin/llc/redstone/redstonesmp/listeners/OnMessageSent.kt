package llc.redstone.redstonesmp.listeners

import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
import llc.redstone.redstonesmp.commands.VoiceChat
import llc.redstone.redstonesmp.config.RedstoneSMPConfig
import llc.redstone.redstonesmp.utils.sendMessage
import llc.redstone.redstonesmp.utils.sendToConsole
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents.AllowChatMessage
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Box

class OnMessageSent {
    companion object {
        fun startListening() {
            ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(AllowChatMessage() { message, sender, params ->
                if (playerChatMap[sender.uuid] == "local") {
                    val players = sender.entityWorld.getEntitiesByClass(
                        ServerPlayerEntity::class.java,
                        Box(
                            sender.x - 128.0,
                            sender.y - 128.0,
                            sender.z - 128.0,
                            sender.x + 128.0,
                            sender.y + 128.0,
                            sender.z + 128.0
                        )
                    ) { true }
                    players.forEach { p ->
                        sendMessage(sender, p, message.signedContent, RedstoneSMPConfig.INSTANCE.localChatPrefix)
                    }
                    sendToConsole(sender, message.signedContent, RedstoneSMPConfig.INSTANCE.localChatPrefix)
                    return@AllowChatMessage false
                }

                if (!playerChatMap.containsKey(sender.uuid) || playerChatMap[sender.uuid] == "global") {
                    sender.entityWorld.server.playerManager.playerList.forEach { p ->
                        sendMessage(sender, p, message.signedContent, RedstoneSMPConfig.INSTANCE.globalChatPrefix)
                    }
                    sendToConsole(sender, message.signedContent, RedstoneSMPConfig.INSTANCE.globalChatPrefix)
                    return@AllowChatMessage false
                }

                if (!playerChatMap.containsKey(sender.uuid) || playerChatMap[sender.uuid] == "voice") {
                    VoiceChat.sendMessage(message.signedContent, sender)
                    return@AllowChatMessage false
                }

                true
            })
        }
    }
}