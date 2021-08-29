package me.lyorex.rokoquestsplugin.Commands;

import me.lyorex.rokoquestsplugin.RokoQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class RokoQuestsGeneral implements CommandExecutor, TabCompleter {
    private final RokoQuestsPlugin plugin = RokoQuestsPlugin.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("rokoquests.rokoquests")) {
            RokoQuestsPlugin.sendPermissionMessage(sender);
            return true;
        }
        if (args.length == 0) {
            // /rokoquests
            return false;
        }
        else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("rokoquests.reload")) {
                    RokoQuestsPlugin.sendPermissionMessage(sender);
                    return true;
                }
                plugin.saveConfig();
                plugin.loadConfigValues();
                String reloadMessage = plugin.getConfig().getString("reload-message");
                reloadMessage = reloadMessage != null ? reloadMessage : (ChatColor.GOLD + "Reloaded RokoQuests config file...");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', reloadMessage));
            }
            else if (args[0].equalsIgnoreCase("info")) {
                if (!sender.hasPermission("rokoquests.info")) {
                    RokoQuestsPlugin.sendPermissionMessage(sender);
                    return true;
                }
                String infoMessage = RokoQuestsPlugin.PLUGIN_INFO_MESSAGE;
                sender.sendMessage(infoMessage);
            }
            else return false;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            List<String> fillers = new ArrayList<>();
            fillers.add("info");
            fillers.add("reload");
            return RokoQuestsPlugin.getMatchingTabCompleteArgs(fillers, args[0]);
        }
        return new ArrayList<>();
    }
}
