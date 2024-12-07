package llc.redstone.redstonesmp.utils;

import llc.redstone.redstonesmp.RedstoneSMP;
import llc.redstone.redstonesmp.database.schema.OriginContinents;
import llc.redstone.redstonesmp.database.schema.OriginRegion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;

public class ContinentMessageUtils {
    //Map link https://map.redstone.llc/#minecraft_overworld;flat;<x>,255,<z>;3
    public static void sendContinentMessage(ServerPlayerEntity player, String originId) {
        OriginContinents cont = RedstoneSMP.originContinentCollection.getOriginLocation(originId);
        if (cont == null) return;

        player.sendMessage(Text.of("§bWelcome to §aEarth Origins"));
        player.sendMessage(Text.of("§7Now that you have selected your origin, you can now select where you want to start your journey."));
        player.sendMessage(Text.of(""));
        MutableText text = MutableText.of(PlainTextContent.of(""));
        text.append(Text.of("§6§m---------------------------------------\n"));
        text.append(Text.of("§bSelect a region in " + cont.continent + "\n"));
        for (OriginRegion region : cont.regions) {
            MutableText regionText = MutableText.of(PlainTextContent.of("§8 - §e" + region.name + " §8(" + region.x + ", " + region.z + ")"));

            MutableText mapLink = MutableText.of(PlainTextContent.of(" §e[MAP]"));
            Style style = mapLink.getStyle()
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("§eClick to open map for " + region.name)))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://map.redstone.llc/#minecraft_overworld;flat;" + region.x + ",255," + region.z + ";3"));
            mapLink.setStyle(style);
            regionText.append(mapLink);

            MutableText selectLink = MutableText.of(PlainTextContent.of("§b [SELECT]\n"));
            style = selectLink.getStyle()
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("§bClick to select " + region.name)))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/regionselect " + cont.continent + " " + region.name));
            selectLink.setStyle(style);
            regionText.append(selectLink);

            text.append(regionText);
        }
        text.append(Text.of("§cTO BE REPLACED WITH A CUSTOM GUI\n"));
        text.append(Text.of("§6§m---------------------------------------"));

        player.sendMessage(text, false);
    }
}
