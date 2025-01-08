package io.github.artificial720.burningDaylight.commands;

import io.github.artificial720.burningDaylight.BurningDaylight;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements SubCommand{
    private final BurningDaylight plugin;

    public ReloadCommand(BurningDaylight plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        plugin.reloadConfigValues();
        sender.sendMessage(Component.text("Configuration reloaded successfully.").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("burn.reload");
    }
}
