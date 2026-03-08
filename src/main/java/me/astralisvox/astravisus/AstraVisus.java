package me.astralisvox.astravisus;

import me.astralisvox.astralibs.PluginUpdater;
import me.astralisvox.astralibs.Utilities;
import me.astralisvox.astravisus.commands.NightVisionCommand;
import me.astralisvox.astravisus.commands.PluginCommand;
import me.astralisvox.astravisus.events.PlayerListener;
import me.astralisvox.astravisus.utils.FileManager;
import me.astralisvox.astravisus.utils.MessageHandler;
import me.astralisvox.astravisus.utils.Placeholders;
import me.astralisvox.astravisus.utils.UserDataHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class AstraVisus extends JavaPlugin {
    private AstraVisus pluginInstance;
    private FileManager fileManager;
    private UserDataHandler userDataHandler;
    private MessageHandler messageHandler;

    private static Economy econ = null;

    @Override
    public void onEnable() {
        pluginInstance = this;
        Utilities.setInstance(pluginInstance);

        Utilities.logInfo(false,
                "AstraVisus v" + pluginInstance.getDescription().getVersion() + " by AstralisVox",
                "Running on version: " + Bukkit.getVersion()
        );

        fileManager = new FileManager(pluginInstance);
        getFileManager().setupFiles();
        getFileManager().configUpdater();

        userDataHandler = new UserDataHandler(pluginInstance);
        messageHandler = new MessageHandler(fileManager.getMessagesFile().getConfig());

        // Populate the user data map with entries from the user data file
        getUserDataHandler().populateUserDataMap();

        setupPlaceholders(pluginInstance);
        //updateChecker();
        setupEconomy();
        registerEvents();
        registerCommands();
    }

    public void onReload() {
        getFileManager().reloadFiles();
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }

    private void registerCommands() {
        Utilities.setCommand().put("astravisus", new PluginCommand(pluginInstance));
        Utilities.setCommand().put("nightvision", new NightVisionCommand(pluginInstance));

        Utilities.registerCommands();
    }

    private void setupPlaceholders(AstraVisus pluginInstance) {
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Utilities.logWarning(true,
                    "AstraVisus requires PlaceholderAPI to be installed if you are wanting to use any of the placeholders",
                    "You can install PlaceholderAPI here: https://www.spigotmc.org/resources/placeholderapi.6245/ "
            );
        } else {
            new Placeholders(pluginInstance).register();
        }
    }

    public void updateChecker() {
        if(getFileManager().getConfigFile().getConfig().getBoolean("Update_Notify")) {
            new PluginUpdater(pluginInstance, 73013).getVersion(version -> {
                int spigotVersion = Integer.parseInt(version.replace(".", ""));
                int pluginVersion = Integer.parseInt(pluginInstance.getDescription().getVersion().replace(".", ""));

                if(pluginVersion >= spigotVersion) {
                    Utilities.logInfo(true, "You are already running the latest version");
                    return;
                }

                PluginDescriptionFile pdf = pluginInstance.getDescription();
                Utilities.logWarning(true,
                        "A new version of " + pdf.getName() + " is avaliable!",
                        "Current Version: " + pdf.getVersion() + " > New Version: " + version,
                        "Grab it here: https://www.spigotmc.org/resources/omegavision.73013/"
                );
            });
        }
    }

    public static Economy getEcon() {
        return econ;
    }

    private void registerEvents() {
        Utilities.registerEvent(new PlayerListener(pluginInstance));
    }

    public FileManager getFileManager() {
        return fileManager;
    }
    public UserDataHandler getUserDataHandler() {
        return userDataHandler;
    }
    public MessageHandler getMessageHandler() {return messageHandler;}
}