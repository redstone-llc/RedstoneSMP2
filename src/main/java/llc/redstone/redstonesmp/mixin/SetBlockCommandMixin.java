package llc.redstone.redstonesmp.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import llc.redstone.redstonesmp.factions.FactionManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(SetBlockCommand.class)
public class SetBlockCommandMixin {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.setblock.failed"));

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void execute(ServerCommandSource source, BlockPos pos, BlockStateArgument block, SetBlockCommand.Mode mode, Predicate<CachedBlockPosition> condition, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
        ServerWorld serverWorld = source.getWorld();
        if (condition != null && !condition.test(new CachedBlockPosition(serverWorld, pos, true))) {
            throw FAILED_EXCEPTION.create();
        } else {
            boolean bl;

            if (FabricLoader.getInstance().isModLoaded("factions")) {
                if (source.getPlayer() != null && FactionManager.isPosInFaction(pos, source.getPlayer())) {
                    throw FAILED_EXCEPTION.create();
                }
            }

            if (mode == SetBlockCommand.Mode.DESTROY) {
                serverWorld.breakBlock(pos, true);
                bl = !block.getBlockState().isAir() || !serverWorld.getBlockState(pos).isAir();
            } else {
                BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
                Clearable.clear(blockEntity);
                bl = true;
            }

            if (bl && !block.setBlockState(serverWorld, pos, 2)) {
                throw FAILED_EXCEPTION.create();
            } else {
                serverWorld.updateNeighbors(pos, block.getBlockState().getBlock());
                source.sendFeedback(() -> {
                    return Text.translatable("commands.setblock.success", new Object[]{pos.getX(), pos.getY(), pos.getZ()});
                }, true);
                cir.setReturnValue(1);
            }
        }
    }
}
