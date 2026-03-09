package me.astralisvox.astravisus.utils;

import me.astralisvox.astralibs.PluginUpdater;
import me.astralisvox.astralibs.Utilities;
import me.astralisvox.astravisus.AstraVisus;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class UpdateChecker {
    private AstraVisus pluginInstance;
    private MessageHandler messageHandler;

    public UpdateChecker(AstraVisus pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.messageHandler = pluginInstance.getMessageHandler();
    }

    public void pluginUpdatesConsole() {
        if(pluginInstance.getFileManager().getConfigFile().getConfig().getBoolean("Update_Notify")) {
            new PluginUpdater(pluginInstance, 133245).getVersion(version -> {
                int spigotVersion = Integer.parseInt(version.replace(".", ""));
                int pluginVersion = Integer.parseInt(pluginInstance.getDescription().getVersion().replace(".", ""));

                if(pluginVersion >= spigotVersion) {
                    Utilities.logInfo(true, "You are already running the latest version");
                } else {
                    PluginDescriptionFile pdf = pluginInstance.getDescription();
                    Utilities.logWarning(true,
                            "A new version of " + pdf.getName() + " is available!",
                            "Current Version: " + pdf.getVersion() + " > New Version: " + version,
                            "Grab it here: https://www.spigotmc.org/resources/astravisus.133245/"
                    );
                }
            });
        }
    }

    public void pluginUpdatesPlayer(Player player) {
        if(pluginInstance.getFileManager().getConfigFile().getConfig().getBoolean("Update_Notify")) {
            new PluginUpdater(pluginInstance, 133245).getVersion(version -> {
                int spigotVersion = Integer.parseInt(version.replace(".", ""));
                int pluginVersion = Integer.parseInt(pluginInstance.getDescription().getVersion().replace(".", ""));

                if(pluginVersion >= spigotVersion) {
                    Utilities.message(player, messageHandler.get("Updates.Up_To_Date", "&aYou are running the latest version of AstraVisus."));
                } else {
                    PluginDescriptionFile pdf = pluginInstance.getDescription();
                    Utilities.message(player,
                            messageHandler.getPrefix() + "&aA new version of " + pdf.getName() + "&a is available!",
                            messageHandler.getPrefix() + "&aCurrent Version: " + pdf.getVersion() + "&a > New Version: " + version,
                            messageHandler.getPrefix() + "&aGrab it here: https://www.spigotmc.org/resources/astravisus.133245/"
                    );
                }
            });
        }
    }
}
