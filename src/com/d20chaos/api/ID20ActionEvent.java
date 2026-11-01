package com.d20chaos.api;

import com.d20chaos.integration.ModIntegrations;

public interface ID20ActionEvent {
    String getId();
    String getName();
    ActionCategory getCategory();
    DiceTier getTier();
    boolean execute(D20EventContext ctx);

    /**
     * Optional required third-party mod ID for this event to be active.
     * Returns null if the event is vanilla / independent.
     */
    default String getRequiredModId() {
        return null;
    }

    /**
     * Returns true if this event is currently eligible to be rolled.
     */
    default boolean isAvailable() {
        String modId = getRequiredModId();
        return modId == null || ModIntegrations.isModLoaded(modId);
    }
}
