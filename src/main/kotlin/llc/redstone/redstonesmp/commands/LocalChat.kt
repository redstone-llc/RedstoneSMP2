package llc.redstone.redstonesmp.commands

import llc.redstone.redstonesmp.commands.LocalChat.Companion.execute
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Box
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
import llc.redstone.redstonesmp.utils.SuggestedArgumentBuilder
import llc.redstone.redstonesmp.utils.sendMessage
import llc.redstone.redstonesmp.utils.sendToConsole
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


fun createLocalChatCommand() {
    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
        val local = dispatcher.register(literal("localchat")
            .then(argument("message", StringArgumentType.string())
                    .executes { context ->
                    val source = context.source
                    execute(source, StringArgumentType.getString(context, "message"))
                    1
                }
            )
            .executes { context ->
                val source = context.source
                execute(source, null)
                1
            }
        )
        dispatcher.register(literal("lc").redirect(local))
    }
}

class LocalChat {
    companion object {
        fun execute(context: ServerCommandSource, message: String?) {
            if (!context.isExecutedByPlayer) {
                context.sendMessage(Text.literal("This command can only be executed by a player"))
                return
            }
            val player = context.player?: return

            if (message == null ) {
                if (!playerChatMap.containsKey(player.uuid) || playerChatMap[player.uuid] == "local") {
                    player.sendMessage("§cCHAT §8|§r §cYou are already in local chat. You can use /globalchat to switch to global chat.")
                } else {
                    player.sendMessage("§cCHAT §8|§r §7You are now in local chat.")
                    playerChatMap[player.uuid] = "local"
                }
                return
            }

            //get all players in a 128 block radius
            val players = player.serverWorld.getEntitiesByClass(ServerPlayerEntity::class.java, Box(player.x - 128.0, player.y - 128.0, player.z - 128.0, player.x + 128.0, player.y + 128.0, player.z + 128.0)) { true }
            players.forEach { p ->
                sendMessage(player, p, message, "§aLOCAL ")
            }
            sendToConsole(player, message, "§aLOCAL ")
        }
    }
}