package me.lyorex.rokoquestsplugin.QuestClasses;

import org.bukkit.Material;

public class FindQuest extends Quest {
    private Material questItem;
    private String questItemName;

    public FindQuest() {
        super();
        this.setQuestType(QuestType.FIND);
        this.setQuestItem(Material.AIR);
    }

    public FindQuest(Material questItem) {
        this();
        this.setQuestItem(questItem);
    }

    public FindQuest(Material questItem, int questAmount) {
        this(questItem);
        this.setQuestAmount(questAmount);
    }

    public FindQuest(Material questItem, QuestDifficulty questDifficulty) {
        this(questItem);
        this.setQuestDifficulty(questDifficulty);
    }

    public FindQuest(Material questItem, int questAmount, QuestDifficulty questDifficulty) {
        this(questItem, questAmount);
        this.setQuestDifficulty(questDifficulty);
    }

    public FindQuest copy() {
        FindQuest quest = new FindQuest();
        quest.setQuestType(this.getQuestType());
        quest.setQuestAmount(this.getQuestAmount());
        quest.setQuestProgress(this.getQuestProgress());
        quest.setFinished(this.isFinished());
        quest.setQuestDifficulty(this.getQuestDifficulty());
        quest.setQuestItem(this.getQuestItem());
        quest.setQuestItemName(this.getQuestItemName());

        return quest;
    }

    public static FindQuest createRandomQuest() {
        return new FindQuest();
    }

    public Material getQuestItem() {
        return questItem;
    }

    public void setQuestItem(Material questItem) {
        this.questItem = questItem;
        this.setQuestItemName(this.questItem.name());
    }

    @Override
    public String getQuestDetails() {
        String out = "The mission is to FIND " + getQuestAmount() + " " + getQuestItem() + "(s). Current progress is " + getQuestProgress() + ". Difficulty: " + getQuestDifficulty();
        return out;
    }

    public String getQuestItemName() {
        return questItemName;
    }

    public void setQuestItemName(String questItemName) {
        this.questItemName = questItemName;
    }
}
