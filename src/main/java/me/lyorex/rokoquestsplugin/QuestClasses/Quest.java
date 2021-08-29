package me.lyorex.rokoquestsplugin.QuestClasses;

public class Quest {
    private QuestType questType;
    private int questAmount;
    private int questProgress;
    private boolean isFinished;
    private QuestDifficulty questDifficulty;

    public Quest() {
        this.setQuestType(QuestType.GENERIC);
        this.setQuestAmount(1);
        this.setQuestProgress(0);
        this.updateFinished();
        this.setQuestDifficulty(QuestDifficulty.EASY);
    }

    public Quest(int questAmount) {
        this();
        this.setQuestAmount(questAmount);
    }

    public Quest(int questAmount, QuestDifficulty questDifficulty) {
        this(questAmount);
        this.setQuestDifficulty(questDifficulty);
    }

    public Quest copy() {
        Quest quest = new Quest();
        quest.setQuestType(this.getQuestType());
        quest.setQuestAmount(this.getQuestAmount());
        quest.setQuestProgress(this.getQuestProgress());
        quest.setFinished(this.isFinished());
        quest.setQuestDifficulty(this.getQuestDifficulty());

        return quest;
    }

    public static Quest createRandomQuest() {
        return new Quest();
    }

    public QuestType getQuestType() {
        return questType;
    }

    public void setQuestType(QuestType questType) {
        this.questType = questType;
    }

    public int getQuestAmount() {
        return questAmount;
    }

    public void setQuestAmount(int questAmount) {
        this.questAmount = questAmount;
    }

    public int getQuestProgress() {
        return questProgress;
    }

    public void setQuestProgress(int questProgress) {
        this.questProgress = questProgress;
    }

    public void incrementQuestProgress(int incAmount) { this.questProgress += incAmount; }

    public String getQuestDetails() {
        String out = "A Generic Quest";
        return out;
    }

    public void updateFinished() {
        this.setFinished(this.getQuestProgress() >= this.getQuestAmount());
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        this.isFinished = finished;
    }

    public QuestDifficulty getQuestDifficulty() {
        return questDifficulty;
    }

    public void setQuestDifficulty(QuestDifficulty questDifficulty) {
        this.questDifficulty = questDifficulty;
    }
}
