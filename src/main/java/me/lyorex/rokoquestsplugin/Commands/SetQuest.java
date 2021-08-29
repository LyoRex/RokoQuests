package me.lyorex.rokoquestsplugin.Commands;

import me.lyorex.rokoquestsplugin.QuestClasses.FindQuest;
import me.lyorex.rokoquestsplugin.QuestClasses.KillQuest;
import me.lyorex.rokoquestsplugin.QuestClasses.Quest;
import me.lyorex.rokoquestsplugin.QuestClasses.QuestType;
import me.lyorex.rokoquestsplugin.RokoQuestsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SetQuest implements CommandExecutor, TabCompleter {

    private final RokoQuestsPlugin plugin = RokoQuestsPlugin.getPlugin();

    /*
            /setquest [type] [target] [amount] [
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 1) {
            return false;
        }
        else if(args.length == 2) {
            Quest quest;
            if(args[1].equals("random")) {
                Random rand = new Random();
                if(rand.nextInt(2) == 0)
                    quest = FindQuest.createRandomQuest();
                else quest = KillQuest.createRandomQuest();
            }
            else return false;
        }

        if(args.length == 4 || args.length == 5) {
            Quest quest;

            int questAmt;
            if(!isParsedInt(args[3])) return false;
            questAmt = Integer.parseInt(args[3]);

            int questProgress = 0;
            if(args.length == 5) {
                if (!isParsedInt(args[4])) return false;
                questProgress = Integer.parseInt(args[4]);
            }

            if(args[1].equals("kill")) {
                try {
                    EntityType questEntity = EntityType.valueOf(args[2].toUpperCase());
                    quest = new KillQuest(questEntity, questAmt);
                } catch(IllegalArgumentException exception) {
                    Bukkit.getLogger().info(exception.getMessage());
                    return false;
                }
            }
            else if(args[1].equals("find")) {
                try {
                    Material questItem = Material.valueOf(args[2].toUpperCase());
                    quest = new FindQuest(questItem, questAmt);
                } catch(IllegalArgumentException exception) {
                    Bukkit.getLogger().info(exception.getMessage());
                    return false;
                }
            }
            else return false;

            if(args[0].equals("all")) {
                plugin.isUniversalQuest = true;
                plugin.universalQuest = quest;
                for(Player player : plugin.registeredPlayers) {
                    if(player.getOpenInventory().getTitle().contains(plugin.QUEST_GUI_NAME)) {
                        player.closeInventory();
                    }
                    if(quest.getQuestType().equals(QuestType.FIND)) {
                        plugin.setQuest(player, plugin.universalQuest.copy());
                    }
                    else if(quest.getQuestType().equals(QuestType.KILL)) {
                        plugin.setQuest(player, plugin.universalQuest.copy());
                    }
                }
                return true;
            }

            Player player = Bukkit.getPlayerExact(args[0]);
            if(player == null)
                return false;
            if(player.getOpenInventory().getTitle().contains(plugin.QUEST_GUI_NAME)) {
                player.closeInventory();
            }
            plugin.setQuest(player, quest);
            return true;
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            List<String> fillers = new ArrayList<>();
            fillers.add("all");
            for(Player player : plugin.registeredPlayers) {
                fillers.add(player.getDisplayName());
            }
            return RokoQuestsPlugin.getMatchingTabCompleteArgs(fillers, args[0]);
        }
        else if(args.length == 2) {
            List<String> fillers = new ArrayList<>();
            fillers.add("kill");
            fillers.add("find");
            return RokoQuestsPlugin.getMatchingTabCompleteArgs(fillers, args[1]);
        }
        else if(args.length == 3) {
            List<String> fillers = new ArrayList<>();
            if(args[1].equals("kill")) {
                for(EntityType et : EntityType.values()) {
                    fillers.add(et.name().toLowerCase());
                }
                return RokoQuestsPlugin.getMatchingTabCompleteArgs(fillers, args[2]);
            }
            else if(args[1].equals("find")) {
                for(Material mat: Material.values()) {
                    fillers.add(mat.name().toLowerCase());
                }
                return RokoQuestsPlugin.getMatchingTabCompleteArgs(fillers, args[2]);
            }
        }
        return new ArrayList<>();
    }

    // check if string is int
    public boolean isParsedInt(String s) {
        try {
            Integer.parseInt(s);
        } catch(Exception e) {
            return false;
        }
        return true;
    }
}
