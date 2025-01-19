package io.github.artificial720.burningDaylight.commands;

import io.github.artificial720.burningDaylight.BurningDaylight;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public class UnExemptCommand implements SubCommand {
    private final BurningDaylight plugin;

    public UnExemptCommand(BurningDaylight plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /burn unexempt <player>");
            return false;
        }

        String playerName = args[1];
        if (!plugin.getExemptPlayers().contains(playerName)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("Player " + playerName + " is not exempt from burning."));
            return true;
        }

        plugin.removeExemptPlayer(playerName);
        sender.sendMessage(MiniMessage.miniMessage().deserialize("Player " + playerName + " is no longer exempt from burning."));
        plugin.getLogger().info("Removed " + playerName + " from the exempt list.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            String part = args[1].toLowerCase();
            return plugin.getServer().getOnlinePlayers().stream().map(HumanEntity::getName)
                    .filter(name -> name.toLowerCase().startsWith(part))
                    .toList();
        }
        return List.of();
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("burn.unexempt");
    }
}

