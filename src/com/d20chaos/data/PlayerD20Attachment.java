package com.d20chaos.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerD20Attachment implements INBTSerializable<CompoundTag> {

    public static final int DEFAULT_CYCLE_TICKS = 24000; // 20 minutes (1 Minecraft Day)

    private int generalDecision = 10; // Default to Tier 3 Neutral
    private int cycleTimerTicks = DEFAULT_CYCLE_TICKS;
    private int chaosLevel = 0;
    private float walkAccumulator = 0.0f;
    private int lastRolledNumber = 0;
    private long lastTriggerTime = 0L;
    private String lastEventId = "none";
    private CompoundTag savedCageMatrix = new CompoundTag();

    public PlayerD20Attachment() {}

    public int getGeneralDecision() {
        return generalDecision;
    }

    public void setGeneralDecision(int generalDecision) {
        this.generalDecision = Math.max(1, Math.min(20, generalDecision));
    }

    public int getCycleTimerTicks() {
        return cycleTimerTicks;
    }

    public void setCycleTimerTicks(int cycleTimerTicks) {
        this.cycleTimerTicks = cycleTimerTicks;
    }

    public void tickCycleTimer() {
        if (this.cycleTimerTicks > 0) {
            this.cycleTimerTicks--;
        }
    }

    public int getChaosLevel() {
        return chaosLevel;
    }

    public void setChaosLevel(int chaosLevel) {
        this.chaosLevel = Math.max(0, Math.min(100, chaosLevel));
    }

    public void addChaos(int amount) {
        setChaosLevel(this.chaosLevel + amount);
    }

    public float getWalkAccumulator() {
        return walkAccumulator;
    }

    public void setWalkAccumulator(float walkAccumulator) {
        this.walkAccumulator = walkAccumulator;
    }

    public void addWalkDistance(float distance) {
        this.walkAccumulator += distance;
    }

    public int getLastRolledNumber() {
        return lastRolledNumber;
    }

    public void setLastRolledNumber(int lastRolledNumber) {
        this.lastRolledNumber = lastRolledNumber;
    }

    public long getLastTriggerTime() {
        return lastTriggerTime;
    }

    public void setLastTriggerTime(long lastTriggerTime) {
        this.lastTriggerTime = lastTriggerTime;
    }

    public String getLastEventId() {
        return lastEventId;
    }

    public void setLastEventId(String lastEventId) {
        this.lastEventId = lastEventId != null ? lastEventId : "none";
    }

    public CompoundTag getSavedCageMatrix() {
        return savedCageMatrix;
    }

    public void setSavedCageMatrix(CompoundTag savedCageMatrix) {
        this.savedCageMatrix = savedCageMatrix != null ? savedCageMatrix : new CompoundTag();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("GeneralDecision", this.generalDecision);
        tag.putInt("CycleTimerTicks", this.cycleTimerTicks);
        tag.putInt("ChaosLevel", this.chaosLevel);
        tag.putFloat("WalkAccumulator", this.walkAccumulator);
        tag.putInt("LastRolledNumber", this.lastRolledNumber);
        tag.putLong("LastTriggerTime", this.lastTriggerTime);
        tag.putString("LastEventId", this.lastEventId);
        if (!this.savedCageMatrix.isEmpty()) {
            tag.put("SavedCageMatrix", this.savedCageMatrix);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.contains("GeneralDecision")) {
            this.generalDecision = tag.getInt("GeneralDecision");
        }
        if (tag.contains("CycleTimerTicks")) {
            this.cycleTimerTicks = tag.getInt("CycleTimerTicks");
        }
        if (tag.contains("ChaosLevel")) {
            this.chaosLevel = tag.getInt("ChaosLevel");
        }
        if (tag.contains("WalkAccumulator")) {
            this.walkAccumulator = tag.getFloat("WalkAccumulator");
        }
        if (tag.contains("LastRolledNumber")) {
            this.lastRolledNumber = tag.getInt("LastRolledNumber");
        }
        if (tag.contains("LastTriggerTime")) {
            this.lastTriggerTime = tag.getLong("LastTriggerTime");
        }
        if (tag.contains("LastEventId")) {
            this.lastEventId = tag.getString("LastEventId");
        }
        if (tag.contains("SavedCageMatrix")) {
            this.savedCageMatrix = tag.getCompound("SavedCageMatrix");
        }
    }
}
