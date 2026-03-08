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

    private void loadPrefix() {
        String raw = messagesConfig.getString("Plugin_Prefix");

        if (raw == null) {
            getErrorMessage("Plugin_Prefix");
            prefix = translate("#8c8c8c[#2b9bbf&lOV#8c8c8c] ");
            return;
        }

        if (raw.equalsIgnoreCase("none")) {
            prefix = "";
            return;
        }

        prefix = translate(raw + " ");
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

    public String console(String path, String fallback) {
        String raw = messagesConfig.getString(path);

        if (raw == null) {
            getErrorMessage(path);
            return translate(fallback);
        }

        if (raw.isEmpty()) {
            return "";
        }

        return translate(raw);
    }

    public String getPrefix() {
        if(messagesConfig.getString("Plugin_Prefix") == null) {
            getErrorMessage("Plugin_Prefix");
            return "#8c8c8c[#2b9bbf&lOV#8c8c8c]" + " ";
        }
        if(Objects.requireNonNull(messagesConfig.getString("Plugin_Prefix")).equalsIgnoreCase("none")) {
            return "";
        }
        return messagesConfig.getString("Plugin_Prefix") + " ";
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
