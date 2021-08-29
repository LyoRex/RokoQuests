package me.lyorex.rokoquestsplugin.QuestClasses;

import org.bukkit.entity.EntityType;

public class KillQuest extends Quest {
    private EntityType questEntity;
    private String questEntityName;

    public KillQuest() {
        super();
        this.setQuestType(QuestType.KILL);
        this.setQuestEntity(EntityType.GIANT);
    }

    public KillQuest(EntityType questEntity) {
        this();
        this.setQuestEntity(questEntity);
    }

    public KillQuest(EntityType questEntity, int questAmount) {
        this(questEntity);
        this.setQuestAmount(questAmount);
    }

    public KillQuest(EntityType questEntity, QuestDifficulty questDifficulty) {
        this(questEntity);
        this.setQuestDifficulty(questDifficulty);
    }

    public KillQuest(EntityType questEntity, int questAmount, QuestDifficulty questDifficulty) {
        this(questEntity, questAmount);
        this.setQuestDifficulty(questDifficulty);
    }

    public KillQuest copy() {
        KillQuest quest = new KillQuest();
        quest.setQuestType(this.getQuestType());
        quest.setQuestAmount(this.getQuestAmount());
        quest.setQuestProgress(this.getQuestProgress());
        quest.setFinished(this.isFinished());
        quest.setQuestDifficulty(this.getQuestDifficulty());
        quest.setQuestEntity(this.getQuestEntity());
        quest.setQuestEntityName(this.getQuestEntityName());

        return quest;
    }

    public static KillQuest createRandomQuest() {
        return new KillQuest();
    }

    public EntityType getQuestEntity() {
        return questEntity;
    }

    public void setQuestEntity(EntityType questEntity) {
        this.questEntity = questEntity;
        this.setQuestEntityName(this.questEntity.name());
    }

    @Override
    public String getQuestDetails() {
        String out = "The mission is to KILL " + getQuestAmount() + " " + getQuestEntity() + "(s). Current progress is " + getQuestProgress() + ". Difficulty: " + getQuestDifficulty();
        return out;
    }

    public String getQuestEntityName() {
        return questEntityName;
    }

    public void setQuestEntityName(String questEntityName) {
        this.questEntityName = questEntityName;
    }
}
