package llc.redstone.redstonesmp.commands

import com.mojang.brigadier.arguments.StringArgumentType
import llc.redstone.redstonesmp.commands.GlobalChat.Companion.execute
import llc.redstone.redstonesmp.config.RedstoneSMPConfig
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal


fun createRedstoneSMPCommand() {
    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
        dispatcher.register(literal("redstonesmp")
            .requires { source -> source.hasPermissionLevel(2) }
            .then(literal("reload")
                .executes { ctx ->
                    RedstoneSMPConfig.INSTANCE.loadOrCreateConfig()

                    1
                })
        )
    }
}