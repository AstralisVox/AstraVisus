package me.astralisvox.astravisus.commands;

import me.astralisvox.astralibs.Utilities;
import me.astralisvox.astralibs.builders.TabCompleteBuilder;
import me.astralisvox.astralibs.commands.GlobalCommand;
import me.astralisvox.astravisus.AstraVisus;
import me.astralisvox.astravisus.utils.MessageHandler;
import me.astralisvox.astravisus.utils.NightVisionHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NightVisionCommand extends GlobalCommand implements TabCompleter {
    private final AstraVisus pluginInstance;
    private final MessageHandler messageHandler;

    /**
     *
     * The public constructor for the Night Vision command
     *
     * @param pluginInstance (The plugin's instance)
     */
    public NightVisionCommand(final AstraVisus pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.messageHandler = pluginInstance.getMessageHandler();
    }

    /**
     *
     * Handles the execution of the Night Vision Command
     *
     * @param commandSender (The Executor for the command that trying to execute the night vision command)
     * @param strings (The arguments passed into the command)
     */
    @Override
    protected void execute(final CommandSender commandSender, final String[] strings) {

        NightVisionHandler nightVisionToggle = new NightVisionHandler(pluginInstance, commandSender);

        // If there are no args, simply call the night vision toggle method
        if(strings.length == 0) {
            nightVisionToggle.nightVisionToggle();
            return;
        }

        if(strings.length == 1) {
            if(!strings[0].equalsIgnoreCase("list")) {
                Player target = Bukkit.getPlayer(strings[0]);

                if(target == null) {
                    return;
                }

                // Call to the toggle others method
                nightVisionToggle.nightVisionToggleOthers(target);
                return;
            } else {
                if(commandSender instanceof Player player) {

                    // Check if the player has permission to use the list command
                    if(!Utilities.checkPermissions(player, true, "astravisus.nightvision.list", "astravisus.nightvision.admin", "astravisus.admin")) {
                        Utilities.message(player, messageHandler.get("Admin.No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
                        return;
                    }

                    // Send a list of the players who currently have night vision enabled.
                    Utilities.message(player, messageHandler.getPrefix() + "#00D4FFThe following players have night vision enabled:", getPlayerList());
                    return;
                }

                Utilities.logInfo(true, "The following players have night vision enabled:", getPlayerList());
                return;
            }
        }

        if(strings.length == 2) {
            // Checks if the first arg in the command is `global`
            if(!strings[0].equalsIgnoreCase("global")) {
                return;
            }

            if(!strings[1].equalsIgnoreCase("add") && !strings[1].equalsIgnoreCase("remove")) {
                if(commandSender instanceof Player player) {
                    Utilities.message(player,
                            "#2b9bbfNight Vision Global Command: #f63e3e/nightvision global add #2b9bbf- Adds night vision to add online players",
                            "#2b9bbfNight Vision Global Command: #f63e3e/nightvision global remove #2b9bbf- Removes night vision from all online players"
                    );
                } else {
                    Utilities.logWarning(true,
                            "Night Vision Global Command: /nightvision global add - Adds night vision to add online players",
                            "Night Vision Global Command: /nightvision global remove - Removes night vision from all online players"
                    );
                }

            }
            // Call the night vision global method and pass it the second arg in the command
            nightVisionToggle.nightVisionToggleGlobal(strings[1]);
            return;
        }

        if(strings.length == 3) {
            // Checks if the first arg in the command is `temp`
            if(!strings[0].equalsIgnoreCase("temp")) {
                return;
            }
            // Call to the night vision temp method and pass in the second and third args
            nightVisionToggle.nightVisionToggleTemp(Bukkit.getPlayer(strings[1]), Integer.parseInt(strings[2]));
        }
    }

    private String getPlayerList() {
        if(Bukkit.getOnlinePlayers().isEmpty())
        {
            return "There are currently no players online!";
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                break;
            }
            return "No players currently have night vision enabled!";
        }

        // Add all the players who have nightvision to a list
        List<String> nightVisionList = new ArrayList<>();
        for(Player playerName : Bukkit.getOnlinePlayers()) {
            if(playerName.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                nightVisionList.add(playerName.getName());
            }
        }

        // Loop through the list created above and add the names into a string
        StringBuilder playerList = new StringBuilder();
        for(String playerName : nightVisionList) {
            playerList.append(playerName).append(", ");
        }

        return playerList.toString();
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
        if(strings.length == 0) {
            return Collections.emptyList();
        }

        List<String> onlinePlayers = new ArrayList<>();
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(onlinePlayer.getName());
        }

        if(strings.length == 1) {

            return new TabCompleteBuilder(commandSender)
                    .checkCommand("global", true, "astravisus.nightvision.global", "astravisus.nightvision.admin", "astravisus.admin")
                    .checkCommand("temp", true, "astravisus.nightvision.temp", "astravisus.nightvision.admin", "astravisus.admin")
                    .checkCommand(onlinePlayers, true, "astravisus.nightvision.toggle.others", "astravisus.nightvision.admin", "astravisus.admin")
                    .build(strings[0]);
        }

        if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("temp")) {
                return new TabCompleteBuilder(commandSender)
                        .checkCommand(onlinePlayers, true, "astravisus.nightvision.temp", "astravisus.nightvision.admin", "astravisus.admin")
                        .build(strings[1]);
            }

            if(strings[0].equalsIgnoreCase("global")) {
                return new TabCompleteBuilder(commandSender)
                        .checkCommand("add", true, "astravisus.nightvision.global", "astravisus.nightvision.admin", "astravisus.admin")
                        .checkCommand("remove", true, "astravisus.nightvision.global", "astravisus.nightvision.admin", "astravisus.admin")
                        .build(strings[1]);
            }
        }

        return Collections.emptyList();
    }
}
