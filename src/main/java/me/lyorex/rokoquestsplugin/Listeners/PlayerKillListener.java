package me.lyorex.rokoquestsplugin.Listeners;

import me.lyorex.rokoquestsplugin.QuestClasses.KillQuest;
import me.lyorex.rokoquestsplugin.QuestClasses.QuestType;
import me.lyorex.rokoquestsplugin.RokoQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class PlayerKillListener implements Listener {

    private final RokoQuestsPlugin plugin = RokoQuestsPlugin.getPlugin();

    @EventHandler
    public void onPlayerKill(EntityDeathEvent e) {
        if(e.getEntity().getKiller() == null)
            return;
        Player player = e.getEntity().getKiller();

        if(plugin.playerQuestMap.get(player.getUniqueId()).getQuestType().equals(QuestType.KILL)) {
            KillQuest killQuest = (KillQuest) plugin.playerQuestMap.get(player.getUniqueId());
            if(killQuest.isFinished()) {
                return;
            }
            if(killQuest.getQuestEntity().equals(e.getEntityType())) {
                killQuest.incrementQuestProgress(1);
                killQuest.updateFinished();
                if(killQuest.isFinished()) {
                    player.sendMessage(ChatColor.GREEN + "Congratulations! You have completed your current quest!");
                }
            }
        }
    }
}
