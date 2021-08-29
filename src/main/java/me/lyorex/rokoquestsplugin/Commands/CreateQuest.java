package me.lyorex.rokoquestsplugin.Commands;

import me.lyorex.rokoquestsplugin.RokoQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreateQuest implements CommandExecutor {

    private final RokoQuestsPlugin plugin = RokoQuestsPlugin.getPlugin();

    // /createquest
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            RokoQuestsPlugin.sendMustBePlayerMessage(sender);
            return true;
        }
        if(!sender.hasPermission("rokoquests.createquest")) {
            RokoQuestsPlugin.sendPermissionMessage(sender);
            return true;
        }
        Player player = (Player) sender;

        plugin.showCreateQuestGUI(player);

        return true;
    }

    public static ItemStack getFindItem() {
        ItemStack typeItem = new ItemStack(Material.SPYGLASS, 1);
        ItemMeta typeMeta = typeItem.getItemMeta();
        typeMeta.setDisplayName(ChatColor.RED + "FIND");
        typeItem.setItemMeta(typeMeta);

        return typeItem;
    }

    public static ItemStack getKillItem() {
        ItemStack typeItem = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta typeMeta = typeItem.getItemMeta();
        typeMeta.setDisplayName(ChatColor.RED + "KILL");
        typeItem.setItemMeta(typeMeta);

        return typeItem;
    }

}
