package llc.redstone.redstonesmp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import llc.redstone.redstonesmp.inventory.BackupInventory;
import llc.redstone.redstonesmp.inventory.BagItemInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ViewBackupCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("viewbackup")
                        .then(argument("uuid", StringArgumentType.string())
                                .then(argument("backup", StringArgumentType.string())
                                        .then(argument("type", StringArgumentType.string())
                                                .executes((ctx) -> {
                                                    if (!ctx.getSource().hasPermissionLevel(2)) {
                                                        return 0;
                                                    }

                                                    if (!ctx.getSource().isExecutedByPlayer()) {
                                                        return 0;
                                                    }

                                                    String uuid = ctx.getArgument("uuid", String.class);
                                                    String backup = ctx.getArgument("backup", String.class);
                                                    String type = ctx.getArgument("type", String.class);

                                                    viewBackup(ctx.getSource().getPlayer(), uuid, backup, type);
                                                    return 0;
                                                })
                                        )
                                )
                        )

        );
    }

    private static void viewBackup(ServerPlayerEntity sender, String uuid, String backup, String type) {
        File file = new File("./backups/" + uuid + "/" + backup + ".dat");
        if (!file.exists()) {
            sender.sendMessage(Text.of("§cBackup does not exist!"), false);
            return;
        }

        try {
            NbtCompound compound = NbtIo.readCompressed(Path.of(file.getPath()), NbtSizeTracker.ofUnlimitedBytes());

            if (type.equals("inventory")) {
                NbtList inventory = compound.getList("Inventory", 10);
                sender.openHandledScreen(new BackupInventory(inventory, 6, sender.getServer()));
            } else if (type.equals("enderchest")) {
                NbtList inventory = compound.getList("EnderItems", 10);
                sender.openHandledScreen(new BackupInventory(inventory, 3, sender.getServer()));
            } else {
                sender.sendMessage(Text.of("§cInvalid type!"), false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to get backup for " + uuid);
        }
    }
}
