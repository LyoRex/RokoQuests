package me.lyorex.rokoquestsplugin.Commands;

import me.lyorex.rokoquestsplugin.RokoQuestsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MyQuest implements CommandExecutor, TabCompleter {

    private final RokoQuestsPlugin plugin = RokoQuestsPlugin.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))
            return true;
        Player player = (Player) sender;

        plugin.showQuestGUI(player);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
