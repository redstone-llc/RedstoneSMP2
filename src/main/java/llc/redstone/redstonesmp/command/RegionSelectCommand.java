package llc.redstone.redstonesmp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.registry.ModComponents;
import llc.redstone.redstonesmp.PlayerData;
import llc.redstone.redstonesmp.ServerRedirect;
import llc.redstone.redstonesmp.database.schema.OriginContinents;
import llc.redstone.redstonesmp.database.schema.OriginRegion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.lang.reflect.Field;

import static llc.redstone.redstonesmp.RedstoneSMP.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RegionSelectCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("regionselect")
                        .then(argument("continent", StringArgumentType.string())
                                .then(argument("region", StringArgumentType.string())
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                            if (!playerDataCollection.hasPlayerData(player.getUuid())) {
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            PlayerData data = playerDataCollection.getPlayerData(player.getUuid());

                                            if (data.selectedContinent || data.isInAdventureServer()) {
                                                player.sendMessage(Text.of("You have already selected a continent."), false);
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            String region = StringArgumentType.getString(ctx, "region");

                                            if (!originContinentCollection.hasOriginLocation(data.getOriginId())) {
                                                player.sendMessage(Text.of("Invalid continent."), false);
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            OriginContinents originContinent = originContinentCollection.getOriginLocation(data.getOriginId());

                                            OriginRegion originRegion = originContinent.getRegion(region);

                                            if (originRegion == null) {
                                                player.sendMessage(Text.of("Invalid region."), false);
                                                return Command.SINGLE_SUCCESS;
                                            }

                                            frozenPlayers.remove(player.getUuid());
                                            player.setInvulnerable(false);

                                            originRegion.teleportPlayer(player);

                                            PlayerOriginComponent component = (PlayerOriginComponent) ModComponents.ORIGIN.get(player);
                                            try {
                                                Field invulnerabilityTicksField = PlayerOriginComponent.class.getDeclaredField("invulnerabilityTicks");
                                                invulnerabilityTicksField.setAccessible(true);
                                                invulnerabilityTicksField.set(component, 0);
                                            } catch (NoSuchFieldException | IllegalAccessException ignored) {} //nah

                                            data.selectedContinent = true;
                                            playerDataCollection.updatePlayerData(data);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
        );
    }
}
