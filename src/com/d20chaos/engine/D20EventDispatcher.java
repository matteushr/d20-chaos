package com.d20chaos.engine;

import com.d20chaos.api.ActionCategory;
import com.d20chaos.api.D20EventContext;
import com.d20chaos.api.DiceTier;
import com.d20chaos.api.ID20ActionEvent;
import com.d20chaos.data.ModAttachments;
import com.d20chaos.data.PlayerD20Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class D20EventDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(D20EventDispatcher.class);
    private static final Map<String, ID20ActionEvent> REGISTRY = new ConcurrentHashMap<>();

    public static void register(ID20ActionEvent event) {
        if (event == null || event.getId() == null) return;
        REGISTRY.put(event.getId().toLowerCase(), event);
        LOGGER.info("[D20] Module registered: {} [{}] ({})", event.getId(), event.getCategory(), event.getTier());
    }

    public static boolean forceTriggerById(String eventId, D20EventContext ctx) {
        if (eventId == null) return false;
        ID20ActionEvent event = REGISTRY.get(eventId.toLowerCase());
        if (event != null) {
            String playerName = ctx.player() != null ? ctx.player().getScoreboardName() : "unknown";
            LOGGER.info("[D20/Forced] Manually triggering module '{}' ({}) for {}", eventId, event.getName(), playerName);
            try {
                boolean success = event.execute(ctx);
                if (success) {
                    LOGGER.info("[D20/Forced] [Success] Module '{}' executed successfully.", eventId);
                    if (ctx.player() != null) {
                        PlayerD20Attachment data = ModAttachments.getData(ctx.player());
                        data.setLastEventId(event.getName());
                    }
                } else {
                    LOGGER.warn("[D20/Forced] [Failure] Module '{}' returned false upon execution.", eventId);
                }
                return success;
            } catch (Throwable t) {
                LOGGER.error("[D20/Forced] [Critical Error] Exception while forcing module '{}':", eventId, t);
                return false;
            }
        }
        LOGGER.warn("[D20/Forced] Module not found in registry: {}", eventId);
        return false;
    }

    public static boolean dispatchRandom(ActionCategory category, DiceTier tier, D20EventContext ctx) {
        String playerName = ctx.player() != null ? ctx.player().getScoreboardName() : "unknown";

        // Tier 3 is Vanilla Equilibrium (Neutral Zone): execute cosmetic neutral feedback if registered
        if (tier == DiceTier.TIER_3_NEUTRAL) {
            ID20ActionEvent neutralEv = REGISTRY.get("neutral_serene_breeze");
            if (neutralEv != null) {
                try {
                    boolean success = neutralEv.execute(ctx);
                    if (success && ctx.player() != null) {
                        PlayerD20Attachment data = ModAttachments.getData(ctx.player());
                        data.setLastEventId(neutralEv.getName());
                    }
                    return success;
                } catch (Throwable t) {
                    LOGGER.error("[D20/Dispatcher] Error executing neutral event:", t);
                }
            }
            LOGGER.info("[D20/Dispatcher] Tier 3 Neutral (Vanilla Equilibrium) active. No action triggered for {}.", playerName);
            return false;
        }

        List<ID20ActionEvent> matching = new ArrayList<>();
        for (ID20ActionEvent ev : REGISTRY.values()) {
            if (ev.getCategory() == category && ev.getTier() == tier) {
                matching.add(ev);
            }
        }

        if (matching.isEmpty()) {
            LOGGER.warn("[D20/Dispatcher] No module registered for Category: {} at Tier: {}", category, tier);
            return false;
        }

        int index = ThreadLocalRandom.current().nextInt(matching.size());
        ID20ActionEvent selected = matching.get(index);
        LOGGER.info("[D20/Dispatcher] Executing module '{}' ({}) for {} upon action {}",
                selected.getId(), selected.getName(), playerName, category);

        try {
            boolean success = selected.execute(ctx);
            if (success) {
                LOGGER.info("[D20/Dispatcher] [Success] Module '{}' completed for {}.", selected.getId(), playerName);
                if (ctx.player() != null) {
                    PlayerD20Attachment data = ModAttachments.getData(ctx.player());
                    data.setLastEventId(selected.getName());
                }
            } else {
                LOGGER.warn("[D20/Dispatcher] [Failure] Module '{}' returned false (invalid condition or aborted) for {}.",
                        selected.getId(), playerName);
            }
            return success;
        } catch (Throwable t) {
            LOGGER.error("[D20/Dispatcher] [Critical Error] Unexpected exception executing module '{}' for {}:",
                    selected.getId(), playerName, t);
            return false;
        }
    }

    public static Map<String, ID20ActionEvent> getRegistry() {
        return REGISTRY;
    }
}
