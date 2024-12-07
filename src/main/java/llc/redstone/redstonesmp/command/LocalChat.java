package llc.redstone.redstonesmp.command;

import com.mojang.brigadier.Command;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mongodb.Block;
import llc.redstone.redstonesmp.RedstoneSMP;
import llc.redstone.redstonesmp.database.schema.OriginContinents;
import llc.redstone.redstonesmp.database.schema.OriginRegion;
import llc.redstone.redstonesmp.database.schema.PortalLocation;
import llc.redstone.redstonesmp.utils.MessageUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class LocalChat {
    HashMap<UUID, Boolean> localChat = new HashMap<>();
    public LocalChat() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, env) -> {
            LiteralCommandNode<ServerCommandSource> lc = dispatcher.register(
                    literal("localchat")
                            .executes(ctx -> {
                                if (!ctx.getSource().isExecutedByPlayer()) {
                                    return Command.SINGLE_SUCCESS;
                                }
                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                if (localChat.containsKey(player.getUuid())) {
                                    localChat.put(player.getUuid(), !localChat.get(player.getUuid()));
                                } else {
                                    localChat.put(player.getUuid(), true);
                                }
                                if (localChat.get(player.getUuid())) {
                                    player.sendMessage(Text.of("§aYou are now talking in Local Chat (Radius: 25)"), false);
                                } else {
                                    player.sendMessage(Text.of("§aYou are now talking in Global Chat"), false);
                                }
                                return Command.SINGLE_SUCCESS;
                            })
                            .then(argument("message", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        if (!ctx.getSource().isExecutedByPlayer()) {
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        sendMessageToNearbyPlayers(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "message"));
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )

            );
            dispatcher.register(literal("lc").redirect(lc));
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            if (localChat.containsKey(sender.getUuid()) && localChat.get(sender.getUuid())) {
                sendMessageToNearbyPlayers(sender, message.getContent().getString());
                return false;
            }
            return true;
        });
    }

    public static void sendMessageToNearbyPlayers(ServerPlayerEntity player, String message) {
        String msg = MessageUtils.formatMessage(player, message, "§a[LOCAL] ");

        int radius = 25;
        BlockPos pos = player.getBlockPos();
        Box box = Box.enclosing(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius));

        player.getServerWorld().getEntitiesByType(EntityType.PLAYER, box, LivingEntity::isAlive).forEach((p) -> {
            p.sendMessage(Text.of(msg), false);
        });
    }
}
