package llc.redstone.redstonesmp.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public abstract class DeadSimpleBagsComponents {
    public static final ComponentType<Boolean> OPEN = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("redstonepacks", "open"),
            ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
    );

    public static final ComponentType<PickupMode> PICKUP_MODE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("redstonepacks", "pickup_mode"),
            ComponentType.<PickupMode>builder().codec(PickupMode.CODEC).build()
    );
}
