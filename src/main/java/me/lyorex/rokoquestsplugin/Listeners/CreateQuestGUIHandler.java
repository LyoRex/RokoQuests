package me.lyorex.rokoquestsplugin.Listeners;

import me.lyorex.rokoquestsplugin.Commands.CreateQuest;
import me.lyorex.rokoquestsplugin.QuestClasses.FindQuest;
import me.lyorex.rokoquestsplugin.QuestClasses.KillQuest;
import me.lyorex.rokoquestsplugin.QuestClasses.Quest;
import me.lyorex.rokoquestsplugin.QuestClasses.QuestDifficulty;
import me.lyorex.rokoquestsplugin.RokoQuestsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Locale;

public class CreateQuestGUIHandler implements Listener {
    private final RokoQuestsPlugin plugin = RokoQuestsPlugin.getPlugin();

    @EventHandler
    public void onQuestGUIClick(InventoryClickEvent e) {
        String invTitle = e.getView().getTitle();
        Player player = (Player) e.getWhoClicked();
        if (invTitle.contains("Create a Quest")) {
            e.setCancelled(true);

            if(e.getClickedInventory() != plugin.createQuestGUIMap.get(player.getUniqueId())) return;

            if(e.getSlot() == 0) {
                ItemStack targetItem = plugin.createQuestGUIMap.get(player.getUniqueId()).getItem(2);
                ItemMeta targetMeta = targetItem.getItemMeta();
                ArrayList<String> targetLore = new ArrayList<>();
                if(e.getClickedInventory().getItem(0).getItemMeta().getDisplayName().contains("FIND")) {
                    e.getClickedInventory().setItem(0, CreateQuest.getKillItem());
                    targetLore.add(plugin.defaultQuestEntity.name());
                }
                else if(e.getClickedInventory().getItem(0).getItemMeta().getDisplayName().contains("KILL")) {
                    e.getClickedInventory().setItem(0, CreateQuest.getFindItem());
                    targetLore.add(plugin.defaultQuestItem.name());
                }
                targetMeta.setLore(targetLore);
                targetItem.setItemMeta(targetMeta);

                plugin.showCreateQuestGUI(player);
            }
            else if(e.getSlot() == 1) {
                player.openInventory(plugin.getPlayerGUI(player));
            }
            else if(e.getSlot() == 2) {
                if(e.getClickedInventory().getItem(0).getItemMeta().getDisplayName().contains("FIND"))
                    player.openInventory(plugin.getItemGUI(player));
                else if (e.getClickedInventory().getItem(0).getItemMeta().getDisplayName().contains("KILL"))
                    player.openInventory(plugin.getEntityGUI(player));
            }
            else if(e.getSlot() == 3) {
                ItemStack amountItem = plugin.createQuestGUIMap.get(player.getUniqueId()).getItem(3);
                ItemMeta amountMeta = amountItem.getItemMeta();
                String curAmt = amountMeta.getLore().get(0);
                ArrayList<String> amountLore = new ArrayList<>();

                int numAmts = plugin.questAmounts.size();
                if(numAmts < 1) return;
                for(int i = 0; i < numAmts; i++) {
                    if(curAmt.equals(plugin.questAmounts.get(i).toString())) {
                        if(i == numAmts - 1) {
                            amountLore.add(plugin.questAmounts.get(0).toString());
                        }
                        else {
                            amountLore.add(plugin.questAmounts.get(i + 1).toString());
                        }
                        break;
                    }
                }

                amountMeta.setLore(amountLore);
                amountItem.setItemMeta(amountMeta);
            }
            else if(e.getSlot() == 4) {
                ItemStack difficultyItem = plugin.createQuestGUIMap.get(player.getUniqueId()).getItem(4);
                ItemMeta difficultyMeta = difficultyItem.getItemMeta();
                String difficulty = difficultyMeta.getLore().get(0);

                if(difficulty.equals("EASY")) {
                    difficultyItem = new ItemStack(Material.CREEPER_HEAD, 1);
                    difficultyMeta = difficultyItem.getItemMeta();
                    difficultyMeta.setDisplayName(ChatColor.BLUE + "Difficulty:");
                    ArrayList<String> difficultyLore = new ArrayList<>();
                    difficultyLore.add("NORMAL");
                    difficultyMeta.setLore(difficultyLore);
                    difficultyItem.setItemMeta(difficultyMeta);
                }
                else if(difficulty.equals("NORMAL")) {
                    difficultyItem = new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
                    difficultyMeta = difficultyItem.getItemMeta();
                    difficultyMeta.setDisplayName(ChatColor.BLUE + "Difficulty:");
                    ArrayList<String> difficultyLore = new ArrayList<>();
                    difficultyLore.add("HARD");
                    difficultyMeta.setLore(difficultyLore);
                    difficultyItem.setItemMeta(difficultyMeta);
                }
                else if(difficulty.equals("HARD")) {
                    difficultyItem = new ItemStack(Material.DRAGON_HEAD, 1);
                    difficultyMeta = difficultyItem.getItemMeta();
                    difficultyMeta.setDisplayName(ChatColor.BLUE + "Difficulty:");
                    ArrayList<String> difficultyLore = new ArrayList<>();
                    difficultyLore.add("EXTREME");
                    difficultyMeta.setLore(difficultyLore);
                    difficultyItem.setItemMeta(difficultyMeta);
                }
                else if(difficulty.equals("EXTREME")) {
                    difficultyItem = new ItemStack(Material.ZOMBIE_HEAD, 1);
                    difficultyMeta = difficultyItem.getItemMeta();
                    difficultyMeta.setDisplayName(ChatColor.BLUE + "Difficulty:");
                    ArrayList<String> difficultyLore = new ArrayList<>();
                    difficultyLore.add("EASY");
                    difficultyMeta.setLore(difficultyLore);
                    difficultyItem.setItemMeta(difficultyMeta);
                }
                plugin.createQuestGUIMap.get(player.getUniqueId()).setItem(4, difficultyItem);
            }
            else if(e.getSlot() == 7) {
                Inventory gui = plugin.createQuestGUIMap.get(player.getUniqueId());
                Quest quest;
                String questType = gui.getItem(0).getItemMeta().getDisplayName().toUpperCase();
                int questAmt = Integer.parseInt(gui.getItem(3).getItemMeta().getLore().get(0));
                String questDiffString = gui.getItem(4).getItemMeta().getLore().get(0);
                QuestDifficulty questDifficulty = QuestDifficulty.EASY;
                if(questDiffString.contains("EASY")) questDifficulty = QuestDifficulty.EASY;
                else if(questDiffString.contains("NORMAL")) questDifficulty = QuestDifficulty.NORMAL;
                else if(questDiffString.contains("HARD")) questDifficulty = QuestDifficulty.HARD;
                else if(questDiffString.contains("EXTREME")) questDifficulty = QuestDifficulty.EXTREME;
                else return;
                if(questType.contains("FIND")) {
                    Material questItem = Material.valueOf(gui.getItem(2).getItemMeta().getLore().get(0).toUpperCase());
                    quest = new FindQuest(questItem, questAmt, questDifficulty);
                }
                else if(questType.contains("KILL")) {
                    EntityType questEntity = EntityType.valueOf(gui.getItem(2).getItemMeta().getLore().get(0).toUpperCase());
                    quest = new KillQuest(questEntity, questAmt, questDifficulty);
                }
                else return;
                String questPlayer = gui.getItem(1).getItemMeta().getLore().get(0).toUpperCase();
                if(questPlayer.contains("ALL")) {
                    plugin.isUniversalQuest = true;
                    plugin.universalQuest = quest;
                    for(Player p : plugin.registeredPlayers) {
                        plugin.setQuest(p, plugin.universalQuest.copy());
                    }
                }
                else {
                    Player p = (Player) ((SkullMeta)gui.getItem(1).getItemMeta()).getOwningPlayer();
                    plugin.setQuest(p, quest.copy());
                }

                plugin.createQuestGUIMap.remove(player.getUniqueId());
                player.closeInventory();
            }
            else if(e.getSlot() == 8) {
                plugin.createQuestGUIMap.remove(player.getUniqueId());
                player.closeInventory();
            }
        }
        else if(invTitle.contains("Choose a Player")) {
            e.setCancelled(true);

            if(e.getSlot() == 53) {
                plugin.showCreateQuestGUI(player);
            }
            else if(e.getSlot() == 45) {
                ItemStack playerHeadItem = new ItemStack(Material.PLAYER_HEAD, 1);
                ItemMeta playerHeadMeta = playerHeadItem.getItemMeta();
                playerHeadMeta.setDisplayName(ChatColor.BLUE + "Quest Given to:");
                ArrayList<String> playerHeadLore = new ArrayList<>();
                playerHeadLore.add("ALL");
                playerHeadMeta.setLore(playerHeadLore);
                playerHeadItem.setItemMeta(playerHeadMeta);

                plugin.createQuestGUIMap.get(player.getUniqueId()).setItem(1, playerHeadItem);

                plugin.showCreateQuestGUI(player);
            }
            else if(e.getCurrentItem() != null) {
                ItemStack playerHeadItem = plugin.createQuestGUIMap.get(player.getUniqueId()).getItem(1);
                SkullMeta playerHeadMeta = (SkullMeta) playerHeadItem.getItemMeta();
                playerHeadMeta.setOwningPlayer(((SkullMeta)e.getCurrentItem().getItemMeta()).getOwningPlayer());
                ArrayList<String> playerHeadLore = new ArrayList<>();
                playerHeadLore.add(e.getCurrentItem().getItemMeta().getDisplayName());
                playerHeadMeta.setLore(playerHeadLore);
                playerHeadItem.setItemMeta(playerHeadMeta);

                plugin.showCreateQuestGUI(player);
            }
        }
        else if(invTitle.contains("Choose a Target Item")) {
            e.setCancelled(true);

            if(e.getSlot() == 53) {
                plugin.showCreateQuestGUI(player);
            }
            else if(e.getCurrentItem() != null) {
                ItemStack targetItem = plugin.createQuestGUIMap.get(player.getUniqueId()).getItem(2);
                ItemMeta targetMeta = targetItem.getItemMeta();
                ArrayList<String> targetLore = new ArrayList<>();
                targetLore.add(e.getCurrentItem().getType().name());
                targetMeta.setLore(targetLore);
                targetItem.setItemMeta(targetMeta);

                plugin.showCreateQuestGUI(player);
            }
        }
        else if(invTitle.contains("Choose a Target Entity")) {
            e.setCancelled(true);

            if(e.getSlot() == 53) {
                plugin.showCreateQuestGUI(player);
            }
            else if(e.getCurrentItem() != null) {
                ItemStack targetItem = plugin.createQuestGUIMap.get(player.getUniqueId()).getItem(2);
                ItemMeta targetMeta = targetItem.getItemMeta();
                ArrayList<String> targetLore = new ArrayList<>();
                targetLore.add(e.getCurrentItem().getType().name().replace("_SPAWN_EGG", ""));
                targetMeta.setLore(targetLore);
                targetItem.setItemMeta(targetMeta);

                plugin.showCreateQuestGUI(player);
            }
        }
    }
}
