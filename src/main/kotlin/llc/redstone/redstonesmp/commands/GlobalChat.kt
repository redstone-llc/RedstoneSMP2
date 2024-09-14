package llc.redstone.redstonesmp.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import llc.redstone.redstonesmp.RedstoneSMP.Companion.playerChatMap
import llc.redstone.redstonesmp.commands.GlobalChat.Companion.execute
import llc.redstone.redstonesmp.utils.sendMessage
import llc.redstone.redstonesmp.utils.sendToConsole
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


fun createGlobalChatCommand() {
    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
        val global = dispatcher.register(literal("globalchat")
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
        dispatcher.register(literal("ac").redirect(global))
        dispatcher.register(literal("allchat").redirect(global))
    }
}

class GlobalChat {
    companion object {
        fun execute(context: ServerCommandSource, message: String?) {
            if (!context.isExecutedByPlayer) {
                context.sendMessage(Text.literal("This command can only be executed by a player"))
                return
            }
            val player = context.player?: return

            if (message == null ) {
                if (!playerChatMap.containsKey(player.uuid) || playerChatMap[player.uuid] == "global") {
                    player.sendMessage("§cCHAT §8|§r §cYou are already in global chat.")
                } else {
                    player.sendMessage("§cCHAT §8|§r §7You are now in global chat.")
                    playerChatMap[player.uuid] = "global"
                }
                return
            }

            val players = player.server.playerManager.playerList
            players.forEach { p ->
                sendMessage(player, p, message, "")
            }
            sendToConsole(player, message, "")
        }
    }
}