package io.github.artificial720.burningDaylight.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    boolean execute(CommandSender sender, String[] args);

    List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args);

    boolean hasPermission(CommandSender sender);
}
