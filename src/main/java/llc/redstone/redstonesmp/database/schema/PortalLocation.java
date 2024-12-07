package llc.redstone.redstonesmp.database.schema;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PortalLocation {
    public String name;
    public int aX1;
    public int aY1;
    public int aZ1;
    public int aX2;
    public int aY2;
    public int aZ2;
    public int bX1;
    public int bY1;
    public int bZ1;
    public int bX2;
    public int bY2;
    public int bZ2;

    public PortalLocation() {
    }

    public PortalLocation(String name, int aX1, int aY1, int aZ1, int aX2, int aY2, int aZ2, int bX1, int bY1, int bZ1, int bX2, int bY2, int bZ2) {
        this.name = name;
        this.aX1 = aX1;
        this.aY1 = aY1;
        this.aZ1 = aZ1;
        this.aX2 = aX2;
        this.aY2 = aY2;
        this.aZ2 = aZ2;
        this.bX1 = bX1;
        this.bY1 = bY1;
        this.bZ1 = bZ1;
        this.bX2 = bX2;
        this.bY2 = bY2;
        this.bZ2 = bZ2;
    }

    public boolean isInsideA(double x, double y, double z) {
        int x1 = Math.min(aX1, aX2);
        int x2 = Math.max(aX1, aX2);
        int y1 = Math.min(aY1, aY2);
        int y2 = Math.max(aY1, aY2);
        int z1 = Math.min(aZ1, aZ2);
        int z2 = Math.max(aZ1, aZ2);
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public boolean isInsideB(double x, double y, double z) {
        int x1 = Math.min(bX1, bX2);
        int x2 = Math.max(bX1, bX2);
        int y1 = Math.min(bY1, bY2);
        int y2 = Math.max(bY1, bY2);
        int z1 = Math.min(bZ1, bZ2);
        int z2 = Math.max(bZ1, bZ2);
        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

    public Vec3d centerA() {
        return new Vec3d((aX1 + aX2) / 2.0, (aY1 + aY2) / 2.0, (aZ1 + aZ2) / 2.0);
    }

    public Vec3d centerB() {
        return new Vec3d((bX1 + bX2) / 2.0, (bY1 + bY2) / 2.0, (bZ1 + bZ2) / 2.0);
    }

    public String getName() {
        return name;
    }
}
