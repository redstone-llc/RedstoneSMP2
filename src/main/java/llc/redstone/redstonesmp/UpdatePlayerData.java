package llc.redstone.redstonesmp;

import com.google.gson.JsonObject;
import llc.redstone.redstonesmp.database.PlayerDataCollection;
import llc.redstone.redstonesmp.database.PortalLocationCollection;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class UpdatePlayerData {
    public static void updatePlayerData(String[] args) {
        RedstoneSMP.config = new JsonObject();
        RedstoneSMP.config.addProperty("mongo-uri", "mongodb://AdminEnder:RedstoneNish12!@mongo.redstone.llc:27017/?tls=true&authSource=admin");

        String uuid = "1734481890670";
        PlayerDataCollection playerDataCollection = new PlayerDataCollection();

        PlayerData playerData = playerDataCollection.getPlayerData(UUID.fromString("47f3d4cd-b7ce-4e17-89aa-4b75837f34ff"));
        File file = new File("./playerdata/" + uuid + ".dat");
        if (!file.exists()) {
            file.getParentFile().mkdir();
            System.out.println("File does not exist");
            return;
        }
        try {
            NbtCompound compound = NbtIo.readCompressed(Path.of(file.getPath()), NbtSizeTracker.ofUnlimitedBytes());
            playerData.setPlayerNBT(compound.toString());
            playerDataCollection.updatePlayerData(playerData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }
}
