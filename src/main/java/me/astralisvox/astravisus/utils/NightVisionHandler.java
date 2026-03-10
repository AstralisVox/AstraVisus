package me.astralisvox.astravisus.utils;

import me.astralisvox.astralibs.Utilities;
import me.astralisvox.astravisus.AstraVisus;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class NightVisionHandler {
    private final FileConfiguration configFile;
    private final MessageHandler messagesHandler;
    private final UserDataHandler userDataHandler;

    private final boolean particleEffects;
    private final boolean particleAmbients;
    private final boolean nightVisionIcon;

    private final String nightVisionApplied;
    private final String nightVisionRemoved;
    private final String nightVisionAppliedActionbar;
    private final String nightVisionRemovedActionbar;

    private final CommandSender commandSender;

    public NightVisionHandler(final AstraVisus pluginInstance, final CommandSender commandSender) {
        this.commandSender = commandSender;
        configFile = pluginInstance.getFileManager().getConfigFile().getConfig();
        messagesHandler = pluginInstance.getMessageHandler();
        userDataHandler = pluginInstance.getUserDataHandler();

        particleEffects = configFile.getBoolean("Night_Vision_Settings.Particle_Effects");
        particleAmbients = configFile.getBoolean("Night_Vision_Settings.Particle_Ambient");
        nightVisionIcon = configFile.getBoolean("Night_Vision_Settings.Night_Vision_Icon");

        nightVisionApplied = messagesHandler.get("NightVision.Enabled", "#2b9bbfNight Vision has been applied!");
        nightVisionRemoved = messagesHandler.get("NightVision.Disabled", "#f63e3eNight Vision has been removed!");
        nightVisionAppliedActionbar = messagesHandler.get("NightVision.ActionBar.Enabled", "#2b9bbfNight vision has been applied!");
        nightVisionRemovedActionbar = messagesHandler.get("NightVision.ActionBar.Disabled", "#f63e3eNight Vision has been removed!");
    }

    /**
     *
     * Handles toggling night vision on|off for a specific player
     *
     */
    public void nightVisionToggle() {
        if(commandSender instanceof ConsoleCommandSender) {
            return;
        }
        Player player = (Player) commandSender;

        // Check if the player has permission
        if(!toggleSelfPerm(player)) {
            return;
        }

        // Check if the player currently has night vision enabled
        if((boolean) userDataHandler.getEffectStatus(player.getUniqueId(), UserDataHandler.NIGHT_VISION)) {
            // Remove night vision from the player
            userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
            Utilities.removePotionEffect(player, PotionEffectType.NIGHT_VISION);
            userDataHandler.saveUserDataToFile(player.getUniqueId());

            // Send night vision removal messages
            sendNightVisionRemovedMessages(player);
            //toggleSoundEffect(player, "Night_Vision_Disabled");
            return;
        }

        // Check if the night vision cost has been enabled, and withdraw the money if it has.
        if (!withdrawNightVisionCost(player)) {
            return;
        }

        // Check if they have particle bypass perm and apply correct night vision effect
        applyNightVision(player, 60 * 60 * 24 * 100);
    }

    /**
     *
     * Handles toggling night vision on|off for a target player
     *
     * @param target (The player whose night vision status is to be modified)
     */
    public void nightVisionToggleOthers(final Player target) {
        if(!(commandSender instanceof ConsoleCommandSender)) {
            Player player = (Player) commandSender;

            // Check if the player has permission
            if(!toggleOthersPerm(player)) {
                return;
            }

            if(target.getName().equals(player.getName())) {
                if(!Utilities.checkPermissions(player, true, "astravisus.nightvision.toggle", "astravisus.nightvision.admin", "astravisus.admin")) {
                    Utilities.message(player, messagesHandler.get("Admin.No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
                    return;
                }
            }
        }

        // Check if the target currently has night vision enabled
        if((boolean) userDataHandler.getEffectStatus(target.getUniqueId(), UserDataHandler.NIGHT_VISION)) {
            // Remove night vision from the target
            userDataHandler.setEffectStatus(target.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
            userDataHandler.saveUserDataToFile(target.getUniqueId());
            Utilities.removePotionEffect(target, PotionEffectType.NIGHT_VISION);

            // Send night vision removal messages
            sendNightVisionRemovedMessages(target);
            //toggleSoundEffect(target, "Night_Vision_Disabled");
            return;
        }

        // Check if the target has particle bypass perm and apply correct night vision effect
        applyNightVision(target, 60 * 60 * 24 * 100);
    }

    /**
     *
     * Handles toggling night vision on for a specific amount of time
     *
     * @param target (The player whose night vision status is to be modified)
     * @param seconds (The duration in seconds for how long the night vision will last)
     */
    public void nightVisionToggleTemp(final Player target, final int seconds) {
        if(!(commandSender instanceof ConsoleCommandSender)) {
            Player player = (Player) commandSender;

            // Check if the player has permission
            if(!toggleTempPerm(player)) {
                return;
            }
        }

        // Check if the target currently has night vision enabled
        if((boolean) userDataHandler.getEffectStatus(target.getUniqueId(), UserDataHandler.NIGHT_VISION)) {
            // Remove night vision from the target
            Utilities.removePotionEffect(target, PotionEffectType.NIGHT_VISION);
        }

        // Check if the target has particle bypass perm and apply correct night vision effect
        applyNightVision(target, seconds);
    }

    /**
     *
     * Handles toggling night vision on|off for all players currently online
     *
     * @param action (Either `add` | `remove`)
     */
    public void nightVisionToggleGlobal(final String action) {
        if(!(commandSender instanceof ConsoleCommandSender)) {
            Player player = (Player) commandSender;

            // Check if the player has permission
            if(!toggleGlobalPerm(player)) {
                return;
            }
        }

        if(Bukkit.getOnlinePlayers().isEmpty()){
            Utilities.logWarning(true, "There are currently no players online!");
            return;
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(action.equalsIgnoreCase("remove")) {
                // Remove night vision from the target
                userDataHandler.setEffectStatus(player.getUniqueId(), false, UserDataHandler.NIGHT_VISION);
                Utilities.removePotionEffect(player, PotionEffectType.NIGHT_VISION);

                if(Utilities.checkPermissions(player, true, "astravisus.nightvision.global", "astravisus.nightvision.admin", "astravisus.admin")) {
                    Utilities.message(player, messagesHandler.get("NightVision.Disabled", "#2b9bbfNight Vision has been removed for all players!"));
                }
                //toggleSoundEffect(player, "Night_Vision_Disabled");
                continue;
            }

            if(action.equalsIgnoreCase("add")) {
                // Check if the target has particle bypass perm and apply correct night vision effect
                applyNightVisionGlobal(player);

                // Send night vision applied messages
                if(Utilities.checkPermissions(player, true, "astravisus.nightvision.global", "astravisus.nightvision.admin", "astravisus.admin")) {
                    Utilities.message(player, messagesHandler.get("NightVision.Enabled", "#2b9bbfNight Vision has been applied for all players!"));
                }
            }
        }
    }

    /**
     *
     * Handles how the night vision effect is applied for the player
     *
     * @param player (The player that night vision is to be applied to)
     * @param duration (The duration in seconds for how long the night vision will last)
     */
    private void applyNightVision(final Player player, final int duration) {

        userDataHandler.setEffectStatus(player.getUniqueId(), true, UserDataHandler.NIGHT_VISION);
        Utilities.addPotionEffect(player, PotionEffectType.NIGHT_VISION, duration ,1, particleEffects, particleAmbients, nightVisionIcon);
        //toggleSoundEffect(player, "Night_Vision_Applied");
        sendNightVisionAppliedMessages(player);
        userDataHandler.saveUserDataToFile(player.getUniqueId());
    }

    /**
     *
     * Handles how the night vision effect is applied for all player currently online
     *
     * @param player (The player that night vision is to be applied to)
     */
    public void applyNightVisionGlobal(final Player player) {
        userDataHandler.setEffectStatus(player.getUniqueId(), true, UserDataHandler.NIGHT_VISION);
        userDataHandler.saveUserDataToFile(player.getUniqueId());
        Utilities.addPotionEffect(player, PotionEffectType.NIGHT_VISION, 60 * 60 * 24 * 100 ,1, particleEffects, particleAmbients, nightVisionIcon);

        //toggleSoundEffect(player, "Night_Vision_Applied");
    }

    /**
     *
     * Sends the player a message notifying them that night vision has been applied
     *
     * @param player (The player that the message needs to be sent to)
     */
    private void sendNightVisionAppliedMessages(final Player player) {
        Utilities.message(player, nightVisionApplied);

        if(configFile.getBoolean("Night_Vision_Settings.ActionBar_Message")) {
            Utilities.sendActionBar(player, nightVisionAppliedActionbar);
        }
    }

    /**
     *
     * Sends the player a message notifying them that night vision has been removed
     *
     * @param player (The player that the message needs to be sent to)
     */
    private void sendNightVisionRemovedMessages(final Player player) {
        Utilities.message(player, nightVisionRemoved);
        if(configFile.getBoolean("Night_Vision_Settings.ActionBar_Message")) {
            Utilities.sendActionBar(player, nightVisionRemovedActionbar);
        }
    }

    /**
     *
     * Checks the player's permission for the toggle-self command
     *
     * @param player (The player who is being checked for permission)
     * @return (True | false depending on the permission)
     */
    private boolean toggleSelfPerm(final Player player) {
        if(!Utilities.checkPermissions(player, true, "astravisus.nightvision.toggle", "astravisus.nightvision.admin", "astravisus.admin")) {
            Utilities.message(player, messagesHandler.get("Admin.No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
            return false;
        }
        return true;
    }

    /**
     *
     * Checks the player's permission for the toggle-global command
     *
     * @param player (The player who is being checked for permission)
     * @return (True | false depending on the permission)
     */
    private boolean toggleGlobalPerm(final Player player) {
        if(!Utilities.checkPermissions(player, true, "astravisus.nightvision.global", "astravisus.nightvision.admin", "astravisus.admin")) {
            Utilities.message(player, messagesHandler.get("Admin.No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
            return false;
        }
        return true;
    }

    /**
     *
     * Checks the player's permission for the toggle-temp command
     *
     * @param player (The player who is being checked for permission)
     * @return (True | false depending on the permission)
     */
    private boolean toggleTempPerm(final Player player) {
        if(!Utilities.checkPermissions(player, true, "astravisus.nightvision.temp", "astravisus.nightvision.admin", "astravisus.admin")) {
            Utilities.message(player, messagesHandler.get("Admin.No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
            return false;
        }
        return true;
    }

    /**
     *
     * Checks the player's permission for the toggle-others command
     *
     * @param player (The player who is being checked for permission)
     * @return (True | false depending on the permission)
     */
    private boolean toggleOthersPerm(final Player player) {
        if(!Utilities.checkPermissions(player, true, "astravisus.nightvision.toggle.others", "astravisus.nightvision.admin", "astravisus.admin")) {
            Utilities.message(player, messagesHandler.get("Admin.No_Permission", "#f63e3eSorry, but you don't have permission to do that."));
            return false;
        }
        return true;
    }

//    /**
//     *
//     * Toggles a sound effect depending on the action taken for a specific player
//     *
//     * @param player (The player to play the sound effect for)
//     * @param soundEffect (The specific sound effect that needs to be played)
//     */
//    public void toggleSoundEffect(final Player player, final String soundEffect) {
//
//        if(!configFile.getBoolean("Sound_Effects.Enabled")) {
//            return;
//        }
//
//        switch (soundEffect) {
//            case "Night_Vision_Applied":
//                if (!configFile.getBoolean("Sound_Effects.Night_Vision_Enable.Enabled")) {
//                    break;
//                }
//                player.playSound(player.getLocation(), Sound.valueOf(Objects.requireNonNull(configFile.getString("Sound_Effects.Night_Vision_Enable.Sound"))), 1, 1);
//                break;
//            case "Night_Vision_Disabled":
//                if (!configFile.getBoolean("Sound_Effects.Night_Vision_Disable.Enabled")) {
//                    break;
//                }
//                player.playSound(player.getLocation(), Sound.valueOf(Objects.requireNonNull(configFile.getString("Sound_Effects.Night_Vision_Disable.Sound"))), 1, 1);
//                break;
//            case "Limit_Reached":
//                if (!configFile.getBoolean("Sound_Effects.Limit_Reached.Enabled")) {
//                    break;
//                }
//                player.playSound(player.getLocation(), Sound.valueOf(Objects.requireNonNull(configFile.getString("Sound_Effects.Limit_Reached.Sound"))), 1, 1);
//                break;
//            default:
//                break;
//        }
//    }

    /**
     *
     * Handles checking if the player needs to be charged a specific amount
     * when they try to enable night vision.
     *
     * @param player (The player trying to enable night vision)
     * @return (True/False depending on if withdrawal was successful)
     */
    private boolean withdrawNightVisionCost( final Player player) {
        // Check if the night vision cost has been enabled.
        if (configFile.getBoolean("Night_Vision_Settings.Night_Vision_Cost.Enabled")) {
            double nightVisionCost = configFile.getDouble("Night_Vision_Settings.Night_Vision_Cost.Amount", 0);

            if(Utilities.checkPermissions(player, true, "astravisus.nightvision.cost.bypass", "astravisus.nightvision.admin", "astravisus.admin")) {
                return true;
            }

            // Try to remove the cost from the players balance
            EconomyResponse economyResponse = AstraVisus.getEcon().withdrawPlayer(player, nightVisionCost);

            // Withdrawal was not successful, send players a message to inform them and return
            if (!economyResponse.transactionSuccess()) {
                Utilities.message(player, messagesHandler.get("Cost.Not_Enough_Money", "#f63e3eSorry, you do not have enough money to use that command!"));
                return false;
            }
            // Withdrawal was successful so send a message to the player and toggle night vision
            Utilities.message(player,
                    messagesHandler.get(
                                    "Cost.Charged",
                                    "#2b9bbfYou have been charged #f63e3e$%NightVisionCost% #2b9bbfto use Night Vision!")
                            .replace("%NightVisionCost%", String.valueOf(nightVisionCost))
            );
            return true;
        }
        return true;
    }
}
