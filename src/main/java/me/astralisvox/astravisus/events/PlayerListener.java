package me.astralisvox.astravisus.events;

import me.astralisvox.astralibs.Utilities;
import me.astralisvox.astravisus.AstraVisus;
import me.astralisvox.astravisus.utils.MessageHandler;
import me.astralisvox.astravisus.utils.NightVisionHandler;
import me.astralisvox.astravisus.utils.UserDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener implements Listener {
    private final AstraVisus pluginInstance;
    private final FileConfiguration configFile;

    private final UserDataHandler userDataHandler;
    private final boolean particleEffects;
    private final boolean ambientEffects;
    private final boolean nightvisionIcon;

    public PlayerListener(final AstraVisus pluginInstance) {
        this.pluginInstance = pluginInstance;
        configFile = pluginInstance.getFileManager().getConfigFile().getConfig();
        userDataHandler = pluginInstance.getUserDataHandler();

        particleEffects = configFile.getBoolean("Night_Vision_Settings.Particle_Effects");
        ambientEffects = configFile.getBoolean("Night_Vision_Settings.Particle_Ambient");
        nightvisionIcon = configFile.getBoolean("Night_Vision_Settings.Night_Vision_Icon");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();

        if(configFile.getBoolean("Update_Notify") && Utilities.checkPermissions(player, true, "astravisus.updates", "astravisus.admin")) {
            pluginInstance.updateChecker();
        }

        if(player.getFirstPlayed() == System.currentTimeMillis()) {
            userDataHandler.getUserDataMap().putIfAbsent(player.getUniqueId(), new ConcurrentHashMap<>());
        } else {
            userDataHandler.addUserToMap(player.getUniqueId());
        }

        if(configFile.getBoolean("Night_Vision_Settings.Night_Vision_Login")
         && (boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION)
         && Utilities.checkPermissions(player, true, "astravisus.nightivison.login", "astravisus.nightvision.admin", "astravisus.admin")) {
            applyNightVision(player);
            userDataHandler.setEffectStatus(player.getUniqueId(), true, UserDataHandler.NIGHT_VISION);
        } else {
            userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        // Save the user's data to the file
        userDataHandler.saveUserDataToFile(playerQuitEvent.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent playerChangedWorldEvent) {
        final Player player = playerChangedWorldEvent.getPlayer();

        // Checks if the world disabled setting has been enabled.
        if(!configFile.getBoolean("Night_Vision_World_Settings.Enabled")) {
            return;
        }

        // Checks if the player has permission to bypass the night vision change world feature
        if(Utilities.checkPermissions(player, true, "astravisus.nightvision.world.bypass", "astravisus.nightvision.admin", "astravisus.admin")) {
            return;
        }

        // Checks the players new world against the list of worlds in the config
        for(String worldName : configFile.getStringList("Night_Vision_World_Settings.Disabled_Worlds.Worlds")) {
            if(!(boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION)) {
                return;
            }

            // If the world name is in the config world list, remove night vision from the player
            if(player.getWorld().getName().equalsIgnoreCase(worldName)) {
                userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
                Utilities.removePotionEffect(player, PotionEffectType.NIGHT_VISION);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        Player player = playerRespawnEvent.getPlayer();

        // Wait 1 full second to allow for respawn to finish before checking night vision status
        Bukkit.getScheduler().runTaskLater(pluginInstance, () -> {
            // Checks if the keep on death feature has been enabled
            if(!configFile.getBoolean("Night_Vision_Settings.Keep_Night_Vision_On_Death")) {
                return;
            }

            // Checks if the player has permission to keep their night vision when they die
            if(!Utilities.checkPermissions(player, false, "omegavision.nightvision.keepondeath", "omegavision.nightvision.admin", "omegavision.admin")) {
                userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
                Utilities.removePotionEffect(player, PotionEffectType.NIGHT_VISION);
                return;
            }

            if(!((boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION))) {
                return;
            }

            // Re-applies night vision to the player after they respawn
            applyNightVision(player);

            NightVisionHandler nightVisionToggle = new NightVisionHandler(pluginInstance, player);
            nightVisionToggle.toggleSoundEffect(player, "Night_Vision_Applied");
        }, 20);
    }

    private void applyNightVision(Player player) {
        Utilities.addPotionEffect(player,
                PotionEffectType.NIGHT_VISION,
                60 * 60 * 24 * 100,
                1,
                particleEffects,
                ambientEffects,
                nightvisionIcon)
        ;

    }

}
