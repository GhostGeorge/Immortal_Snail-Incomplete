package me.ghostgeorge.snailv2.commands;
import org.bukkit.event.Listener;

public class snailcommands implements Listener {

    // Unimplemented commands

    /*
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("snail")) {
            if (sender instanceof Player && !sender.hasPermission("immortalsnail.admin")) {
                sender.sendMessage("You do not have permission to run this command.");
                return false;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    // Spawn snails for each player and activate them
                    resetPlayerEffectsAndMode();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        removeAreaRestriction(player);
                    }
                    spawnSnailsForPlayers();
                    // Inform players the snails are starting
                    sender.sendMessage(ChatColor.GREEN + "The Immortal Snails have been unleashed!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("stop")) {
                    // Stop snails from moving and dealing damage
                    stopSnails();
                    sender.sendMessage(ChatColor.RED + "The Immortal Snails have been stopped.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reset")) {
                    resetGame(sender);
                    sender.sendMessage(ChatColor.YELLOW + "Game has been reset. All players returned to start.");
                    return true;
                }
            }
        }
        return false;
     }
     */



    /*
    private void spawnSnailsForPlayers() {
        snailActive = true;
        for (Player player : Bukkit.getOnlinePlayers()) {
            // Spawn snail 15 blocks away from the player
            Location spawnLocation = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(15));

            // Make sure the location is not obstructed
            if (!spawnLocation.getBlock().isPassable()) {
                spawnLocation = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(20)); // Adjust spawn point if obstructed
            }

            // Ensure the chunk is loaded before spawning
            spawnLocation.getChunk().load();

            // Log the spawn location for debugging
            getLogger().info("✅ Successfully prepared spawn location at " + spawnLocation);
            player.sendMessage(ChatColor.GREEN + "Snail spawn location: " + spawnLocation.getBlockX() + ", " + spawnLocation.getBlockY() + ", " + spawnLocation.getBlockZ());

            // Spawn the mob based on the configured type
            try {
                Entity snail = (Entity) player.getWorld().spawnEntity(spawnLocation, spawnMobType);
                snail.setMetadata("spawnedByPlugin", new FixedMetadataValue(this, true)); // Mark as plugin-spawned
                snail.setCustomName("Immortal Snail");
                snail.setCustomNameVisible(true);  // Ensure custom name is visible
                snail.setGlowing(true);  // Make the snail glow (for visibility)

                // Log successful spawn
                getLogger().info("✅ Spawned Immortal Snail for " + player.getName() + " at " + spawnLocation);
                player.sendMessage(ChatColor.GREEN + "Snail spawned at: " + spawnLocation.getBlockX() + ", " + spawnLocation.getBlockY() + ", " + spawnLocation.getBlockZ());
                snail.teleport(spawnLocation.add(0, 1, 0)); // Adjust the height a little to ensure it's not stuck in blocks

            } catch (Exception e) {
                // If an error occurs, log it
                getLogger().severe("❌ Failed to spawn snail for " + player.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
     */





}
