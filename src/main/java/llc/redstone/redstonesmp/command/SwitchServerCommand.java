package llc.redstone.redstonesmp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import llc.redstone.redstonesmp.PlayerData;
import llc.redstone.redstonesmp.RedstoneSMP;
import llc.redstone.redstonesmp.ServerRedirect;
import llc.redstone.redstonesmp.database.schema.PortalLocation;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static llc.redstone.redstonesmp.RedstoneSMP.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class SwitchServerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("switchserver")
                        .requires(cs -> cs.hasPermissionLevel(2))
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            MinecraftServer server = player.getServer();

                            if (playerDataCollection.hasPlayerData(player.getUuid())) {
                                PlayerData playerDataE = playerDataCollection.getPlayerData(player.getUuid());
                                playerDataE.setInAdventureServer(!playerDataE.isInAdventureServer());
                                playerDataCollection.updatePlayerData(playerDataE);
                                updatePlayerData(player, playerDataE);

                                if (playerDataE.isInAdventureServer() && server.getOverworld().getSeed() == 27594263L) {
                                    serverSwitch.set(true);
                                    ServerRedirect.sendTo(player, "adventure.redstone.llc");
                                } else if (!playerDataE.isInAdventureServer() && server.getOverworld().getSeed() != 27594263L) {
                                    serverSwitch.set(true);
                                    ServerRedirect.sendTo(player, "smp.redstone.llc");
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}
