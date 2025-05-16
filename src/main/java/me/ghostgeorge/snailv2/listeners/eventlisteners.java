package me.ghostgeorge.snailv2.listeners;

import me.ghostgeorge.snailv2.Snailv2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Armadillo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;

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
        if (event.getEntityType() == plugin.spawnMobType &&
                event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
            plugin.getLogger().info("Cancelled non-plugin spawn of " + event.getEntityType());
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

    // Allows snails to travel across dimensions
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (!plugin.snailActive) return;

        // Remove the old snail (if it exists)
        Entity oldSnail = plugin.getPlayerSnailMap().get(player);
        if (oldSnail != null && !oldSnail.isDead()) {
            oldSnail.remove();
        }

        plugin.getPlayerSnailMap().remove(player);

        // Delay the new snail spawn by 2 seconds (40 ticks)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Make sure player is still online
            if (!player.isOnline()) return;

            // Get a safe spawn location
            Location newSpawnLoc = player.getLocation().clone().add(player.getLocation().getDirection().normalize().multiply(-15));
            newSpawnLoc.setY(player.getWorld().getHighestBlockYAt(newSpawnLoc) + 1);

            // Spawn new snail
            Entity newSnail = player.getWorld().spawnEntity(newSpawnLoc, plugin.spawnMobType);
            newSnail.setCustomName(player.getName());
            newSnail.setCustomNameVisible(true);
            newSnail.setGlowing(true);
            newSnail.setMetadata("snail", new FixedMetadataValue(plugin, true));

            plugin.getPlayerSnailMap().put(player, (Armadillo) newSnail); // Update Armadillo to the snail mob type
            plugin.startFollowingSnail(newSnail, player);

            player.sendMessage(ChatColor.GREEN + "Your Immortal Snail followed you through dimensions!");
        }, 160L); // 160 ticks = 8 seconds
    }
}
