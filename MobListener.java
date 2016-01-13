package com.survivorserver.Civ;
 
import java.util.UUID;
 
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
 
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.scheduler.BukkitRunnable;
 
public class MobListener implements Listener {
 
    private Civ civ;
    private GriefPrevention gp;
     
    public MobListener(Civ civ) {
        this.civ = civ;
        gp = civ.getGp();
    }
     
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == SpawnReason.DEFAULT
            || event.getSpawnReason() == SpawnReason.NATURAL
            || event.getSpawnReason() == SpawnReason.JOCKEY) {
            Location loc = event.getLocation();
            Claim claim = gp.dataStore.getClaimAt(loc, true, null);
            if (claim != null) {
                Settlement set = civ.getSettlementFromClaim(claim.getID());
                if (set != null) {
                    if (!set.getSpawnMobs()) {
                        final UUID uid = event.getEntity().getUniqueId();
                        final Chunk chunk = loc.getChunk();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Entity ent : chunk.getEntities()) {
                                    if (ent instanceof LivingEntity) {
                                        if (((LivingEntity) ent).getUniqueId().equals(uid)) {
                                            ent.remove();
                                            break;
                                        }
                                    }
                                }
                            }
                        }.runTaskLater(civ, 1);
                    }
                }
            }
        }
    }
}
