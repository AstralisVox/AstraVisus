package me.astralisvox.astravisus.utils;

import me.astralisvox.astralibs.Utilities;
import me.astralisvox.astralibs.configs.ConfigCreator;
import me.astralisvox.astralibs.libs.com.tchristofferson.configupdater.ConfigUpdater;
import me.astralisvox.astravisus.AstraVisus;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * Handles the messages for the plugin
 *
 * @author AstralisVox
 */
public class FileManager {
    private final AstraVisus pluginInstance;
    private final ConfigCreator configFile;
    private final ConfigCreator messagesFile;
    private final ConfigCreator userDataFile;

    private static final String CONFIG_VERSION = "1.0";
    private static final String MESSAGES_VERSION = "1.0";

    public FileManager(final AstraVisus pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.configFile = new ConfigCreator("config.yml");
        this.messagesFile = new ConfigCreator("messages.yml");
        this.userDataFile = new ConfigCreator("userData.yml");
    }

    public void setupFiles() {
        getConfigFile().createConfig();
        getMessagesFile().createConfig();
        getUserDataFile().createConfig();
    }

    /**
     *
     * Handles making sure all the files are up-to-date against the default in the resources folder
     *
     */
    public void configUpdater() {
        Utilities.logInfo(true, "Attempting to update the config files....");

        try {
            updateFile(configFile, "config.yml", CONFIG_VERSION);
            updateFile(messagesFile, "messages.yml", MESSAGES_VERSION);

            pluginInstance.onReload();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateFile(ConfigCreator creator, String fileName, String expectedVersion) throws IOException {
        String currentVersion = creator.getConfig().getString("Config_Version");

        if(currentVersion == null) {
            Utilities.logInfo(true, "Update checking has been skipped for " + fileName);
            return;
        }

        if (currentVersion.equals(expectedVersion)) {
            Utilities.logInfo(true, "The " + fileName + " is up to date.");
            return;
        }

        creator.getConfig().set("Config_Version", expectedVersion);
        creator.saveConfig();

        ConfigUpdater.update(pluginInstance, fileName, creator.getFile(), Collections.emptyList());
        Utilities.logInfo(true, "The " + fileName + " has been successfully updated.");
    }

    /**
     *
     * Handles reloading all the files
     *
     */
    public void reloadFiles() {
        getConfigFile().reloadConfig();
        getMessagesFile().reloadConfig();
    }

    /**
     *
     * A getter for the configuration file
     *
     * @return configFile
     */
    public ConfigCreator getConfigFile() {
        return configFile;
    }

    /**
     *
     * A getter for the messages file
     *
     * @return messagesFile
     */
    public ConfigCreator getMessagesFile() {
        return messagesFile;
    }

    public ConfigCreator getUserDataFile() {
        return userDataFile;
    }
}