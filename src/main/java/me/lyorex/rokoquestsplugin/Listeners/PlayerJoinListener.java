package me.lyorex.rokoquestsplugin.Listeners;

import me.lyorex.rokoquestsplugin.RokoQuestsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final RokoQuestsPlugin plugin = RokoQuestsPlugin.getPlugin();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        boolean playerInActivePlayers = false;
        for(Player p : plugin.registeredPlayers){
            if(p.getUniqueId().equals(player.getUniqueId())) {
                playerInActivePlayers = true;
            }
        }
        if(!playerInActivePlayers) {
            plugin.addPlayer(player);
        }
        plugin.setPlayerDicts(player);
    }
}
