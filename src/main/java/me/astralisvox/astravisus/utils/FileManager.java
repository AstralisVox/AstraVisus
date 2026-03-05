package me.astralisvox.astravisus.utils;

import me.astralisvox.astravisus.AstraVisus;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 *
 * Handles the messages for the plugin
 *
 * @author AstralisVox
 */
public class MessageHandler {
    private final AstraVisus pluginInstance;
    private FileConfiguration messages;

    public MessageHandler(final AstraVisus pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public void load() {
        File file = new File(pluginInstance.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            pluginInstance.saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String path) {
        String msg = messages.getString(path);
        if (msg == null) {
            return "&cThe message is missing: " + path;
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}