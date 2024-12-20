package llc.redstone.redstonesmp.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import llc.redstone.redstonesmp.RedstoneSMP;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RedstoneSMPCommand {
    private long cooldown = 0; //Because its only one person, we can just use a long instead of a map
    public RedstoneSMPCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, env) -> {
            LiteralCommandNode<ServerCommandSource> chat = dispatcher.register(
                    literal("redstonesmp")
                            .then(literal("reload")
                                    .executes(ctx -> {
                                        if (!ctx.getSource().hasPermissionLevel(2)) {
                                            return Command.SINGLE_SUCCESS;
                                        }

                                        // Reload the config
                                        File configFile = new File("config/smp_config.json");
                                        if (configFile.exists()) {
                                            try {
                                                RedstoneSMP.config = new Gson().fromJson(Files.readString(configFile.toPath()), JsonObject.class);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(literal("give")
                                    .then(argument("player", StringArgumentType.string())
                                            .then(argument("item", StringArgumentType.string())
                                                    .executes(ctx -> {
                                                        if (!ctx.getSource().isExecutedByPlayer() || !ctx.getSource().getPlayer().getUuid().equals(UUID.fromString("aa940820-910b-46c9-9498-0d148a25ba28"))) {
                                                            return Command.SINGLE_SUCCESS;
                                                        }

                                                        if (System.currentTimeMillis() < cooldown) {
                                                            ctx.getSource().sendMessage(Text.of("Qwesty you are on cooldown"));
                                                            return Command.SINGLE_SUCCESS;
                                                        }

                                                        String player = StringArgumentType.getString(ctx, "player");
                                                        String itemId = StringArgumentType.getString(ctx, "item");

                                                        try {
                                                            Registries.ITEM.get(Identifier.of(itemId));
                                                        } catch (Exception e) {
                                                            ctx.getSource().sendMessage(Text.of("Qwesty that item doesn't exist"));
                                                            return Command.SINGLE_SUCCESS;
                                                        }

                                                        if (ctx.getSource().getServer().getPlayerManager().getPlayer(player) == null) {
                                                            ctx.getSource().sendMessage(Text.of("Qwesty use a player that is online :)"));
                                                            return Command.SINGLE_SUCCESS;
                                                        }

                                                        if (player.equalsIgnoreCase("Qwestii")) {
                                                            ctx.getSource().sendMessage(Text.of("Oh you want some building blocks aye? well lemme steal some gold from you"));
                                                            if (ctx.getSource().getPlayer().getInventory().contains(object -> object.getItem().equals(Registries.ITEM.get(Identifier.of("minecraft", "gold_ingot"))))) {
                                                                PlayerInventory inv = ctx.getSource().getPlayer().getInventory();
                                                                int slot = inv.getSlotWithStack(Registries.ITEM.get(Identifier.of("minecraft", "gold_ingot")).getDefaultStack());
                                                                ItemStack stack = inv.getStack(slot);
                                                                stack.setCount(stack.getCount() - (int) (Math.random() * 5) + 1);
                                                                inv.setStack(slot, stack);
                                                            } else {
                                                                ctx.getSource().sendMessage(Text.of("Qwesty you don't have any gold"));
                                                                return Command.SINGLE_SUCCESS;
                                                            }
                                                        }
                                                        Item item = Registries.ITEM.get(Identifier.of(itemId));

                                                        ItemStack itemStack = item.getDefaultStack();
                                                        // give the player a random amount of the item between 1 and 64
                                                        itemStack.setCount((int) (Math.random() * 64) + 1);
                                                        ctx.getSource().getServer().getPlayerManager().getPlayer(player).giveItemStack(itemStack);
                                                        cooldown = System.currentTimeMillis() + 1000 * 60 * 5; // 5 minutes
                                                        return Command.SINGLE_SUCCESS;
                                                    })
                                            )
                                    )
                            )
            );
        });
    }
}
