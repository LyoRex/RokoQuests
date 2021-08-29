package me.lyorex.rokoquestsplugin;

import me.lyorex.rokoquestsplugin.Commands.*;
import me.lyorex.rokoquestsplugin.Listeners.CreateQuestGUIHandler;
import me.lyorex.rokoquestsplugin.Listeners.PlayerJoinListener;
import me.lyorex.rokoquestsplugin.Listeners.PlayerKillListener;
import me.lyorex.rokoquestsplugin.Listeners.QuestGUIHandler;
import me.lyorex.rokoquestsplugin.QuestClasses.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class RokoQuestsPlugin extends JavaPlugin {

    // ENABLE AND DISABLE MESSAGES
    private final String PLUGIN_START_MESSAGE = ChatColor.DARK_GREEN + "RokoQuests Plugin has been successfully enabled!";
    private final String PLUGIN_END_MESSAGE = ChatColor.DARK_RED + "RokoQuests Plugin has been successfully disabled";

    public static String PLUGIN_INFO_MESSAGE = ChatColor.GOLD + "Information about RokoQuests:";

    // PERMISSION/COMMAND MESSAGES
    public static final String PERMISSION_MESSAGE = ChatColor.RED + "You do not have permission to run this command!";
    public static final String MUST_BE_PLAYER_MESSAGE = ChatColor.RED + "You must be a player to run this command!";

    // INSTANCE OF PLUGIN CLASS
    private static RokoQuestsPlugin plugin;
    public static RokoQuestsPlugin getPlugin() { return plugin; }
    public static void setPlugin(RokoQuestsPlugin plugin) { RokoQuestsPlugin.plugin = plugin; }

    // QUEST VARIABLES
    public final String QUEST_GUI_NAME = ChatColor.DARK_GREEN + "Quest GUI";
    public final String CREATEQUEST_GUI_NAME = ChatColor.DARK_AQUA + "Create a Quest";
    public final String CREATEQUEST_PLAYER_GUI_NAME = "Choose a Player";
    public final String CREATEQUEST_ITEM_GUI_NAME = "Choose a Target Item:";
    public final String CREATEQUEST_ENTITY_GUI_NAME = "Choose a Target Entity:";
    public final String REWARD_GUI_NAME = ChatColor.DARK_GREEN + "Collect your reward(s)!";

    public List<Player> registeredPlayers = new ArrayList<>();

    public List<Integer> questAmounts = new ArrayList<>();

    // LIST OF QUEST TARGETS
    public List<Material> validFindItems = new ArrayList<>();
    public List<EntityType> validKillEntities = new ArrayList<>();

    // LIST OF REWARDS
    public List<ItemStack> easyRewards = new ArrayList<>();
    public List<ItemStack> normalRewards = new ArrayList<>();
    public List<ItemStack> hardRewards = new ArrayList<>();
    public List<ItemStack> extremeRewards = new ArrayList<>();

    public Material defaultQuestItem = Material.COBBLESTONE;
    public EntityType defaultQuestEntity = EntityType.ZOMBIE;

    public boolean isUniversalQuest;
    public Quest universalQuest;

    // QUEST HASHMAPS
    public HashMap<UUID, Quest> playerQuestMap = new HashMap<>();
    public HashMap<UUID, Inventory> playerQuestGUIMap = new HashMap<>();
    public HashMap<UUID, Inventory> playerRewardGUIMap = new HashMap<>();

    public HashMap<UUID, Inventory> createQuestGUIMap = new HashMap<>();

    // EXTRA VARIABLES
    public String[] DISC_NAMES;
    {
        DISC_NAMES = new String[]{
                "MUSIC_DISC_13",
                "MUSIC_DISC_CAT",
                "MUSIC_DISC_BLOCKS",
                "MUSIC_DISC_CHIRP",
                "MUSIC_DISC_FAR",
                "MUSIC_DISC_MALL",
                "MUSIC_DISC_MELLOHI",
                "MUSIC_DISC_STAL",
                "MUSIC_DISC_STRAD",
                "MUSIC_DISC_WARD",
                "MUSIC_DISC_11",
                "MUSIC_DISC_WAIT",
                "MUSIC_DISC_PIGSTEP"
        };
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        setPlugin(this);

        for(Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player);
            setPlayerDicts(player);
        }

        this.getCommand("rokoquests").setExecutor(new RokoQuestsGeneral());
        this.getCommand("listplayers").setExecutor(new ListPlayerQuests());
        this.getCommand("setquest").setExecutor(new SetQuest());
        this.getCommand("myquest").setExecutor(new MyQuest());
        this.getCommand("createquest").setExecutor(new CreateQuest());
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(), this);
        getServer().getPluginManager().registerEvents(new QuestGUIHandler(), this);
        getServer().getPluginManager().registerEvents(new CreateQuestGUIHandler(), this);

        isUniversalQuest = true;
        universalQuest = new Quest();

        this.saveDefaultConfig();

        // SAVING CUSTOM CONFIG EXAMPLE
//        configFile = new File(getDataFolder(), "data.yml");
//
//        dataConfig = YamlConfiguration.loadConfiguration(configFile);
//
//        InputStream defaultStream = getResource("data.yml");
//        if(defaultStream != null) {
//            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
//            dataConfig.setDefaults(defaultConfig);
//        }
//
//        ItemStack ex = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
//        ItemMeta exMeta = ex.getItemMeta();
//        exMeta.setDisplayName(ChatColor.GREEN + "DAMN");
//        ArrayList<String> exLore = new ArrayList<>();
//        exLore.add(ChatColor.RED + "SUPER");
//        exLore.add("DUPER");
//        exMeta.setLore(exLore);
//        ex.setItemMeta(exMeta);
//        ex.addUnsafeEnchantment(Enchantment.MENDING, 1);
//        ex.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
//
//        dataConfig.set("test", ex);
//
//        try {
//            dataConfig.save(configFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        this.loadConfigValues();

        Bukkit.getLogger().info(PLUGIN_START_MESSAGE);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(PLUGIN_END_MESSAGE);
    }

    // ADD PLAYER TO REGISTERD PLAYERS
    public void addPlayer(Player player) {
        registeredPlayers.add(player);
    }

    // SET PLAYER'S QUEST AND RESET THEIR MYQUEST GUI
    public void setQuest(Player player, Quest quest) {
        playerQuestMap.put(player.getUniqueId(), quest);
        if(player.getOpenInventory().getTitle().contains(plugin.QUEST_GUI_NAME)) showQuestGUI(player);
        playerRewardGUIMap.remove(player.getUniqueId());

        getLogger().info(player.getDisplayName() + "'s Quest has been set to: " + quest.getQuestDetails());
    }

    // SET {PLAYER, QUEST} AND {PLAYER, QUESTGUI} MAPS
    public void setPlayerDicts(Player player) {
        if(!playerQuestMap.containsKey(player.getUniqueId())) {
            if(!isUniversalQuest)
                setQuest(player, new Quest());
            else {
                setQuest(player, universalQuest.copy());
            }
        }
        if(!playerQuestGUIMap.containsKey(player.getUniqueId())) {
            playerQuestGUIMap.put(player.getUniqueId(), Bukkit.createInventory(player, 9, QUEST_GUI_NAME));
        }
    }

    // OPEN MYQUEST GUI
    public void showQuestGUI(Player player) {
        Inventory questGUI;
        if(!plugin.playerQuestGUIMap.containsKey(player.getUniqueId())) {
            questGUI = Bukkit.createInventory(player, 9, plugin.QUEST_GUI_NAME);
        }
        else {
            questGUI = plugin.playerQuestGUIMap.get(player.getUniqueId());
        }

        Quest playerQuest = plugin.playerQuestMap.get(player.getUniqueId());

        String questType = playerQuest.getQuestType().toString();
        String questTarget = "NULL";
        String questProgress = Integer.toString(playerQuest.getQuestProgress());
        String questAmount = Integer.toString(playerQuest.getQuestAmount());
        String questDifficulty = playerQuest.getQuestDifficulty().name();
        boolean questFinished = playerQuest.isFinished();

        if(playerQuest.getQuestType().equals(QuestType.FIND)) {
            questTarget = ((FindQuest) playerQuest).getQuestItemName();
            questGUI.clear(4);
        }
        else if(playerQuest.getQuestType().equals(QuestType.KILL)) {
            questTarget = ((KillQuest) playerQuest).getQuestEntityName();
        }
        ChatColor progressColor = questFinished ? ChatColor.DARK_GREEN : ChatColor.RED;

        ItemStack questDescItem = new ItemStack(Material.MAP, 1);
        ItemMeta questDescItemMeta = questDescItem.getItemMeta();
        questDescItemMeta.setDisplayName(progressColor + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + questType + " QUEST");
        ArrayList<String> questDescLore = new ArrayList<>();
        questDescLore.add(ChatColor.GOLD + "Your objective is to");
        questDescLore.add(ChatColor.GOLD + questType + " " + questAmount + " " + questTarget + "(s)...");
        questDescLore.add(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Difficulty:" + ChatColor.GOLD + " " + questDifficulty);
        questDescLore.add(progressColor + "Progress: " + questProgress + "/" + questAmount);
        questDescItemMeta.setLore(questDescLore);
        questDescItem.setItemMeta(questDescItemMeta);

        ItemStack closeGUIItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta closeGUIItemMeta = closeGUIItem.getItemMeta();
        closeGUIItemMeta.setDisplayName(ChatColor.RED + "CLOSE GUI");
        closeGUIItem.setItemMeta(closeGUIItemMeta);

        ItemStack directionItem = new ItemStack(Material.ARROW, 1);
        ItemMeta directionItemMeta = directionItem.getItemMeta();
        directionItemMeta.setDisplayName(ChatColor.DARK_PURPLE + "For FIND Quests:");
        ArrayList<String> directionItemLore = new ArrayList<>();
        directionItemLore.add(ChatColor.LIGHT_PURPLE + "Place your quest items in");
        directionItemLore.add(ChatColor.LIGHT_PURPLE + "the middle slot. Then click");
        directionItemLore.add(ChatColor.LIGHT_PURPLE + "the emerald to submit.");
        directionItemMeta.setLore(directionItemLore);
        directionItem.setItemMeta(directionItemMeta);

        ItemStack submitItem = new ItemStack(Material.EMERALD, 1);
        ItemMeta submitItemMeta = submitItem.getItemMeta();
        submitItemMeta.setDisplayName(ChatColor.GREEN + "Submit Quest Items...");
        submitItem.setItemMeta(submitItemMeta);

        ItemStack finishedItem = new ItemStack(Material.GREEN_WOOL, 1);
        ItemMeta finishedMeta = finishedItem.getItemMeta();
        finishedMeta.setDisplayName(ChatColor.GREEN + "FINISHED");
        ArrayList<String> finishedLore = new ArrayList<>();
        finishedLore.add("Click to open rewards...");
        finishedMeta.setLore(finishedLore);
        finishedItem.setItemMeta(finishedMeta);

        ItemStack unfinishedItem = new ItemStack(Material.RED_WOOL, 1);
        ItemMeta unfinishedMeta = unfinishedItem.getItemMeta();
        unfinishedMeta.setDisplayName(ChatColor.RED + "IN PROGRESS");
        unfinishedItem.setItemMeta(unfinishedMeta);

        ItemStack fillerItem = getFillerItem();

        questGUI.setItem(0, questDescItem);
        if(playerQuest.isFinished())
            questGUI.setItem(1, finishedItem);
        else
            questGUI.setItem(1, unfinishedItem);

        questGUI.setItem(2, fillerItem);
        questGUI.setItem(3, directionItem);

        questGUI.setItem(5, submitItem);
        questGUI.setItem(6, fillerItem);
        questGUI.setItem(7, fillerItem);
        questGUI.setItem(8, closeGUIItem);

        player.openInventory(questGUI);
    }

    // OPEN CREATEQUEST GUI
    public void showCreateQuestGUI(Player player) {
        Inventory createInventory;

        if(!plugin.createQuestGUIMap.containsKey(player.getUniqueId())) {
            createInventory = Bukkit.createInventory(player, 9, CREATEQUEST_GUI_NAME);
        }
        else {
            createInventory = plugin.createQuestGUIMap.get(player.getUniqueId());

            plugin.createQuestGUIMap.put(player.getUniqueId(), createInventory);

            player.openInventory(createInventory);
            return;
        }

        // FIND/KILL TOGGLE
        ItemStack typeItem = new ItemStack(Material.SPYGLASS, 1);
        ItemMeta typeMeta = typeItem.getItemMeta();
        typeMeta.setDisplayName(ChatColor.RED + "FIND");
        typeItem.setItemMeta(typeMeta);

        // SELECT WHICH PLAYER TO GIVE THE QUEST TO
        ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta playerMeta = playerItem.getItemMeta();
        playerMeta.setDisplayName(ChatColor.BLUE + "Quest Given to:");
        ArrayList<String> playerLore = new ArrayList<>();
        playerLore.add("ALL");
        playerMeta.setLore(playerLore);
        playerItem.setItemMeta(playerMeta);

        // SELECT WHAT ITEM/ENTITY TO TARGET
        ItemStack targetItem = new ItemStack(Material.TARGET, 1);
        ItemMeta targetMeta = targetItem.getItemMeta();
        targetMeta.setDisplayName(ChatColor.BLUE + "Quest Target:");
        ArrayList<String> targetLore = new ArrayList<>();
        if(plugin.validFindItems.size() < 1) targetLore.add("[ITEM]");
        else targetLore.add(plugin.validFindItems.get(0).name());
        targetMeta.setLore(targetLore);
        targetItem.setItemMeta(targetMeta);

        // TOGGLE HOW MUCH OF THE ITEM/ENTITY TO FIND/KILL
        ItemStack amountItem = new ItemStack(Material.FIREWORK_ROCKET, 1);
        ItemMeta amountMeta = amountItem.getItemMeta();
        amountMeta.setDisplayName(ChatColor.BLUE + "Quest Amount:");
        ArrayList<String> amountLore = new ArrayList<>();
        if(questAmounts.size() > 0) amountLore.add(questAmounts.get(0).toString());
        else amountLore.add("5");
        amountMeta.setLore(amountLore);
        amountItem.setItemMeta(amountMeta);

        // TOGGLE DIFFICULTY OF QUEST (DICTATES REWARDS)
        ItemStack difficultyItem = new ItemStack(Material.ZOMBIE_HEAD, 1);
        ItemMeta difficultyMeta = difficultyItem.getItemMeta();
        difficultyMeta.setDisplayName(ChatColor.BLUE + "Difficulty:");
        ArrayList<String> difficultyLore = new ArrayList<>();
        difficultyLore.add("EASY");
        difficultyMeta.setLore(difficultyLore);
        difficultyItem.setItemMeta(difficultyMeta);

        // CLICK TO CREATE QUEST
        ItemStack createItem = new ItemStack(Material.GREEN_WOOL, 1);
        ItemMeta createMeta = createItem.getItemMeta();
        createMeta.setDisplayName(ChatColor.GREEN + "Create Quest");
        createItem.setItemMeta(createMeta);

        // CLICK TO CANCEL QUEST
        ItemStack cancelItem = new ItemStack(Material.RED_WOOL, 1);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
        cancelItem.setItemMeta(cancelMeta);

        ItemStack fillerItem = getFillerItem();

        createInventory.setItem(0, typeItem);
        createInventory.setItem(1, playerItem);
        createInventory.setItem(2, targetItem);
        createInventory.setItem(3, amountItem);
        createInventory.setItem(4, difficultyItem);
        createInventory.setItem(5, fillerItem);
        createInventory.setItem(6, fillerItem);
        createInventory.setItem(7, createItem);
        createInventory.setItem(8, cancelItem);

        plugin.createQuestGUIMap.put(player.getUniqueId(), createInventory);

        player.openInventory(createInventory);
    }

    // LOAD VALUES FROM CONFIG FILE TO CORRESPONDING VARIABLES
    public void loadConfigValues() {
        loadTargetLists();
        loadRewardsLists();
        questAmounts = getConfig().getIntegerList("amounts");
    }
    public void loadTargetLists() {
        // CLEAR OUT CURRENT LIST OF ITEMS/ENTITIES AND CREATE A LIST OF NAMES OF ITEMS/ENTITIES FROM CONFIG FILE
        validFindItems.clear();
        validKillEntities.clear();
        List<String> findItems = getConfig().getStringList("find-items");
        List<String> killEntities = getConfig().getStringList("kill-entities");

        // ADD ALL ITEMS AND ENTITIES
        for(String item : findItems) {
            String upperItem = item.toUpperCase();
            try {
                if(!validFindItems.contains(Material.valueOf(upperItem)))
                    this.validFindItems.add(Material.valueOf(upperItem));
            }
            catch(IllegalArgumentException e) {
                getLogger().info(ChatColor.DARK_RED + "" + upperItem + " is not a valid Material value!");
            }
        }
        for(String entity : killEntities) {
            String upperEntity = entity.toUpperCase();
            try {
                if(!validKillEntities.contains(EntityType.valueOf(upperEntity)))
                    this.validKillEntities.add(EntityType.valueOf(upperEntity));
            } catch (IllegalArgumentException e) {
                getLogger().info(ChatColor.DARK_RED + "" + upperEntity + " is not a valid EntityType value!");
            }
        }

        // CUT OFF EXTRA ITEMS IN LIST PAST 53RD ITEM
        // IF LIST IS EMPTY, ADD DEFAULT VALUES (COBBLESTONE AND ZOMBIE)
        if(validFindItems.size() > 53) validFindItems = validFindItems.subList(0, 53);
        if(validKillEntities.size() > 53) validKillEntities = validKillEntities.subList(0, 53);
        if(validFindItems.size() < 1) validFindItems.add(Material.COBBLESTONE);
        if(validKillEntities.size() < 1) validKillEntities.add(EntityType.ZOMBIE);

        getLogger().info(ChatColor.GOLD + "List of valid quest Items: " + validFindItems.toString());
        getLogger().info(ChatColor.GOLD + "List of valid quest Entities: " + validKillEntities.toString());

        // CLOSE CREATEQUEST GUI FOR ALL PLAYERS WITH IT OPEN
        for(Player player : registeredPlayers) {
            createQuestGUIMap.clear();
            String invTitle = player.getOpenInventory().getTitle();
            if(invTitle.contains(CREATEQUEST_GUI_NAME) || invTitle.contains(CREATEQUEST_PLAYER_GUI_NAME) || invTitle.contains(CREATEQUEST_ITEM_GUI_NAME) || invTitle.contains(CREATEQUEST_ENTITY_GUI_NAME))
                player.closeInventory();
        }
    }
    public void loadRewardsLists() {
        // CLEAR OUT CURRENT LIST OF REWARDS AND CREATE A LIST OF REWARDS FROM CONFIG FILE
        easyRewards.clear();
        normalRewards.clear();
        hardRewards.clear();
        extremeRewards.clear();

        int numEasy = getPlugin().getConfig().getConfigurationSection("rewards.easy").getKeys(false).size();
        for(int i = 0; i < numEasy; i++) {
            String itemName = getConfig().getString("rewards.easy.reward-" + i + ".item");
            if(itemName.equals("MUSIC_DISC")) itemName = DISC_NAMES[(new Random()).nextInt(DISC_NAMES.length)];

            Material mat = Material.valueOf(itemName);
            int amt = getConfig().getInt("rewards.easy.reward-" + i + ".amount");

            ItemStack is = new ItemStack(mat, amt);

            easyRewards.add(is);
        }

        int numNormal = getPlugin().getConfig().getConfigurationSection("rewards.normal").getKeys(false).size();
        for(int i = 0; i < numNormal; i++) {
            String itemName = getConfig().getString("rewards.normal.reward-" + i + ".item");
            if(itemName.equals("MUSIC_DISC")) itemName = DISC_NAMES[(new Random()).nextInt(DISC_NAMES.length)];

            Material mat = Material.valueOf(itemName);
            int amt = getConfig().getInt("rewards.normal.reward-" + i + ".amount");

            ItemStack is = new ItemStack(mat, amt);

            normalRewards.add(is);
        }

        int numHard = getPlugin().getConfig().getConfigurationSection("rewards.hard").getKeys(false).size();
        for(int i = 0; i < numHard; i++) {
            String itemName = getConfig().getString("rewards.hard.reward-" + i + ".item");
            if(itemName.equals("MUSIC_DISC")) itemName = DISC_NAMES[(new Random()).nextInt(DISC_NAMES.length)];

            Material mat = Material.valueOf(itemName);
            int amt = getConfig().getInt("rewards.hard.reward-" + i + ".amount");

            ItemStack is = new ItemStack(mat, amt);

            hardRewards.add(is);
        }

        int numExtreme = getPlugin().getConfig().getConfigurationSection("rewards.extreme").getKeys(false).size();
        for(int i = 0; i < numExtreme; i++) {
            String itemName = getConfig().getString("rewards.extreme.reward-" + i + ".item");
            if(itemName.equals("MUSIC_DISC")) itemName = DISC_NAMES[(new Random()).nextInt(DISC_NAMES.length)];

            Material mat = Material.valueOf(itemName);
            int amt = getConfig().getInt("rewards.extreme.reward-" + i + ".amount");

            ItemStack is = new ItemStack(mat, amt);

            extremeRewards.add(is);
        }
        getLogger().info(ChatColor.RED + "EASY");
        for(ItemStack is : easyRewards) {
            getLogger().info(is.toString());
        }
        getLogger().info(ChatColor.RED + "NORMAL");
        for(ItemStack is : normalRewards) {
            getLogger().info(is.toString());
        }
        getLogger().info(ChatColor.RED + "HARD");
        for(ItemStack is : hardRewards) {
            getLogger().info(is.toString());
        }
        getLogger().info(ChatColor.RED + "EXTREME");
        for(ItemStack is : extremeRewards) {
            getLogger().info(is.toString());
        }
    }

    /*
        CREATE QUEST FUNCTIONS
     */
    // CREATE PLAYER SELECT GUI
    public Inventory getPlayerGUI(Player player) {
        Inventory questPlayerGUI = Bukkit.createInventory(player, 54, CREATEQUEST_PLAYER_GUI_NAME);
        for(int i = 0; i < registeredPlayers.size(); i++) {
            String playerName = registeredPlayers.get(i).getDisplayName();
            ItemStack curHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta sm = (SkullMeta) curHead.getItemMeta();
            sm.setDisplayName(playerName);
            sm.setOwningPlayer(registeredPlayers.get(i));
            curHead.setItemMeta(sm);
            questPlayerGUI.setItem(i, curHead);
        }
        ItemStack allItem = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta allMeta = allItem.getItemMeta();
        allMeta.setDisplayName(ChatColor.DARK_GREEN + "ALL");
        allItem.setItemMeta(allMeta);
        questPlayerGUI.setItem(45, allItem);

        ItemStack exitItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.setDisplayName(ChatColor.RED + "RETURN");
        exitItem.setItemMeta(exitMeta);
        questPlayerGUI.setItem(53, exitItem);

        return questPlayerGUI;
    }
    // CREATE ITEM SELECT GUI
    public Inventory getItemGUI(Player player) {
        Inventory questItemGUI = Bukkit.createInventory(player, 54, CREATEQUEST_ITEM_GUI_NAME);
        for(int i = 0; i < validFindItems.size(); i++) {
            ItemStack curItem = new ItemStack(validFindItems.get(i), 1);
            ItemMeta meta = curItem.getItemMeta();
            meta.setDisplayName(validFindItems.get(i).name());
            curItem.setItemMeta(meta);
            questItemGUI.setItem(i, curItem);
            if(i == 52) break;
        }
        ItemStack exitItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.setDisplayName(ChatColor.RED + "RETURN");
        exitItem.setItemMeta(exitMeta);
        questItemGUI.setItem(53, exitItem);

        return questItemGUI;
    }
    // CREATE ENTITY SELECT GUI
    public Inventory getEntityGUI(Player player) {
        Inventory questEntityGUI = Bukkit.createInventory(player, 54, CREATEQUEST_ENTITY_GUI_NAME);
        for(int i = 0; i < validKillEntities.size(); i++) {
            EntityType et = validKillEntities.get(i);
            Material entityMaterial;
            if(et.equals(EntityType.SNOWMAN) || et.equals(EntityType.IRON_GOLEM) || et.equals(EntityType.ENDER_DRAGON) || et.equals(EntityType.WITHER)) {
                if(et.equals(EntityType.SNOWMAN)) entityMaterial = Material.SNOWBALL;
                else if(et.equals(EntityType.IRON_GOLEM)) entityMaterial = Material.IRON_BLOCK;
                else if(et.equals(EntityType.ENDER_DRAGON)) entityMaterial = Material.DRAGON_HEAD;
                else entityMaterial = Material.WITHER_SKELETON_SKULL;
            }
            else {
                String entitySpawnEggName = validKillEntities.get(i).name() + "_SPAWN_EGG";
                if(Material.getMaterial(entitySpawnEggName) != null)
                    entityMaterial = Material.getMaterial(entitySpawnEggName);
                else continue;
            }
            ItemStack curItem = new ItemStack(entityMaterial, 1);
            ItemMeta meta = curItem.getItemMeta();
            meta.setDisplayName(et.name());
            curItem.setItemMeta(meta);
            questEntityGUI.setItem(i, curItem);
            if(i == 52) break;
        }
        ItemStack exitItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.setDisplayName(ChatColor.RED + "RETURN");
        exitItem.setItemMeta(exitMeta);
        questEntityGUI.setItem(53, exitItem);

        return questEntityGUI;
    }

    public Inventory getRewardGUI(Player player, QuestDifficulty difficulty) {
        if(playerRewardGUIMap.containsKey(player.getUniqueId())) {
            return playerRewardGUIMap.get(player.getUniqueId());
        }

        Inventory rewardInv = Bukkit.createInventory(player, 18, REWARD_GUI_NAME);
        List<ItemStack> rewardsTemp = new ArrayList<>();
        if(difficulty.equals(QuestDifficulty.EASY)) {
            for(ItemStack is : easyRewards) {
                rewardsTemp.add(is.clone());
            }
        }
        else if(difficulty.equals(QuestDifficulty.NORMAL)) {
            for(ItemStack is : normalRewards) {
                rewardsTemp.add(is.clone());
            }
        }
        else if(difficulty.equals(QuestDifficulty.HARD)) {
            for(ItemStack is : hardRewards) {
                rewardsTemp.add(is.clone());
            }
        }
        else if(difficulty.equals(QuestDifficulty.EXTREME)) {
            for(ItemStack is : extremeRewards) {
                rewardsTemp.add(is.clone());
            }
        }
        int numRewards = Math.min(3, rewardsTemp.size());
        for(int i = 0; i < numRewards; i++) {
            Random rand = new Random();
            int index = rand.nextInt(rewardsTemp.size());
            rewardInv.setItem(2*i + 2, rewardsTemp.remove(index));
        }

        ItemStack infoItem = new ItemStack(Material.FILLED_MAP, 1);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName(ChatColor.RED + "Unclaimed");
        ArrayList<String> infoLore = new ArrayList<>();
        infoLore.add("Claim 2 of the rewards!");
        infoLore.add("Click the wool to toggle");
        infoLore.add("between red and green");
        infoLore.add("Red = Don't Claim; Green = Claim");
        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);

        ItemStack redWool = new ItemStack(Material.RED_WOOL, 1);

        ItemStack confirmItem = new ItemStack(Material.EMERALD, 1);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm Reward Selection");
        confirmItem.setItemMeta(confirmMeta);

        ItemStack cancelItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.DARK_RED + "Return");
        cancelItem.setItemMeta(cancelMeta);

        rewardInv.setItem(0, infoItem);
        rewardInv.setItem(11, redWool);
        rewardInv.setItem(13, redWool);
        rewardInv.setItem(15, redWool);
        rewardInv.setItem(8, confirmItem);
        rewardInv.setItem(17, cancelItem);

        playerRewardGUIMap.put(player.getUniqueId(), rewardInv);

        return rewardInv;
    }
    /*
        HELPER FUNCTIONS
     */
    // FILTER TAB COMPLETION TO FILLERS THAT MATCH THE TYPED INPUT
    public static List<String> getMatchingTabCompleteArgs(List<String> fillers, String curArgString) {

        List<String> results = new ArrayList<>();
        for(String filler : fillers)
        {
            String s = filler.toLowerCase();
            if(s.startsWith(curArgString.toLowerCase()))
                results.add(filler);
        }
        return results;
    }

    // SEND USER ERROR MESSAGES
    public static void sendPermissionMessage(CommandSender sender) {
        sender.sendMessage(PERMISSION_MESSAGE);
    }
    public static void sendMustBePlayerMessage(CommandSender sender) {
        sender.sendMessage(MUST_BE_PLAYER_MESSAGE);
    }

    // RETURN GUI FILLER ITEM
    public ItemStack getFillerItem() {
        ItemStack fillerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        fillerMeta.setDisplayName(" ");
        fillerMeta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        fillerItem.setItemMeta(fillerMeta);

        return fillerItem;
    }
}
