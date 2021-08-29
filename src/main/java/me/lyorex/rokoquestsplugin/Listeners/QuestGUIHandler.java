package me.lyorex.rokoquestsplugin.Listeners;

import me.lyorex.rokoquestsplugin.QuestClasses.FindQuest;
import me.lyorex.rokoquestsplugin.QuestClasses.Quest;
import me.lyorex.rokoquestsplugin.QuestClasses.QuestType;
import me.lyorex.rokoquestsplugin.RokoQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestGUIHandler implements Listener {

    private final RokoQuestsPlugin plugin = RokoQuestsPlugin.getPlugin();

    @EventHandler
    public void onQuestGUIClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        // CHECK IF PLAYER IS VIEWING "MYQUEST" GUI
        if(e.getView().getTitle().contains(plugin.QUEST_GUI_NAME)) {
            if (e.getClickedInventory() == plugin.playerQuestGUIMap.get(player.getUniqueId())) {
                Quest playerQuest = plugin.playerQuestMap.get(player.getUniqueId());
                // CANCEL EVENT IF CLICKED ANY SLOT BESIDES THE ITEM SUBMISSION SLOT
                if (e.getSlot() != 4) {
                    e.setCancelled(true);
                }
                // OPEN REWARD GUI IF QUEST IS FINISHED
                if (e.getSlot() == 1) {
                    if (playerQuest.isFinished()) {
                        player.openInventory(plugin.getRewardGUI(player, playerQuest.getQuestDifficulty()));
                    }
                } else if (e.getSlot() == 5) {
                    Inventory questGUI = plugin.playerQuestGUIMap.get(player.getUniqueId());
                    if (!(playerQuest.getQuestType().equals(QuestType.FIND))) return;
                    Material questItem = ((FindQuest) playerQuest).getQuestItem();

                    ItemStack submitItem;
                    try {
                        submitItem = e.getClickedInventory().getItem(4);
                        submitItem.getType();
                    } catch (NullPointerException exception) {
                        return;
                    }

                    if (!playerQuest.isFinished() && submitItem.getType().equals(questItem)) {
                        int numSubmit = submitItem.getAmount();
                        int amtLeft = playerQuest.getQuestAmount() - playerQuest.getQuestProgress();

                        if (numSubmit <= amtLeft) {
                            playerQuest.incrementQuestProgress(numSubmit);
                            questGUI.clear(4);
                        } else {
                            playerQuest.incrementQuestProgress(amtLeft);
                            int newAmt = numSubmit - amtLeft;
                            submitItem.setAmount(newAmt);
                            HashMap<Integer, ItemStack> extraItems = player.getInventory().addItem(submitItem);
                            for (ItemStack is : extraItems.values()) {
                                player.getWorld().dropItemNaturally(player.getLocation(), is);
                            }
                            questGUI.clear(4);
                        }
                        playerQuest.updateFinished();

                        if (playerQuest.isFinished()) {
                            player.sendMessage(ChatColor.GREEN + "Congratulations! You have completed your current quest!");
                        }

                        plugin.showQuestGUI(player);
                    }
                } else if (e.getSlot() == 8) {
                    player.closeInventory();
                }
            }
        }
        else if(e.getView().getTitle().contains(plugin.REWARD_GUI_NAME)) {
            Inventory rewardGUI = plugin.playerRewardGUIMap.get(player.getUniqueId());
            e.setCancelled(true);
            int slot = e.getSlot();
            if(slot == 17) {
                plugin.showQuestGUI(player);
            }
            if(rewardGUI.getItem(0).getItemMeta().getDisplayName().contains("Unclaimed")) {
                if (slot == 8) {
                    if (rewardGUI.contains(Material.GREEN_WOOL, 2) && !rewardGUI.contains(Material.GREEN_WOOL, 3)) {
                        ArrayList<ItemStack> extraItems = new ArrayList<>();
                        if (rewardGUI.getItem(11).getType().equals(Material.GREEN_WOOL))
                            extraItems.addAll(player.getInventory().addItem(rewardGUI.getItem(2)).values());
                        if (rewardGUI.getItem(13).getType().equals(Material.GREEN_WOOL))
                            extraItems.addAll(player.getInventory().addItem(rewardGUI.getItem(4)).values());
                        if (rewardGUI.getItem(15).getType().equals(Material.GREEN_WOOL))
                            extraItems.addAll(player.getInventory().addItem(rewardGUI.getItem(6)).values());

                        for (ItemStack is : extraItems) {
                            player.getWorld().dropItemNaturally(player.getLocation(), is);
                        }

                        ItemMeta infoMeta = rewardGUI.getItem(0).getItemMeta();
                        infoMeta.setDisplayName(ChatColor.GREEN + "Claimed");
                        rewardGUI.getItem(0).setItemMeta(infoMeta);
                    }
                }
                else if (slot == 11 || slot == 13 || slot == 15) {
                    if (rewardGUI.getItem(slot).getType().equals(Material.RED_WOOL))
                        rewardGUI.setItem(slot, new ItemStack(Material.GREEN_WOOL, 1));
                    else if (rewardGUI.getItem(slot).getType().equals(Material.GREEN_WOOL))
                        rewardGUI.setItem(slot, new ItemStack(Material.RED_WOOL, 1));
                }
            }
        }
    }

    @EventHandler
    public void onQuestGUIClick(InventoryCloseEvent e) {
        if(!e.getView().getTitle().contains(plugin.QUEST_GUI_NAME)) {
            return;
        }
        Player player = (Player) e.getPlayer();
        ItemStack submitItem;
        try {
            submitItem = e.getInventory().getItem(4);
            submitItem.getType();
        }
        catch(NullPointerException except) {
            return;
        }
        HashMap<Integer, ItemStack> extraItems = player.getInventory().addItem(submitItem);
        for(ItemStack is : extraItems.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), is);
        }
        e.getInventory().clear(4);
    }
}
