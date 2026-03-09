package me.astralisvox.astravisus.commands;

import me.astralisvox.astralibs.Utilities;
import me.astralisvox.astralibs.builders.TabCompleteBuilder;
import me.astralisvox.astralibs.commands.GlobalCommand;
import me.astralisvox.astravisus.AstraVisus;
import me.astralisvox.astravisus.utils.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PluginCommand extends GlobalCommand implements TabCompleter {
    private final AstraVisus pluginInstance;
    private final MessageHandler messageHandler;

    public PluginCommand(final AstraVisus pluginInstance) {
        this.pluginInstance = pluginInstance;
        messageHandler = pluginInstance.getMessageHandler();
    }

    @Override
    protected void execute(final CommandSender sender, final String[] strings) {
        // If no arguments as passed into the command, send the command help message
        if(strings.length != 1) {
            helpCommand(sender);
            return;
        }

        // Call the correct method based on the first arg passed into the command
        switch(strings[0]) {
            case "version":
                versionCommand(sender);
                break;
            case "reload":
                reloadCommand(sender);
                break;
            case "debug":
                debugCommand(sender);
                break;
            default:
                helpCommand(sender);
                break;
        }
    }

    /**
     *
     * Method to handle the plugin's reload command
     *
     * @param commandSender (The CommandSender who is trying to execute the command)
     */
    private void reloadCommand(final CommandSender commandSender) {
        // Check if the CommandSender is a player
        if(commandSender instanceof Player player) {

            // Check if the player has permission to reload the plugin
            if(!Utilities.checkPermissions(player, true, "astravisus.reload", "astravisus.admin")) {
                Utilities.message(player, messageHandler.get("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
                return;
            }

            // Reload the plugin and send a message to the player telling them the plugin has reloaded.
            pluginInstance.onReload();
            Utilities.message(player, messageHandler.get("Plugin_Reload", "#f63e3eAstraVisus has successfully been reloaded."));
            return;
        }

        // If the CommandSender is the server console, skip other checks and just reload the plugin
        if(commandSender instanceof ConsoleCommandSender) {
            pluginInstance.onReload();
            Utilities.logInfo(true, "AstraVisus has successfully been reloaded.");
        }
    }

    /**
     *
     * Method to handle the plugin's version command
     *
     * @param sender (The CommandSender who is trying to execute the command)
     */
    private void versionCommand(final CommandSender sender) {
        // Check if the CommandSender is a player
        if(sender instanceof Player player) {

            // Check if the player has permission to view the plugins version
            if(!Utilities.checkPermission(player, true, "astravisus.admin")) {
                return;
            }

            Utilities.message(player, messageHandler.getPrefix() + "#86DE0FAstraVisus #CA002Ev" + pluginInstance.getDescription().getVersion() + " #86DE0FBy AstralisVox");
            return;
        }

        if(sender instanceof ConsoleCommandSender) {
            Utilities.logInfo(true, "AstraVisus v" + pluginInstance.getDescription().getVersion() + " By AstralisVox");
        }
    }

    /**
     *
     * Method to handle the plugin's help command
     *
     * @param sender (The CommandSender who is trying to execute the command)
     */
    private void helpCommand(final CommandSender sender) {
        if(sender instanceof Player player) {
            versionCommand(player);
            Utilities.message(player,
                    messageHandler.getPrefix() + "#86DE0FReload Command: #CA002E/astravisus reload",
                    messageHandler.getPrefix() + "#86DE0FVersion Command: #CA002E/astravisus version",
                    messageHandler.getPrefix() + "#86DE0FHelp Command: #CA002E/astravisus help",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Toggle Command: #CA002E/nightvision",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Toggle Others Command: #CA002E/nightvision <player>",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Global Command: #CA002E/nightvision global add|remove",
                    messageHandler.getPrefix() + "#86DE0FNight Vision Temp Command: #CA002E/nightvision <player> <time>",
                    messageHandler.getPrefix() + "#86DE0FNight Vision List Command: #CA002E/nightvision list"
            );
            return;
        }

        if(sender instanceof ConsoleCommandSender) {
            versionCommand(sender);
            Utilities.logInfo(true,
                    "Reload Command: /astravisus reload",
                    "Version Command: /astravisus version",
                    "Help Command: /astravisus help",
                    "Night Vision Toggle Others Command: /nightvision <player>",
                    "Night Vision Global Command: /nightvision global add|remove",
                    "Night Vision Temp Command: /nightvision <player> <time>",
                    "Night Vision List Command: /nightvision list"
            );
        }
    }

    /**
     *
     * Method to handle the plugin's debug command
     *
     * @param commandSender (The CommandSender who is trying to execute the command)
     */
    private void debugCommand(final CommandSender commandSender) {
        StringBuilder plugins = new StringBuilder();

        if(commandSender instanceof Player player) {

            if(!Utilities.checkPermissions(player, true, "astravisus.debug", "astravisus.admin")) {
                Utilities.message(player, messageHandler.get("No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
                return;
            }

            for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                plugins.append("#ff4a4a").append(plugin.getName()).append(" ").append(plugin.getDescription().getVersion()).append("#14abc9, ");
            }

            Utilities.message(player,
                    "#14abc9===========================================",
                    " #6928f7AstraVisus #ff4a4av" + pluginInstance.getDescription().getVersion() + " #14abc9By AstralisVox",
                    "#14abc9===========================================",
                    " #14abc9Server Brand: #ff4a4a" + Bukkit.getName(),
                    " #14abc9Server Version: #ff4a4a" + Bukkit.getServer().getVersion(),
                    " #14abc9Online Mode: #ff4a4a" + Bukkit.getOnlineMode(),
                    " #14abc9Players Online: #ff4a4a" + Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers(),
                    " #14abc9AstraVisus Commands: #ff4a4a" + Utilities.setCommand().size() + " / 2 #14abc9registered",
                    " #14abc9Currently Installed Plugins...",
                    " " + plugins,
                    "#14abc9==========================================="
            );
            return;
        }

        for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            plugins.append(plugin.getName()).append(" ").append(plugin.getDescription().getVersion()).append(", ");
        }

        Utilities.logInfo(true,
                "===========================================",
                " AstraVisus v" + pluginInstance.getDescription().getVersion() + " By AstralisVox",
                "===========================================",
                " Server Brand: " + Bukkit.getName(),
                " Server Version: " + Bukkit.getServer().getVersion(),
                " Online Mode: " + Bukkit.getOnlineMode(),
                " Players Online: " + Bukkit.getOnlinePlayers().size() + " / " + Bukkit.getMaxPlayers(),
                " AstraVisus Commands: " + Utilities.setCommand().size() + " / 2 registered",
                " Currently Installed Plugins...",
                " " + plugins,
                "==========================================="
        );
    }

    /**
     *
     * Sets up the command tab completion based on player's permissions
     *
     * @param commandSender (Who sent the command)
     * @param command (The argument to add into the tab completion list)
     * @param strings (The command arguments)
     * @return (The completed tab completion list)
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if(strings.length <= 1) {
            return new TabCompleteBuilder(commandSender)
                    .checkCommand("version", true, "astravisus.admin")
                    .checkCommand("reload", true, "astravisus.reload", "astravisus.admin")
                    .checkCommand("debug", true, "astravisus.admin")
                    .build(strings[0]);
        }
        return Collections.emptyList();
    }
}
