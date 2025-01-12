package io.github.artificial720.burningDaylight;

import io.github.artificial720.burningDaylight.commands.BurningDaylightCommander;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public final class BurningDaylight extends JavaPlugin implements Listener {
    private static final int PERIOD = 20; // 20 ticks = 1 second
    private static final Map<Player, Integer> affectedPlayers = new HashMap<>();
    private final Map<UUID, BukkitTask> playerGracePeriod = new HashMap<>();
    // config.yml settings
    private BurningDaylightConfig config;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = new BurningDaylightConfig(getConfig());

        BurningDaylightCommander commander = new BurningDaylightCommander(this);
        PluginCommand pluginCommand = getCommand("burn");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commander);
            pluginCommand.setTabCompleter(commander);
        }

        getServer().getPluginManager().registerEvents(this, this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isBurnConditionMet(player)) {
                        startBurnEffect(player);
                    }
                    if (affectedPlayers.containsKey(player)) {
                        applyBurnEffect(player);
                        if (shouldStopBurnEffect(player)) {
                            removeBurnEffect(player);
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, PERIOD);

        getLogger().info("The Burning Daylight Plugin has been enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("The Burning Daylight Plugin has been disabled");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getLastDamageCause() == null) return;
        EntityDamageEvent.DamageCause damageCause = player.getLastDamageCause().getCause();

        if (damageCause == EntityDamageEvent.DamageCause.CUSTOM) {
            Component message = getRandomDeathMessage(player);
            event.deathMessage(message);
        }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (config.applyOnRespawn) {
            applyGracePeriod(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(player.getUniqueId());

        Component component = MiniMessage.miniMessage().deserialize("""
                <gold><bold>Welcome to Burning Daylight!</bold></gold>
                
                <white><bold>Core Mechanics:</bold></white>
                <red>  - Sunburn Damage:</red> <white>Direct sunlight will damage you, so stay in the shade or underground!</white>
                <blue>  - Nighttime Safety:</blue> <white>You'll take less damage at night, so use it to your advantage.</white>
                <dark_aqua>  - Weather Protection:</dark_aqua> <white>Rainy or stormy weather also reduces sunlight damage—watch the skies!</white>
                <green>  - Special Sun Gear:</green> <white>Leather armor isn't just for style; it protects you from the sun's harsh rays.</white>""");
        player.sendMessage(component);

        boolean isFirstJoin = !offlinePlayer.hasPlayedBefore();


        if (isFirstJoin){
            player.sendMessage("Welcome to the server for the first time!");
            if (config.applyOnFirstJoin) {
                applyGracePeriod(player);
            }
        }
    }

    private void applyGracePeriod(Player player) {
        UUID playerID = player.getUniqueId();

        // If already have a grace period remove that first before refreshing
        if (playerGracePeriod.containsKey(playerID)) {
            playerGracePeriod.get(playerID).cancel();
        }

        if (config.gracePeriodNotifyPlayer) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(config.gracePeriodStartMsg));
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (playerGracePeriod.containsKey(playerID)) {
                    playerGracePeriod.remove(playerID);
                    if (config.gracePeriodNotifyPlayer) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(config.gracePeriodEndMsg));
                    }
                }
            }
        }.runTaskLater(this, 20L * config.gracePeriodDuration);

        playerGracePeriod.put(playerID, task);
    }

    public void reloadConfigValues() {
        reloadConfig();
        config.loadConfigValues(getConfig());
    }

    public Component getRandomDeathMessage(Player player) {
        String[] deathMessages = {
                player.getName() + " was scorched by the harsh sunlight!",
                player.getName() + " couldn't handle the burning rays of the sun!",
                player.getName() + " was roasted by the unforgiving daylight!",
                player.getName() + " perished in the blazing sunlight!",
                player.getName() + " was burned alive by the sun's wrath!",
                player.getName() + " turned to ash under the scorching sun!",
                player.getName() + " was incinerated by the sun's unrelenting heat!",
                player.getName() + " couldn't escape the fiery gaze of the sun!",
                player.getName() + " met their end in the merciless daylight!",
                player.getName() + " burned to a crisp under the hot sun!"
        };

        Random random = new Random();
        String randomMessage = deathMessages[random.nextInt(deathMessages.length)];

        return Component.text(randomMessage);
    }

    public void startBurnEffect(Player player) {
        affectedPlayers.put(player, 0);
    }

    public void removeBurnEffect(Player player) {
        affectedPlayers.remove(player);
    }

    private boolean shouldStopBurnEffect(Player player) {
        if (!affectedPlayers.containsKey(player)) return true;

        int ticks = affectedPlayers.get(player);
        ticks += PERIOD;
        // stop effect after x ticks
        if (ticks >= config.getEffectDurationInTicks()) {
            getLogger().info("Its been " + ticks + " ticks removing effect from " + player.getName());
            return true;
        }
        affectedPlayers.put(player, ticks);
        return !player.isOnline() || player.isDead();
    }

    private void applyBurnEffect(Player player) {
        double damage = calculateBurnDamage(player);
        player.damage(damage);
        player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 0.5f, 1f);
    }

    private double calculateBurnDamage(Player player) {
        World world = player.getWorld();
        boolean isDay = world.isDayTime();
        boolean hasWeather = world.hasStorm() || world.isThundering();
        boolean wearingLeather = false;

        // Full armor = half damage
        EntityEquipment equipment = player.getEquipment();
        ItemStack helmet = equipment.getHelmet();
        ItemStack chestPlate = equipment.getChestplate();
        ItemStack leggings = equipment.getLeggings();
        ItemStack boots = equipment.getBoots();

        // Check if all armor items are not null and are made of leather
        if (helmet != null && chestPlate != null && leggings != null && boots != null &&
                helmet.getType() == Material.LEATHER_HELMET &&
                chestPlate.getType() == Material.LEATHER_CHESTPLATE &&
                leggings.getType() == Material.LEATHER_LEGGINGS &&
                boots.getType() == Material.LEATHER_BOOTS) {
            wearingLeather = true;

            // damage the armor
            damageArmor(helmet);
            damageArmor(chestPlate);
            damageArmor(leggings);
            damageArmor(boots);
        }

        // 2 damage during day
        // 1 damage during night
        // 1 damage during day with leather armor
        // 0 damage during night with leather armor
        if (wearingLeather) {
            if (isDay) {
                if (hasWeather) {
                    return config.burnDamageWeatherWithLeatherArmor;
                }
                return config.burnDamageDayWithLeatherArmor;
            } else {
                return config.burnDamageNightWithLeatherArmor;
            }
        }
        if (isDay) {
            if (hasWeather) {
                return config.burnDamageWeather;
            }
            return config.burnDamageDay;
        } else {
            return config.burnDamageNight;
        }
    }

    private void damageArmor(ItemStack armor) {
        ItemMeta meta = armor.getItemMeta();
        if (meta instanceof Damageable damageable) {
            int currentDamage = damageable.getDamage();
            int newDamage = currentDamage + 1;

            if (newDamage >= armor.getType().getMaxDurability()) {
                armor.setAmount(0); // Break armor
            } else {
                // set new durability
                damageable.setDamage(newDamage);
                armor.setItemMeta(meta);
            }
        }
    }

    private boolean isBurnConditionMet(Player player) {
        if (player.isDead()) return false;
        if (player.getGameMode().compareTo(GameMode.CREATIVE) == 0) return false;
        if (playerGracePeriod.containsKey(player.getUniqueId())) return false;

        Location location = player.getLocation();
        World world = location.getWorld();

        if (world == null) {
            getLogger().warning("World is null");
            return false;
        }

        World.Environment environment = world.getEnvironment();
        if ((environment == World.Environment.NETHER && config.preventInNether) ||
                (environment == World.Environment.THE_END && config.preventInEnd)) {
            return false;
        }

        if (config.preventWithFireResistance && player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            return false;
        }

        // Check for blocks above player
        RayTraceResult rayTraceResult = world.rayTraceBlocks(
                player.getEyeLocation(),
                new Vector(0, 1, 0),
                world.getMaxHeight() - location.getBlockY(),
                FluidCollisionMode.NEVER,
                true
        );
        if (rayTraceResult != null) {
            return rayTraceResult.getHitBlock() == null;
        }

        return true;
    }
}
