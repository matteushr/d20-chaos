package com.d20chaos.integration;

import net.neoforged.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry and detector for third-party optional mod integrations.
 * Verifies mod availability at runtime and maintains active/inactive flags.
 */
public class ModIntegrations {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModIntegrations.class);

    public enum SupportedMod {
        LUCKY_BLOCK("lucky", "Lucky Block", "Adds Lucky Block drops to critical successes and dungeon chests"),
        JEI("jei", "Just Enough Items", "Recipe and usage viewer"),
        FARMERS_DELIGHT("farmersdelight", "Farmer's Delight", "Culinary and agriculture system"),
        APPLESKIN("appleskin", "AppleSkin", "Food and exhaustion HUD overlay"),
        WAYSTONES("waystones", "Waystones", "Teleportation and waypoints system");

        private final String modId;
        private final String displayName;
        private final String description;

        SupportedMod(String modId, String displayName, String description) {
            this.modId = modId;
            this.displayName = displayName;
            this.description = description;
        }

        public String getModId() {
            return modId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    private static final Map<String, Boolean> MOD_STATUS_CACHE = new ConcurrentHashMap<>();
    private static boolean initialized = false;

    public static synchronized void init() {
        if (initialized) return;
        LOGGER.info("[D20/Integration] Checking optional mod integrations...");

        for (SupportedMod mod : SupportedMod.values()) {
            boolean loaded = checkModLoaded(mod.getModId());
            MOD_STATUS_CACHE.put(mod.getModId(), loaded);

            if (loaded) {
                LOGGER.info("[D20/Integration] -> {} [{}]: ACTIVE [✓] ({})",
                        mod.getDisplayName(), mod.getModId(), mod.getDescription());
            } else {
                LOGGER.info("[D20/Integration] -> {} [{}]: NOT INSTALLED [✗] (features disabled)",
                        mod.getDisplayName(), mod.getModId());
            }
        }

        initialized = true;
    }

    public static boolean isModLoaded(String modId) {
        if (modId == null || modId.isBlank()) return false;
        return MOD_STATUS_CACHE.computeIfAbsent(modId.toLowerCase(), ModIntegrations::checkModLoaded);
    }

    public static boolean isLuckyBlockLoaded() {
        return isModLoaded(SupportedMod.LUCKY_BLOCK.getModId());
    }

    private static boolean checkModLoaded(String modId) {
        try {
            ModList modList = ModList.get();
            return modList != null && modList.isLoaded(modId);
        } catch (Throwable t) {
            LOGGER.warn("[D20/Integration] Failed to verify mod '{}': {}", modId, t.getMessage());
            return false;
        }
    }

    public static Map<String, Boolean> getStatusMap() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(MOD_STATUS_CACHE));
    }
}
