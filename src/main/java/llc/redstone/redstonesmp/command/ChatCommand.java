package llc.redstone.redstonesmp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static llc.redstone.redstonesmp.command.FactionChat.factionChat;
import static llc.redstone.redstonesmp.command.LocalChat.localChat;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ChatCommand {
    public ChatCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, env) -> {
            LiteralCommandNode<ServerCommandSource> chat = dispatcher.register(
                    literal("chat")
                            .then(argument("type", StringArgumentType.string())
                                    .suggests((ctx, builder) -> {
                                        builder.suggest("local");
                                        builder.suggest("global");
                                        builder.suggest("faction");
                                        return builder.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        if (!ctx.getSource().isExecutedByPlayer()) {
                                            return Command.SINGLE_SUCCESS;
                                        }

                                        String type = StringArgumentType.getString(ctx, "type");
                                        if (type.equals("local")) {
                                            factionChat.remove(ctx.getSource().getPlayer().getUuid());
                                            localChat.put(ctx.getSource().getPlayer().getUuid(), !localChat.getOrDefault(ctx.getSource().getPlayer().getUuid(), false));
                                        } else if (type.equals("global")) {
                                            localChat.remove(ctx.getSource().getPlayer().getUuid());
                                            factionChat.remove(ctx.getSource().getPlayer().getUuid());
                                        } else if (type.equals("faction")) {
                                            localChat.remove(ctx.getSource().getPlayer().getUuid());
                                            factionChat.put(ctx.getSource().getPlayer().getUuid(), !factionChat.getOrDefault(ctx.getSource().getPlayer().getUuid(), false));
                                        }

                                        ctx.getSource().sendMessage(Text.of("Chat type set to " + type.toLowerCase().substring(0, 1).toUpperCase() + type.toLowerCase().substring(1)));

                                        return Command.SINGLE_SUCCESS;
                                    })
                            ));
        });
    }
}
