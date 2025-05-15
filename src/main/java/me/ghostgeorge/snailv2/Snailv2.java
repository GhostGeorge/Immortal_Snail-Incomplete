package me.ghostgeorge.snailv2;

import me.ghostgeorge.snailv2.listeners.eventlisteners;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Armadillo;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public final class Snailv2 extends JavaPlugin{
    /*
    --TO DO LIST--
    - Make snails follow players, teleport when they get too far
    - Add a method for spawning snails
    - Add start stop reset commands to be able to play the game
    - Command autofill from tutorials
    - Stop armadillos from curling up
    - Snails go across dimensions
    - Custom snail pathfinding AI
    - Review plugin
     */

    // Changing the entity the snail is. Primarily for debugging armadillo spawns
    public EntityType spawnMobType = EntityType.ARMADILLO; // <-- Change this to change snail type
    // Determines whether the snail game is active
    public boolean snailActive = false;
    // No clue what this does
    private final Map<Player, Armadillo> playerSnailMap = new HashMap<>(); // <-- CHANGE ARMADILLO WHEN SPAWNMOBTYPE CHANGES

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupGame();
        getServer().getPluginManager().registerEvents(new eventlisteners(this), this);
        getLogger().info("Snail Enabled");
    }

    public void setupGame() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Calls methods to setup the minigame
            restrictPlayerArea(player);
            grantEffects(player);
        }

        // Enforces area restriction globally every second
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasMetadata("restrictedArea")) {
                    restrictPlayerArea(player);
                }
            }
        }, 0L, 20L); // Run every second
    }

    private void resetGame(CommandSender sender) {
        stopSnails();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
            Location spawn = player.getWorld().getSpawnLocation();
            player.teleport(spawn);
            player.setMetadata("restrictedArea", new FixedMetadataValue(this, true));
            restrictPlayerArea(player);
            grantEffects(player);
            player.sendMessage(ChatColor.YELLOW + "You have been reset to the beginning!");
        }
    }

    // Stops immortal snails
    private void stopSnails() {
        snailActive = false;
        for (Entity snail : playerSnailMap.values()) {
            if (snail != null && !snail.isDead()) {
                snail.remove();
            }
        }
        playerSnailMap.clear();
    }

    // Restricts players until the game starts
    public void restrictPlayerArea(Player player) {
        Location center = player.getWorld().getSpawnLocation();
        double maxDistance = 5;

        // Add metadata if not already present
        if (!player.hasMetadata("restrictedArea")) {
            player.setMetadata("restrictedArea", new FixedMetadataValue(this, true));
        }

        // Enforce teleport if outside bounds
        if (player.getLocation().distance(center) > maxDistance) {
            player.teleport(center);
        }
    }

    // Grants player effects
    public void grantEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 1, true, false));
    }

    // Resets effects and gamemode
    private void resetPlayerEffectsAndMode() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Remove regen and saturation
            player.removePotionEffect(PotionEffectType.REGENERATION);
            player.removePotionEffect(PotionEffectType.SATURATION);

            // Set player to survival
            player.setGameMode(GameMode.SURVIVAL);

            // Remove area restriction
            removeAreaRestriction(player);
        }
    }
    // Removes area restriction
    private void removeAreaRestriction(Player player) {
        player.removeMetadata("restrictedArea", this);
    }

    private void startFollowingSnail(Entity snail, Player player) {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (snail == null || snail.isDead() || !player.isOnline()) return;

            Location snailLoc = snail.getLocation();
            Location playerLoc = player.getLocation();

            double distance = snailLoc.distance(playerLoc);

            // ✅ If too far, teleport closer (~20 blocks away)
            if (distance > 50) {
                Location teleportLoc = playerLoc.clone().add(20, 0, 0);
                teleportLoc.setY(player.getWorld().getHighestBlockYAt(teleportLoc) + 1); // Ensure it's above ground
                snail.teleport(teleportLoc);
                return;
            }

            // ✅ Otherwise, move toward the player gradually
            if (distance > 2) { // Only move if a bit away (not standing on top)
                Vector direction = playerLoc.toVector().subtract(snailLoc.toVector()).normalize().multiply(0.5); // Slow follow
                Location newLoc = snailLoc.clone().add(direction);

                // Optional: face the player
                newLoc.setDirection(playerLoc.toVector().subtract(snailLoc.toVector()));

                // Apply the move
                snail.teleport(newLoc);
            }

            // ✅ Optional: prevent curling if Armadillo (Paper only)
            if (snail instanceof Armadillo armadillo) {
                // Prevent snail from curling here
            }

        }, 0L, 5L); // Run every 5 ticks (~0.25s)
    }


}
