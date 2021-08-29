package me.lyorex.rokoquestsplugin.Commands;

import me.lyorex.rokoquestsplugin.RokoQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListPlayerQuests implements CommandExecutor, TabCompleter {

    private final RokoQuestsPlugin plugin = RokoQuestsPlugin.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("rokoquests.listplayers")) {
            RokoQuestsPlugin.sendPermissionMessage(sender);
            return true;
        }

        List<Player> players = plugin.registeredPlayers;

        for(Player player : players) {
            if(plugin.playerQuestMap.get(player.getUniqueId()) == null) {
                sender.sendMessage(ChatColor.YELLOW + player.getDisplayName() + ": " + ChatColor.RED + "Quest is null!");
            }
            else {
                String msg = ChatColor.YELLOW + player.getDisplayName() + ": " + ChatColor.RED + (plugin.playerQuestMap.get(player.getUniqueId()).getQuestDetails());
                sender.sendMessage(msg);
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}

