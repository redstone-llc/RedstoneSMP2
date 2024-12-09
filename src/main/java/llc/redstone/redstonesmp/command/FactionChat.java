package llc.redstone.redstonesmp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.icker.factions.api.persistents.Faction;
import io.icker.factions.api.persistents.User;
import io.icker.factions.util.Message;
import llc.redstone.redstonesmp.ServerRedirect;
import llc.redstone.redstonesmp.utils.MessageUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class FactionChat {
    public static HashMap<UUID, Boolean> factionChat = new HashMap<>();
    public FactionChat() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, env) -> {
            LiteralCommandNode<ServerCommandSource> lc = dispatcher.register(
                    literal("factionchat")
                            .then(argument("message", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        if (!ctx.getSource().isExecutedByPlayer()) {
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        sendToFactionUsers(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "message"));
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )

            );
            dispatcher.register(literal("fc").redirect(lc));
        });

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            if (factionChat.containsKey(sender.getUuid()) && factionChat.get(sender.getUuid())) {
                sendToFactionUsers(sender, message.getContent().getString());
                return false;
            }
            return true;
        });
    }

    public static void sendToFactionUsers(ServerPlayerEntity player, String message) {
        Faction faction = User.get(player.getUuid()).getFaction();

        String msg = MessageUtils.formatMessage(player, message, faction.getColor() + "[" + faction.getName() + "] ");
        player.getServer().getPlayerManager().getPlayerList().stream().filter(p -> faction.getUsers().contains(User.get(p.getUuid()))).forEach(p -> {
            p.sendMessage(Text.of(msg));
        });
    }
}
