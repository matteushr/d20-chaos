package com.d20chaos.client;

import com.d20chaos.api.DiceTier;
import com.d20chaos.network.D20SyncPayload;

public class ClientD20State {

    private static int generalDecision = 10;
    private static DiceTier currentTier = DiceTier.TIER_3_NEUTRAL;
    private static int ticksRemaining = 24000;
    private static int chaosLevel = 0;
    private static float triggerChance = 0.04f;
    private static int lastRolledNumber = 0;
    private static String lastEventId = "none";
    private static long lastTriggerTimestamp = 0L;

    public static synchronized void update(D20SyncPayload payload) {
        if (payload.generalDecision() != generalDecision) {
            generalDecision = payload.generalDecision();
            currentTier = DiceTier.fromDecision(generalDecision);
        }
        ticksRemaining = payload.ticksRemaining();
        chaosLevel = payload.chaosLevel();
        triggerChance = payload.triggerChance();
        lastRolledNumber = payload.rolledNumber();

        if (payload.lastEventId() != null && !payload.lastEventId().equals("none")) {
            lastEventId = payload.lastEventId();
        }
        if (payload.timestamp() > lastTriggerTimestamp) {
            lastTriggerTimestamp = payload.timestamp();
        }
    }

    public static int getGeneralDecision() {
        return generalDecision;
    }

    public static DiceTier getCurrentTier() {
        return currentTier;
    }

    public static int getTicksRemaining() {
        return ticksRemaining;
    }

    public static int getChaosLevel() {
        return chaosLevel;
    }

    public static float getTriggerChance() {
        return triggerChance;
    }

    public static int getLastRolledNumber() {
        return lastRolledNumber;
    }

    public static String getLastEventId() {
        return lastEventId;
    }

    public static boolean isTriggerHighlightActive() {
        return (System.currentTimeMillis() - lastTriggerTimestamp) < 3000L; // 3 seconds lock
    }

    public static float getHighlightProgress() {
        long elapsed = System.currentTimeMillis() - lastTriggerTimestamp;
        if (elapsed >= 3000L) return 0.0f;
        return 1.0f - (elapsed / 3000.0f);
    }
}
