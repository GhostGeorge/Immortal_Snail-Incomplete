package me.ghostgeorge.snailv2.listeners;

import me.ghostgeorge.snailv2.Snailv2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class eventlisteners implements Listener {
    private final Snailv2 plugin;

    public eventlisteners(Snailv2 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    // Makes snail invincible
    public void onEntityDamage(EntityDamageEvent event) {
        // Gets mob type from main class
        EntityType spawnMobType = plugin.spawnMobType;

        // Prevent snails (Armadillos or other mob types) from taking damage (make them immortal)
        if (event.getEntity().getType() == spawnMobType) {
            event.setCancelled(true);
        }
    }

    // Stops the snail mob type from spawning
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Cancel natural snail mob spawns
        if (event.getEntityType() == plugin.spawnMobType &&
                event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
    }

    // For when a player dies
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            // Switch the player to spectator mode
            player.setGameMode(org.bukkit.GameMode.SPECTATOR);
            player.sendMessage("You died and are now in spectator mode.");
        }
    }

    // Kills player if THEIR snail comes within range
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if the player is too close to any Armadillo
        for (Entity entity : player.getNearbyEntities(1, 1, 1)) { // 5-block radius (adjust as needed)
            if (entity.getType() == plugin.spawnMobType) {
                // Check if the Armadillo's name matches the player's name
                if (entity.getCustomName() != null && entity.getCustomName().equals(player.getName())) {
                    // If the names match, kill the player
                    player.setHealth(0);
                    break; // Stop checking after the first matching armadillo
                }
            }
        }
    }
}
