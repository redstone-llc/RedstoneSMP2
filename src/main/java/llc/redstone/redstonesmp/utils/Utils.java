package llc.redstone.redstonesmp.utils;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.function.Function;

public class Utils {
    public static class Serializer<T, E extends NbtElement> {
        private final Function<T, E> serializer;
        private final Function<E, T> deserializer;

        public Serializer(Function<T, E> serializer, Function<E, T> deserializer) {
            this.serializer = serializer;
            this.deserializer = deserializer;
        }

        @SuppressWarnings("unchecked")
        public NbtElement serialize(Object value) {
            return serializer.apply((T) value);
        }

        @SuppressWarnings("unchecked")
        public T deserialize(NbtElement value) {
            return deserializer.apply((E) value);
        }
    }

    public static Serializer<SimpleInventory, NbtList> createInventorySerializer(int size, MinecraftServer server) {
        return new Serializer<SimpleInventory, NbtList>(val -> {
            NbtList nbtList = new NbtList();

            for (int i = 0; i < val.size(); ++i) {
                ItemStack itemStack = val.getStack(i);
                if (!itemStack.isEmpty()) {
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte) i);
                    nbtCompound.put("Data", itemStack.encode(server.getRegistryManager()));
                    nbtList.add(nbtCompound);
                }
            }

            return nbtList;
        }, el -> {
            SimpleInventory inventory = new SimpleInventory(size);

            for (int i = 0; i < size; ++i) {
                inventory.setStack(i, ItemStack.EMPTY);
            }

            for (int i = 0; i < el.size(); ++i) {
                NbtCompound nbtCompound = el.getCompound(i);
                int slot = nbtCompound.getByte("Slot") & 255;
                if (slot >= 0 && slot < size) {
                    inventory.setStack(slot, ItemStack.fromNbt(server.getRegistryManager(), nbtCompound).get());
                }

                HashMap<Integer, Integer> slotMap = new HashMap<>();
                slotMap.put(150, 49);
                slotMap.put(100, 50);
                slotMap.put(101, 51);
                slotMap.put(102, 52);
                slotMap.put(103, 53);

                for (int key : slotMap.keySet()) {
                    if (slot == key) {
                        inventory.setStack(slotMap.get(key), ItemStack.fromNbt(server.getRegistryManager(), nbtCompound).get());
                    }
                }
            }

            return inventory;
        });
    }
}
