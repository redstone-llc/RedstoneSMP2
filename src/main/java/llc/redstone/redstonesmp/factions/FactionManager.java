package llc.redstone.redstonesmp.factions;

import io.icker.factions.api.events.ClaimEvents;
import io.icker.factions.api.events.FactionEvents;
import io.icker.factions.api.events.PlayerEvents;
import io.icker.factions.api.persistents.Faction;
import io.icker.factions.api.persistents.User;
import io.icker.factions.util.Message;
import llc.redstone.redstonesmp.PlayerData;
import net.minecraft.util.ActionResult;

import static llc.redstone.redstonesmp.RedstoneSMP.frozenPlayers;

public class FactionManager {



    public static void register() {
        ClaimEvents.ADD.register((claim) -> {
            int x = claim.x;
            int z = claim.z;
            //if chunk is 1 chunk away from another chunk then remove it
            Faction.all().forEach(faction -> {
                if (faction.getClaims().stream().anyMatch(claim1 -> {
                    if (claim1 == claim) return false; //skip if same claim
                    if (claim.getFaction().equals(claim1.getFaction())) return false; //skip if same faction
                    int x1 = claim1.x;
                    int z1 = claim1.z;
                    return Math.abs(x - x1) <= 1 && Math.abs(z - z1) <= 1;
                })) {
                    claim.remove();
                    new Message("Claim removed due to being too close to another claim").send(claim.getFaction());
                }
            });
        });

        PlayerEvents.IS_INVULNERABLE.register((source, target) -> {
            if (frozenPlayers.containsKey(target.getUuid())) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        });
    }
}
