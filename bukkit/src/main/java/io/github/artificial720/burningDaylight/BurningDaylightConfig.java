package io.github.artificial720.burningDaylight;

import org.bukkit.configuration.file.FileConfiguration;

public class BurningDaylightConfig {
    public int gracePeriodDuration; // in seconds
    public int effectDurationTicks; // in ticks
    public double burnDamageDay;
    public double burnDamageNight;
    public double burnDamageDayWithLeatherArmor;
    public double burnDamageNightWithLeatherArmor;
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
        gracePeriodDuration = config.getInt("grace_period.duration", 300); // Default: 300 seconds
        gracePeriodNotifyPlayer = config.getBoolean("grace_period.notify_player", true);
        applyOnFirstJoin = config.getBoolean("grace_period.apply_on.first_join", true);
        applyOnRespawn = config.getBoolean("grace_period.apply_on.respawn", false);
        gracePeriodStartMsg = config.getString("grace_period.message.start", "<green>You have a 5-minute grace period where you are immune to sun damage. Good Luck!");
        gracePeriodEndMsg = config.getString("grace_period.message.end", "<red>Your grace period has ended. You can now take damage.");

        // effect
        effectDurationTicks = config.getInt("effect_duration_ticks", 100); // Default: 100 ticks

        // damage numbers
        burnDamageDay = config.getDouble("burn_damage_amounts.day", 2.0); // Default: 2.0
        burnDamageNight = config.getDouble("burn_damage_amounts.night", 1.0); // Default: 1.0
        burnDamageDayWithLeatherArmor = config.getDouble("burn_damage_amounts.day_with_leather_armor", 1.0); // Default: 1.0
        burnDamageNightWithLeatherArmor = config.getDouble("burn_damage_amounts.night_with_leather_armor", 0.0); // Default: 0.0

        // prevent damage
        preventInNether = config.getBoolean("damage_prevention.nether", true);
        preventInEnd = config.getBoolean("damage_prevention.end", true);
        preventWithFireResistance = config.getBoolean("damage_prevention.fire_resistance_potion", true);
    }
}
