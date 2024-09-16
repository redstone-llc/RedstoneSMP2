package llc.redstone.redstonesmp.listeners

import llc.redstone.redstonesmp.RedstoneSMP.Companion.groupData
import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
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
                    val players = sender.serverWorld.getEntitiesByClass(
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
                        sendMessage(sender, p, message.signedContent, "§aLOCAL ")
                    }
                    sendToConsole(sender, message.signedContent, "§aLOCAL ")
                    return@AllowChatMessage false
                }

                if (!playerChatMap.containsKey(sender.uuid) || playerChatMap[sender.uuid] == "global") {
                    sender.server.playerManager.playerList.forEach { p ->
                        sendMessage(sender, p, message.signedContent, "")
                    }
                    sendToConsole(sender, message.signedContent, "")
                    return@AllowChatMessage false
                }

                val group = playerChatMap[sender.uuid]
                val data = groupData[group] ?: return@AllowChatMessage true
                data.players.forEach { member ->
                    val player = sender.server.playerManager.getPlayer(member) ?: return@forEach
                    sendMessage(sender, player, message.signedContent, "§a${data.name.uppercase()} ")
                }
                sendToConsole(sender, message.signedContent, "§a${data.name.uppercase()} ")
                return@AllowChatMessage false
            })
        }
    }
}