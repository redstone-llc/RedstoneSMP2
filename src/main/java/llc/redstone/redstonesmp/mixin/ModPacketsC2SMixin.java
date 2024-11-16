package llc.redstone.redstonesmp.mixin;

import io.github.apace100.origins.networking.ModPacketsC2S;
import io.github.apace100.origins.networking.ModPacketsS2C;
import io.github.apace100.origins.networking.packet.s2c.ConfirmOriginS2CPacket;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import llc.redstone.redstonesmp.RedstoneSMP;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModPacketsC2S.class, remap = false)
public class ModPacketsC2SMixin {
    @Inject(method = "confirmOrigin", at = @At("HEAD"))
    private static void confirmOrigin(ServerPlayerEntity player, OriginLayer layer, Origin origin, CallbackInfo ci) {
        System.out.println(RedstoneSMP.Companion.getOriginsLocations());
        System.out.println(origin.getId().toString());
        System.out.println(RedstoneSMP.Companion.getOriginsLocations().containsKey(origin.getId().toString()));
        if (RedstoneSMP.Companion.getOriginsLocations().containsKey(origin.getId().toString())) {
            Vec3d coords = RedstoneSMP.Companion.getOriginsLocations().get(origin.getId().toString());
            player.sendMessage(Text.of("§6Teleporting you to your origins location"), false);
            player.teleport(player.getServer().getOverworld(), coords.getX(), coords.getY(), coords.getZ(), player.getYaw(), player.getPitch());
        }
    }
}
