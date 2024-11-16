package llc.redstone.redstonesmp.networking;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.networking.packet.VersionHandshakePacket;
import io.github.apace100.origins.networking.packet.c2s.ChooseOriginC2SPacket;
import io.github.apace100.origins.networking.packet.c2s.ChooseRandomOriginC2SPacket;
import io.github.apace100.origins.networking.packet.s2c.ConfirmOriginS2CPacket;
import io.github.apace100.origins.networking.task.VersionHandshakeTask;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayerManager;
import io.github.apace100.origins.origin.OriginManager;
import io.github.apace100.origins.registry.ModComponents;
import joptsimple.internal.Strings;
import llc.redstone.redstonesmp.networking.c2s.ChooseContinentC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

public class ModPacketsC2S {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ChooseContinentC2SPacket.PACKET_ID, ModPacketsC2S::onChooseContinent);
    }

    private static void onChooseContinent(ChooseContinentC2SPacket packet, ServerPlayNetworking.Context context) {

        ServerPlayerEntity player = context.player();

        OriginComponent component = ModComponents.ORIGIN.get(player);

        component.selectingOrigin(false);
        component.sync();

        player.sendMessage(Text.of("You chose continent " + packet.continentId()), false);
    }

}
