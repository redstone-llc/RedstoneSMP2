package llc.redstone.redstonesmp.command;

import com.mojang.brigadier.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import llc.redstone.redstonesmp.RedstoneSMP;
import llc.redstone.redstonesmp.database.schema.OriginContinents;
import llc.redstone.redstonesmp.database.schema.OriginRegion;
import llc.redstone.redstonesmp.database.schema.PortalLocation;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class CreateRegionCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("createregion")
                        .requires(cs -> cs.hasPermissionLevel(2))
                        .then(argument("originId", StringArgumentType.string())
                                .then(argument("continent", StringArgumentType.string())
                                        .then(argument("name", StringArgumentType.string())
                                                .then(argument("pos", BlockPosArgumentType.blockPos())
                                                        .then(argument("radius", IntegerArgumentType.integer())
                                                                .executes(ctx -> {
                                                                    return createRegion(ctx.getSource(), StringArgumentType.getString(ctx, "originId"), StringArgumentType.getString(ctx, "continent"), StringArgumentType.getString(ctx, "name"), BlockPosArgumentType.getBlockPos(ctx, "pos"), IntegerArgumentType.getInteger(ctx, "radius"));
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private static int createRegion(ServerCommandSource source, String originId, String continent, String name, BlockPos a, int radius) {
        int x = a.getX();
        int y = a.getY();
        int z = a.getZ();

        source.sendMessage(Text.of("§aSuccessfully created region with name " + name + " at " + a + " and radius " + radius + " for origin " + originId + " in continent " + continent));

        if (RedstoneSMP.originContinentCollection.hasOriginLocation(originId)) {
            OriginContinents originContinents = RedstoneSMP.originContinentCollection.getOriginLocation(originId);
            originContinents.continent = continent;
            originContinents.addRegion(new OriginRegion(name, x, y, z, radius));
            RedstoneSMP.originContinentCollection.updateOriginLocation(originContinents);
        } else {
            OriginContinents originContinents = new OriginContinents();
            originContinents.originId = originId;
            originContinents.continent = continent;
            originContinents.addRegion(new OriginRegion(name, x, y, z, radius));
            RedstoneSMP.originContinentCollection.insertOriginLocation(originContinents);
        }
        return Command.SINGLE_SUCCESS;
    }
}
