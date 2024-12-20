package llc.redstone.redstonesmp;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import llc.redstone.redstonesmp.database.PlayerDataCollection;
import llc.redstone.redstonesmp.database.PortalLocationCollection;
import llc.redstone.redstonesmp.database.schema.PortalLocation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;

public class MovePlayersToClosestPortal {
    public static void main(String[] args) {
        RedstoneSMP.config = new JsonObject();
        RedstoneSMP.config.addProperty("mongo-uri", "<mongo uri>");

        PlayerDataCollection playerDataCollection = new PlayerDataCollection();
        PortalLocationCollection portalLocationCollection = new PortalLocationCollection();

        playerDataCollection.all().forEach(playerData -> {
            if (!playerData.getOriginId().equalsIgnoreCase("origins:enderian")) return;
            playerData.setOriginId("rsextras:enderian_new");
            System.out.println("Updated " + playerData.getUuid());
            playerDataCollection.updatePlayerData(playerData);
        });

//        playerDataCollection.all().stream().filter(PlayerData::isInAdventureServer).forEach(playerData -> {
//            if (playerData.getUuid().equalsIgnoreCase("ad80d7cf81154e2ab15de5cc0bf6a9a2") || playerData.getUuid().equalsIgnoreCase("75c2db653aea4065b2b3dc5b4fae1233")) {
//                return;
//            }
//            NbtCompound newNbt;
//            try {
//                newNbt = StringNbtReader.parse(playerData.getPlayerNBT());
//            } catch (CommandSyntaxException e) {
//                throw new RuntimeException(e);
//            }
//            NbtList pos = newNbt.getList("Pos", 6);
//            double x = pos.getDouble(0);
//            double y = pos.getDouble(1);
//            double z = pos.getDouble(2);
//            PortalLocation closestPortal = null;
//            double closestDistance = Double.MAX_VALUE;
//            for (PortalLocation portalLocation : portalLocationCollection.getPortalLocations()) {
//                double distance = distance(x, y, z, portalLocation.centerB().x, portalLocation.centerB().y, portalLocation.centerB().z);
//                if (distance < closestDistance) {
//                    closestDistance = distance;
//                    closestPortal = portalLocation;
//                }
//                distance = distance(x, y, z, portalLocation.centerB().x, portalLocation.centerB().y, portalLocation.centerB().z);
//                if (distance < closestDistance) {
//                    closestDistance = distance;
//                    closestPortal = portalLocation;
//                }
//            }
//            playerData.setInAdventureServer(false);
//            playerData.setTpCoords(closestPortal.centerA().x + "," + closestPortal.centerA().y + "," + closestPortal.centerA().z);
//
//            playerDataCollection.updatePlayerData(playerData);
//            System.out.println(playerData.getUuid() + " is closest to " + closestPortal + " at " + closestDistance + " blocks away");
//        });
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }
}
