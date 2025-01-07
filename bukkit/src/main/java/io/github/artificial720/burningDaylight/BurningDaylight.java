package io.github.artificial720.burningDaylight;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public final class BurningDaylight extends JavaPlugin implements Listener {
    private static final int PERIOD = 20; // 20 ticks = 1 second
    private static final Map<Player, Integer> affectedPlayers = new HashMap<>();
    private final Set<UUID> playerGracePeriod = new HashSet<>();
    private int gracePeriodDuration; // in seconds
    private int effectDurationTicks; // in ticks
    private double burnDamageDay;
    private double burnDamageNight;
    private double burnDamageDayWithLeatherArmor;
    private double burnDamageNightWithLeatherArmor;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();

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
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(playerID);

        Component component = MiniMessage.miniMessage().deserialize("""
                <gold><bold>Welcome to Burning Daylight!</bold></gold>
                
                <white><bold>Core Mechanics:</bold></white>
                <red>  - Sunburn Damage:</red> <white>Direct sunlight will damage you, so stay in the shade or underground!</white>
                <blue>  - Nighttime Safety:</blue> <white>You'll take less damage at night, so use it to your advantage.</white>
                <dark_aqua>  - Weather Protection:</dark_aqua> <white>Rainy or stormy weather also reduces sunlight damage—watch the skies!</white>
                <green>  - Special Sun Gear:</green> <white>Leather armor isn't just for style; it protects you from the sun's harsh rays.</white>""");
        player.sendMessage(component);

        if (!offlinePlayer.hasPlayedBefore()) {
            player.sendMessage("Welcome to the server for the first time!");
            player.sendMessage(ChatColor.GREEN + "You have " + (gracePeriodDuration / 60) + "-minute grace period where you are immune to sun damage. Good Luck!");
            playerGracePeriod.add(playerID);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (playerGracePeriod.contains(playerID)) {
                        playerGracePeriod.remove(playerID);
                        player.sendMessage(ChatColor.RED + "Your grace period has ended. You can now take damage.");
                    }
                }
            }.runTaskLater(this, 20L * gracePeriodDuration);

        }
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();

        // Load basic settings
        gracePeriodDuration = config.getInt("grace_period_duration", 300); // Default: 300 seconds
        effectDurationTicks = config.getInt("effect_duration_ticks", 100); // Default: 100 ticks

        // Load nested damage settings
        burnDamageDay = config.getDouble("burn_damage_amounts.day", 2.0); // Default: 2.0
        burnDamageNight = config.getDouble("burn_damage_amounts.night", 1.0); // Default: 1.0
        burnDamageDayWithLeatherArmor = config.getDouble("burn_damage_amounts.day_with_leather_armor", 1.0); // Default: 1.0
        burnDamageNightWithLeatherArmor = config.getDouble("burn_damage_amounts.night_with_leather_armor", 0.0); // Default: 0.0
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
        if (ticks >= effectDurationTicks) {
            getLogger().info("Its been " + ticks + " ticks removing effect");
            return true;
        }
        affectedPlayers.put(player, ticks);
        return !player.isOnline() || player.isDead();
    }

    private void applyBurnEffect(Player player) {
        double damage = calculateBurnDamage(player);
        getLogger().info("Applying damage to player " + damage);
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
            getLogger().info("Wherein full leather armor half damage");
            wearingLeather = true;

            // damage the armor
            damageArmor(helmet, 1);
            damageArmor(chestPlate, 1);
            damageArmor(leggings, 1);
            damageArmor(boots, 1);
        }

        // 2 damage during day
        // 1 damage during night
        // 1 damage during day with leather armor
        // 0 damage during night with leather armor

        getLogger().info("wearingLeather: " + wearingLeather);
        getLogger().info("isDay: " + isDay);
        getLogger().info("hasWeather: " + hasWeather);
        if (wearingLeather) {
            if (isDay) {
                if (hasWeather) {
                    return burnDamageNightWithLeatherArmor;
                }
                return burnDamageDayWithLeatherArmor;
            } else {
                return burnDamageNightWithLeatherArmor;
            }
        }
        if (isDay) {
            if (hasWeather) {
                return burnDamageNight;
            }
            return burnDamageDay;
        } else {
            return burnDamageNight;
        }
    }

    private void damageArmor(ItemStack armor, int damage) {
        ItemMeta meta = armor.getItemMeta();
        if (meta instanceof Damageable damageable) {
            int currentDamage = damageable.getDamage();
            int newDamage = currentDamage + damage;

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
        if (playerGracePeriod.contains(player.getUniqueId())) return false;

        Location location = player.getLocation();
        World world = location.getWorld();

        if (world == null) {
            getLogger().info("World is null");
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
            if (rayTraceResult.getHitBlock() != null) {
                return false;
            }
        }

        return true;
    }
}
