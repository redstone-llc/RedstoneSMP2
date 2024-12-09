package llc.redstone.redstonesmp.mixin;

import io.icker.factions.core.InteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InteractionManager.class)
public class InteractionManagerMixin {
    @Inject(method = "onAttackEntity", at = @At("HEAD"), cancellable = true)
    private static void onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (entity instanceof PlayerEntity) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
