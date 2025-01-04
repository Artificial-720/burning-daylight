package io.github.artificial720.burningDaylight;

import org.bukkit.plugin.java.JavaPlugin;

public final class BurningDaylight extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getLogger().info("The Burning Daylight Plugin has been enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("The Burning Daylight Plugin has been disabled");
    }
}
