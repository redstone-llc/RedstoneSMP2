package llc.redstone.redstonesmp.database.schema;

import java.util.ArrayList;
import java.util.List;

public class OriginContinents {
    public String continent;
    public String originId;
    public List<OriginRegion> regions = new ArrayList<>();

    public void addRegion(OriginRegion region) {
        regions.add(region);
    }

    public OriginRegion getRegion(String region) {
        for (OriginRegion r : regions) {
            if (r.name.equals(region)) {
                return r;
            }
        }
        return null;
    }

    public boolean hasRegion(String continent, String region) {
        if (!this.continent.equals(continent)) {
            return false;
        }
        for (OriginRegion r : regions) {
            if (r.name.equals(region)) {
                return true;
            }
        }
        return false;
    }
}