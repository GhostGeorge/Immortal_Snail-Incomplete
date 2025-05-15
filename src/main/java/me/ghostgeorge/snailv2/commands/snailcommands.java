package me.ghostgeorge.snailv2.commands;
import me.ghostgeorge.snailv2.Snailv2;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class snailcommands implements CommandExecutor {
    private final Snailv2 plugin;

    public snailcommands(Snailv2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // If snail command is run
        if (command.getName().equalsIgnoreCase("snail")) {
            // Checks for permissions to run command
            if (sender instanceof Player && !sender.hasPermission("immortalsnail.admin")) {
                sender.sendMessage("You do not have permission to run this command.");
                return false;
            }
            // If user has permissions
            if (args.length == 1) {
                // If command arg was start
                if (args[0].equalsIgnoreCase("start")) {
                    // Takes away starting effects
                    plugin.resetPlayerEffectsAndMode();
                    // Removes area restriction for all players
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        plugin.removeAreaRestriction(player);
                    }
                    // Spawns a snail for each player
                    plugin.spawnSnailsForPlayers();
                    // Inform players the snails are starting
                    sender.sendMessage(ChatColor.GREEN + "The Immortal Snails have been unleashed!");
                    return true;
                }
                // If command arg was reset
                if (args[0].equalsIgnoreCase("reset")) {
                    // Executes reset game method
                    plugin.resetGame(sender);
                    sender.sendMessage(ChatColor.YELLOW + "Game has been reset. All players returned to start.");
                    return true;
                }
            }
        }
        return false;
    }
}
