package llc.redstone.redstonesmp.item;

import llc.redstone.redstonesmp.RedstoneSMP;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public abstract class DeadSimpleBagsItems {
    public static final BagItem TINY_BAG = new BagItem(1, new Item.Settings());
    public static final BagItem SMALL_BAG = new BagItem(2, new Item.Settings());
    public static final BagItem MEDIUM_BAG = new BagItem(3, new Item.Settings());
    public static final BagItem LARGE_BAG = new BagItem(4, new Item.Settings());
    public static final BagItem HUGE_BAG = new BagItem(5, new Item.Settings());
    public static final BagItem MASSIVE_BAG = new BagItem(6, new Item.Settings());

    public static void register() {
        System.out.println("Registering redstonepacks items");

        register("tiny_bag", TINY_BAG);
        register("small_bag", SMALL_BAG);
        register("medium_bag", MEDIUM_BAG);
        register("large_bag", LARGE_BAG);
        register("huge_bag", HUGE_BAG);
        register("massive_bag", MASSIVE_BAG);
    }

    private static void register(String name, Item item) {
        Registry.register(Registries.ITEM, Identifier.of("redstonepacks", name), item);
    }
}
