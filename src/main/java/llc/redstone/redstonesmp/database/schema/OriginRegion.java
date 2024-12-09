package llc.redstone.redstonesmp.database.schema;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.Heightmap;

import java.util.List;

public class OriginRegion {
    public String name;
    public int x;
    public int y;
    public int z;
    public int radius;

    public OriginRegion() {
    }

    public OriginRegion(String name, int x, int y, int z, int radius) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    public void teleportPlayer(ServerPlayerEntity player) {
        if (radius == 1 || radius == 0 || true) { //True for now cause its having issues lol
            player.teleport(player.getServerWorld(), x, y, z, player.getYaw(), player.getPitch());
            return;
        }
        int x = (int) (this.x + (Math.random() * this.radius * 2) - this.radius);
        int z = (int) (this.z + (Math.random() * this.radius * 2) - this.radius);

        int y = player.getServerWorld().getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
        if (!player.getServer().getOverworld().equals(player.getServerWorld())) return;
        player.teleport(player.getServerWorld(), x, y, z, player.getYaw(), player.getPitch());
    }
}
