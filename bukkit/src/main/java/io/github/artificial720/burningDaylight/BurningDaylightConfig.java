package io.github.artificial720.burningDaylight;

import org.bukkit.configuration.file.FileConfiguration;

public class BurningDaylightConfig {
    public int gracePeriodDuration; // in seconds
    public int effectDuration; // in seconds
    public double burnDamageDay;
    public double burnDamageNight;
    public double burnDamageWeather;
    public double burnDamageDayWithLeatherArmor;
    public double burnDamageNightWithLeatherArmor;
    public double burnDamageWeatherWithLeatherArmor;
    public boolean gracePeriodNotifyPlayer;
    public boolean applyOnFirstJoin;
    public boolean applyOnRespawn;
    public boolean preventInNether;
    public boolean preventInEnd;
    public boolean preventWithFireResistance;
    public String gracePeriodStartMsg;
    public String gracePeriodEndMsg;

    public BurningDaylightConfig(FileConfiguration config) {
        loadConfigValues(config);
    }

    public void loadConfigValues(FileConfiguration config) {
        // grace period
        gracePeriodDuration = config.getInt("grace_period.duration", 300);
        gracePeriodNotifyPlayer = config.getBoolean("grace_period.notify_player", true);
        applyOnFirstJoin = config.getBoolean("grace_period.apply_on.first_join", true);
        applyOnRespawn = config.getBoolean("grace_period.apply_on.respawn", false);
        gracePeriodStartMsg = config.getString("grace_period.message.start", "<green>You have a 5-minute grace period where you are immune to sun damage. Good Luck!");
        gracePeriodEndMsg = config.getString("grace_period.message.end", "<red>Your grace period has ended. You can now take damage.");

        // effect
        effectDuration = config.getInt("effect_duration", 5);

        // damage numbers
        burnDamageDay = config.getDouble("burn_damage.day.default", 2.0);
        burnDamageNight = config.getDouble("burn_damage.night.default", 1.0);
        burnDamageWeather = config.getDouble("burn_damage.weather.default", 1.0);
        burnDamageDayWithLeatherArmor = config.getDouble("burn_damage.day.leather_armor", 1.0);
        burnDamageNightWithLeatherArmor = config.getDouble("burn_damage.night.leather_armor", 0.0);
        burnDamageWeatherWithLeatherArmor = config.getDouble("burn_damage.weather.leather_armor");

        // prevent damage
        preventInNether = config.getBoolean("damage_prevention.nether", true);
        preventInEnd = config.getBoolean("damage_prevention.end", true);
        preventWithFireResistance = config.getBoolean("damage_prevention.fire_resistance_potion", true);
    }

    public int getEffectDurationInTicks() {
        return effectDuration * 20;
    }
}
