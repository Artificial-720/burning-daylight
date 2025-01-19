package io.github.artificial720.burningDaylight.commands;

import io.github.artificial720.burningDaylight.BurningDaylight;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.List;

public class ExemptCommand implements SubCommand {
    private final BurningDaylight plugin;

    public ExemptCommand(BurningDaylight plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /burn exempt <player>");
            return false;
        }

        String playerName = args[1];
        if (plugin.getExemptPlayers().contains(playerName)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("Player " + playerName + " is already exempt from burning."));
            return true;
        }

        plugin.addExemptPlayer(playerName);
        sender.sendMessage(MiniMessage.miniMessage().deserialize("Player " + playerName + " is now exempt from burning."));
        plugin.log("Added " + playerName + " to the exempt list.");
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
        return sender.hasPermission("burn.exempt");
    }
}
