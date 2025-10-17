package llc.redstone.redstonesmp.commands

import com.mojang.brigadier.arguments.StringArgumentType
import llc.redstone.redstonesmp.RedstoneSMP
import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
import llc.redstone.redstonesmp.utils.sendMessage
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import kotlin.collections.set

fun createVoiceChatCommand() {
    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
        dispatcher.register(
            literal("vchat")
                .then(
                    argument("message", StringArgumentType.greedyString())
                        .executes { ctx ->
                            val message = StringArgumentType.getString(ctx, "message")

                            val p = ctx.source.player ?: return@executes 1

                            VoiceChat.sendMessage(message, p)

                            1
                        }
                )
                .executes { ctx ->
                    val p = ctx.source.player ?: return@executes 1
                    playerChatMap[p.uuid] = "voice"
                    p.sendMessage("§cCHAT §8|§r §7You are now in voice chat.")
                    1
                })
    }
}

object VoiceChat {
    fun sendMessage(message: String, player: ServerPlayerEntity) {
        val group = RedstoneSMP.API.getConnectionOf(player.uuid)?.group ?: run {
            player.sendMessage("§cVOICE CHAT §8|§r §cYou are not in a voice chat group.")
            return
        }

        val members = player.entityWorld.server.playerManager.playerList.filter {
            RedstoneSMP.API.getConnectionOf(it.uuid)?.group == group
        }

        members.forEach { member ->
            sendMessage(player, member, message, "§aVOICE ")
        }
    }
}
