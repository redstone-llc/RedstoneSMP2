package llc.redstone.redstonesmp.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockRegistry {
    public static final Block DIAMOND_ANVIL = new DiamondAnvilBlock(Block.Settings.create().strength(5.0f, 1200));
    public static final Block ENCHANTED_DIAMOND_BLOCK = new Block(Block.Settings.create().strength(5.0f, 1200));
    public static void register() {
        // Register blocks here
        Registry.register(Registries.BLOCK, Identifier.of("redstone", "diamond_anvil"), DIAMOND_ANVIL);
        Registry.register(Registries.ITEM, Identifier.of("redstone", "diamond_anvil"), new BlockItem(DIAMOND_ANVIL, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("redstone", "enchanted_diamond_block"), ENCHANTED_DIAMOND_BLOCK);
        Registry.register(Registries.ITEM, Identifier.of("redstone", "enchanted_diamond_block"), new BlockItem(ENCHANTED_DIAMOND_BLOCK, new Item.Settings()));
    }
}
