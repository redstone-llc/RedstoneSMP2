package llc.redstone.redstonesmp.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.PlainTextContent;

public class MessageUtils {

    // Prefix must end with a space
    public static String formatMessage(ServerPlayerEntity sender, String message, String prefix) {
        if (sender == null) {
            return "[Server] " + message;
        }
        LuckPerms api = LuckPermsProvider.get();
        String userGroup = api.getUserManager().getUser(sender.getUuid()).getPrimaryGroup();
        MutableText text;
        text = MutableText.of(PlainTextContent.of(prefix + "§8●§r "))
                .append(displayName(sender))
                .append(Text.of(" §8»§r §f" + message));

        return text.getString();
    }

    public static Text displayName(ServerPlayerEntity player) {
        LuckPerms api = LuckPermsProvider.get();
        var data = api.getUserManager().getUser(player.getUuid()).getCachedData().getMetaData();
        if (data == null) {
            return player.getDisplayName();
        }
        String prefix = data.getPrefix() != null ? data.getPrefix().replace("&", "§") : "";
        String suffix = data.getSuffix() != null ? data.getSuffix().replace("&", "§") : "";
        return MutableText.of(PlainTextContent.of(prefix))
                .append(player.getDisplayName())
                .append(Text.of(suffix));
    }
}