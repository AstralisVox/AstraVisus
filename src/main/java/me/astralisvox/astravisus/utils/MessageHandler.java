package me.astralisvox.astravisus.utils;

import me.astralisvox.astralibs.Utilities;
import me.astralisvox.astravisus.AstraVisus;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MessageHandler {

    private final FileConfiguration messagesConfig;
    private final Set<String> missingMessage = new HashSet<>();

    private String prefix;

    public MessageHandler(FileConfiguration messagesConfig) {
        this.messagesConfig = messagesConfig;

    }

    public String get(String path, String fallback) {
        String raw = messagesConfig.getString(path);

        if (raw == null) {
            getErrorMessage(path);
            return prefix + translate(fallback);
        }

        if (raw.isEmpty()) {
            return "";
        }

        return prefix + translate(raw);
    }

    public String getPrefix() {
        if(messagesConfig.getString("Prefix") == null) {
            getErrorMessage("Prefix");
            return "#8c8c8c[#2b9bbf&lAV#8c8c8c]" + " ";
        }
        if(Objects.requireNonNull(messagesConfig.getString("Prefix")).equalsIgnoreCase("none")) {
            return "";
        }
        return messagesConfig.getString("Prefix") + " ";
    }

    private void getErrorMessage(final String message) {
        if(missingMessage.contains(message)) return;
        missingMessage.add(message);

        Utilities.logInfo(true,
                "There was an error getting the " + message + " message from the " + messagesConfig.getName() + ".",
                "I have set a fallback message to take it's place until the issue is fixed.",
                "To resolve this, please locate " + message + " in the " + messagesConfig.getName() + " and fix the issue."
        );
    }

    private String translate(String msg) {
        // Supports hex (#RRGGBB) + legacy & codes
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
