package me.ghostgeorge.snailv2;

import me.ghostgeorge.snailv2.commands.snailcommands;
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
    - Implement custom model, use downloaded bb model
    - Correct all logic to work for custom model
    - Stop snails teleporting so close to me
    - Doesnt come close once they are ~2 blocks away
    - Snail pathfinding AI - to player
     */

    // Changing the entity the snail is. Primarily for debugging armadillo spawns
    public EntityType spawnMobType = EntityType.ARMADILLO; // <-- Change this to change snail type
    // Determines whether the snail game is active
    public boolean snailActive = false;
    // No clue what this does
    private final Map<Player, Armadillo> playerSnailMap = new HashMap<>(); // <-- CHANGE ARMADILLO WHEN SPAWNMOBTYPE CHANGES
    // Returns snail map to event listeners
    public Map<Player, Armadillo> getPlayerSnailMap() {
        return playerSnailMap;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupGame();
        getServer().getPluginManager().registerEvents(new eventlisteners(this), this);
        snailcommands commandExecutor = new snailcommands(this);
        this.getCommand("snail").setExecutor(commandExecutor);
        this.getCommand("snail").setTabCompleter(commandExecutor);
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

    public void resetGame(CommandSender sender) {
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
    public void stopSnails() {
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
    public void resetPlayerEffectsAndMode() {
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
    public void removeAreaRestriction(Player player) {
        player.removeMetadata("restrictedArea", this);
    }

    public void spawnSnailsForPlayers() {
        // Sets active variable to true
        snailActive = true;

        for (Player player : Bukkit.getOnlinePlayers()) {
            // Spawn location 15 blocks behind the player
            Location spawnLocation = player.getLocation().clone()
                    .add(player.getLocation().getDirection().normalize().multiply(-15));
            spawnLocation.setY(spawnLocation.getWorld().getHighestBlockYAt(spawnLocation) + 1); // Ensure it's above ground

            // actually spawn the armadillo
            Armadillo snail = (Armadillo) player.getWorld().spawnEntity(spawnLocation, spawnMobType);

            // modify it
            snail.setCustomName(player.getName());
            snail.setCustomNameVisible(true);
            snail.setGlowing(true);
            snail.setMetadata("snail", new FixedMetadataValue(this, true));

            // remember the pairing
            playerSnailMap.put(player, snail);

            // attach follow AI
            startFollowingSnail(snail, player); // Pathfinding

            // Tells player the snail has been summoned
            player.sendMessage(ChatColor.GREEN + "Your Immortal Snail has been summoned.");
        }

    }

    public void startFollowingSnail(Entity snail, Player player) {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!snail.isValid() || !player.isOnline()) return;

            Location playerLoc = player.getLocation();
            Location snailLoc = snail.getLocation();

            double distance = snailLoc.distance(playerLoc);

            if (distance > 30) {
                Location teleportLoc = playerLoc.clone().add(-3, 0, -3);
                snail.teleport(teleportLoc);
            } else if (distance > 5) {
                Vector direction = playerLoc.toVector().subtract(snailLoc.toVector()).normalize().multiply(0.25);
                snail.setVelocity(direction);
            }

            // Prevent curling if the mob is an Armadillo
            if (snail instanceof Armadillo armadillo) {
                // Get help from paper docs
                armadillo.setAggressive(true);
            }

        }, 0L, 10L); // Every 0.5s
    }
}
