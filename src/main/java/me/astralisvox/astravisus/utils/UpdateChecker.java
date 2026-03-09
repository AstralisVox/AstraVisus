package me.astralisvox.astravisus.utils;

import me.astralisvox.astralibs.PluginUpdater;
import me.astralisvox.astralibs.Utilities;
import me.astralisvox.astravisus.AstraVisus;
import org.bukkit.plugin.PluginDescriptionFile;

public class UpdateChecker {
    public UpdateChecker(AstraVisus pluginInstance) {
        pluginUpdates(pluginInstance);
    }

    public void pluginUpdates(AstraVisus pluginInstance) {
        if(pluginInstance.getFileManager().getConfigFile().getConfig().getBoolean("Update_Notify")) {
            new PluginUpdater(pluginInstance, 133245).getVersion(version -> {
                int spigotVersion = Integer.parseInt(version.replace(".", ""));
                int pluginVersion = Integer.parseInt(pluginInstance.getDescription().getVersion().replace(".", ""));

                if(pluginVersion >= spigotVersion) {
                    Utilities.logInfo(true, "You are already running the latest version");
                    return;
                }

                PluginDescriptionFile pdf = pluginInstance.getDescription();
                Utilities.logWarning(true,
                        "A new version of " + pdf.getName() + " is available!",
                        "Current Version: " + pdf.getVersion() + " > New Version: " + version,
                        "Grab it here: https://www.spigotmc.org/resources/astravisus.133245/"
                );
            });
        }
    }
}
