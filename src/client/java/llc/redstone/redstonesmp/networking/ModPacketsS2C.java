package llc.redstone.redstonesmp.networking;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsClient;
import io.github.apace100.origins.badge.BadgeManager;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.networking.packet.VersionHandshakePacket;
import io.github.apace100.origins.networking.packet.s2c.*;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayerManager;
import io.github.apace100.origins.origin.OriginManager;
import io.github.apace100.origins.registry.ModComponents;
import io.github.apace100.origins.screen.ChooseOriginScreen;
import io.github.apace100.origins.screen.WaitForNextLayerScreen;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import llc.redstone.redstonesmp.networking.s2c.OpenChooseContinentScreenS2CPacket;
import llc.redstone.redstonesmp.screen.ChooseContinentScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class ModPacketsS2C {

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientPlayConnectionEvents.INIT.register(((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(OpenChooseContinentScreenS2CPacket.PACKET_ID, ModPacketsS2C::openContinentScreen);
        }));

    }

    @Environment(EnvType.CLIENT)
    private static void openContinentScreen(OpenChooseContinentScreenS2CPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient.getInstance().setScreen(new ChooseContinentScreen(ChooseContinentScreen.getContinents(), 0, packet.showBackground()));
    }
}
