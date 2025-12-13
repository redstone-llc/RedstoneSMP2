package llc.redstone.redstonesmp.commands

import llc.redstone.redstonesmp.config.RedstoneSMPConfig
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.DefaultPermissions
import net.minecraft.server.command.CommandManager.literal


fun createRedstoneSMPCommand() {
    CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
        dispatcher.register(literal("redstonesmp")
            .requires { source -> source.permissions.hasPermission(DefaultPermissions.GAMEMASTERS) }
            .then(literal("reload")
                .executes { ctx ->
                    RedstoneSMPConfig.INSTANCE.loadOrCreateConfig()

                    1
                })
        )
    }
}