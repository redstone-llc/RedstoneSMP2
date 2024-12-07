package llc.redstone.redstonesmp.mixin;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.networking.ModPacketsC2S;
import io.github.apace100.origins.networking.ModPacketsS2C;
import io.github.apace100.origins.networking.packet.s2c.ConfirmOriginS2CPacket;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.registry.ModComponents;
import llc.redstone.redstonesmp.PlayerData;
import llc.redstone.redstonesmp.RedstoneSMP;
import llc.redstone.redstonesmp.utils.ContinentMessageUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

import static llc.redstone.redstonesmp.RedstoneSMP.frozenPlayers;
import static llc.redstone.redstonesmp.RedstoneSMP.playerDataCollection;


@Mixin(value = ModPacketsC2S.class, remap = false)
public class ModPacketsC2SMixin {
    @Inject(method = "confirmOrigin", at = @At("HEAD"))
    private static void confirmOrigin(ServerPlayerEntity player, OriginLayer layer, Origin origin, CallbackInfo ci) {
        PlayerOriginComponent component = (PlayerOriginComponent) ModComponents.ORIGIN.get(player);
        try {
            Field invulnerabilityTicksField = PlayerOriginComponent.class.getDeclaredField("invulnerabilityTicks");
            invulnerabilityTicksField.setAccessible(true);
            invulnerabilityTicksField.set(component, 60);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {} //nah

        frozenPlayers.put(player.getUuid(), true);
        player.setInvulnerable(true);
        PlayerData playerData = playerDataCollection.getPlayerData(player.getUuid());
        playerData.setOriginId(origin.getId().toString());
        playerData.setLayerId(layer.getId().toString());
        playerData.selectedContinent = false;
        playerDataCollection.updatePlayerData(playerData);

        ContinentMessageUtils.sendContinentMessage(player, origin.getId().toString());
    }
}
