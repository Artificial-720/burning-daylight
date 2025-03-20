package io.github.artificial720.burningDaylight;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BurningDaylightConfig {
    public int gracePeriodDuration; // in seconds
    public boolean gracePeriodNotifyPlayer;
    public boolean gracePeriodOnFirstJoin;
    public boolean gracePeriodOnRespawn;
    public String gracePeriodStartMsg;
    public String gracePeriodEndMsg;

    public boolean preventWithFireResistance;

    public double burnDamageDay;
    public double burnDamageNight;
    public double burnDamageWeather;

    // Armor behavior
    private double leatherArmor;
    private double ironArmor;
    private double goldArmor;
    private double diamondArmor;
    private double netheriteArmor;
    public boolean durabilityDay;
    public boolean durabilityNight;
    public boolean durabilityWeather;

    public int effectDuration; // in seconds

    // Logging
    public boolean loggingEnabled ;
    public boolean logToChat;
    public boolean logToConsole;

    // Exempt players
    private Set<String> exemptPlayers;

    // Enabled worlds
    private Set<String> enabledWorlds;

    public BurningDaylightConfig(FileConfiguration config) {
        loadConfigValues(config);
    }

    public void loadConfigValues(FileConfiguration config) {
        // grace period
        gracePeriodDuration = config.getInt("grace_period.duration", 300);
        gracePeriodNotifyPlayer = config.getBoolean("grace_period.notify_player", true);
        gracePeriodOnFirstJoin = config.getBoolean("grace_period.apply_on.first_join", true);
        gracePeriodOnRespawn = config.getBoolean("grace_period.apply_on.respawn", false);
        gracePeriodStartMsg = config.getString("grace_period.message.start", "<green>You have a 5-minute grace period where you are immune to sun damage. Good Luck!");
        gracePeriodEndMsg = config.getString("grace_period.message.end", "<red>Your grace period has ended. You can now take damage.");

        // prevent damage
        preventWithFireResistance = config.getBoolean("damage_prevention.fire_resistance_potion", true);

        // damage numbers
        burnDamageDay = config.getDouble("burn_damage.day", 2.0);
        burnDamageNight = config.getDouble("burn_damage.night", 1.0);
        burnDamageWeather = config.getDouble("burn_damage.weather", 1.0);

        // Armor behavior
        leatherArmor = config.getDouble("armor_behavior.damage_reduciton.leather_armor", 0.25);
        ironArmor = config.getDouble("armor_behavior.damage_reduciton.iron_armor", 0.0);
        goldArmor = config.getDouble("armor_behavior.damage_reduciton.gold_armor", 0.0);
        diamondArmor = config.getDouble("armor_behavior.damage_reduciton.diamond_armor", 0.0);
        netheriteArmor = config.getDouble("armor_behavior.damage_reduciton.netherite_armor", 0.0);
        durabilityDay = config.getBoolean("armor_behavior.durability.day", true);
        durabilityNight = config.getBoolean("armor_behavior.durability.night", false);
        durabilityWeather = config.getBoolean("armor_behavior.durability.weather", false);

        // effect
        effectDuration = config.getInt("effect_duration", 5);

        // logging
        loggingEnabled = config.getBoolean("logging.enabled", false);
        logToConsole = config.getBoolean("logging.log_to_console", true);
        logToChat = config.getBoolean("logging.log_to_chat", false);

        // exempt players
        List<String> loadedExemptPlayers = config.getStringList("exempt_players");
        exemptPlayers = new HashSet<>(loadedExemptPlayers);

        // enabled worlds
        List<String> loadedEnabledWorlds = config.getStringList("enabled_worlds");
        enabledWorlds = new HashSet<>(loadedEnabledWorlds);
    }

    public int getEffectDurationInTicks() {
        return effectDuration * 20;
    }

    public double getArmorDamageReduction(String armorName) {
        return switch (armorName) {
            case "leather" -> leatherArmor;
            case "iron" -> ironArmor;
            case "gold" -> goldArmor;
            case "diamond" -> diamondArmor;
            case "netherite" -> netheriteArmor;
            default -> 0.0;
        };
    }

    public Set<String> getExemptPlayers() {
        return exemptPlayers;
    }

    public boolean worldEnabled(String name) {
        return enabledWorlds.contains(name);
    }
}
