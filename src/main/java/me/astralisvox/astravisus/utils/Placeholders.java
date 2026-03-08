package me.astralisvox.astravisus.utils;

import me.astralisvox.astravisus.AstraVisus;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {
    private final AstraVisus plugin;
    private UserDataHandler userDataHandler;

    public Placeholders(AstraVisus plugin) {
        this.plugin = plugin;

    }

    @Override
    public boolean canRegister() { return true; }

    @NotNull
    @Override
    public String getIdentifier() {
        return "astravisus";
    }

    @NotNull
    @Override
    public String getAuthor() { return plugin.getDescription().getAuthors().toString(); }

    @NotNull
    @Override
    public String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if(player == null) {
            return "";
        }

        if(identifier.equals("hasnightvision")){
            return String.valueOf(plugin.getUserDataHandler().getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION));
        }
        return identifier;
    }



}
