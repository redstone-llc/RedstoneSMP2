package llc.redstone.redstonesmp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import llc.redstone.redstonesmp.RedstoneSMP;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class UnFreezeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("unfreeze")
                        .requires(cs -> cs.hasPermissionLevel(2))
                        .then(argument("player", StringArgumentType.string())
                                .executes(ctx -> {
                                    String playerName = StringArgumentType.getString(ctx, "player");
                                    // Unfreeze player
                                    if (ctx.getSource().getServer().getPlayerManager().getPlayer(playerName) != null) {
                                        ServerPlayerEntity target = ctx.getSource().getServer().getPlayerManager().getPlayer(playerName);
                                        if (target == null) {
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        RedstoneSMP.frozenPlayers.remove(target.getUuid());
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
        );
    }
}
