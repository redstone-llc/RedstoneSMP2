package llc.redstone.redstonesmp.blocks;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class RedstoneBlockTags {
    public static final TagKey<Block> DIAMOND_ANVIL = of("diamond_anvil");
    private RedstoneBlockTags() {
    }

    private static TagKey<Block> of(String id) {
        return TagKey.of(RegistryKeys.BLOCK, Identifier.of("redstone", id));
    }
}
