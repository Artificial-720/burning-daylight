package io.github.artificial720.burningDaylight;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class BurningDaylight extends JavaPlugin implements Listener {
    private static final Map<Player, Integer> affectedPlayers = new HashMap<>();
    private static final int PERIOD = 20; // 20 ticks = 1 second
    private static final int DURATION_TICKS = 100;
    private static final double BURN_DAMAGE_AMOUNT = 2.0;


    @Override
    public void onEnable() {
        saveDefaultConfig();

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
        if (ticks >= DURATION_TICKS) {
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
        ItemStack chestplate = equipment.getChestplate();
        ItemStack leggings = equipment.getLeggings();
        ItemStack boots = equipment.getBoots();

        // Check if all armor items are not null and are made of leather
        if (helmet != null && chestplate != null && leggings != null && boots != null &&
                helmet.getType() == Material.LEATHER_HELMET &&
                chestplate.getType() == Material.LEATHER_CHESTPLATE &&
                leggings.getType() == Material.LEATHER_LEGGINGS &&
                boots.getType() == Material.LEATHER_BOOTS) {
            getLogger().info("Wherein full leather armor half damage");
            wearingLeather = true;
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
                    return 0.0;
                }
                return 1.0;
            } else {
                return 0.0;
            }
        }
        if (isDay) {
            if (hasWeather) {
                return 1.0;
            }
            return 2.0;
        } else {
            return 1.0;
        }
    }

    private boolean isBurnConditionMet(Player player) {
        if (player.isDead()) return false;
        if (player.getGameMode().compareTo(GameMode.CREATIVE) == 0) return false;

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
                getLogger().info("Raytrace hit a block");
                return false;
            }
        }

//        for (int y = location.getBlockY() + 1; y <= world.getMaxHeight(); y++) {
//            if (!location.clone().add(0, y - location.getBlockY(), 0).getBlock().isEmpty()) {
//                getLogger().info("There is a block above head");
//                return false;
//            }
//        }

        return true;
    }
}
