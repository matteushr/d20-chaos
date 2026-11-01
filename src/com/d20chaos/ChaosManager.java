package com.d20chaos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class ChaosManager {
    private static final Map<UUID, Integer> CHAOS_PERCENT = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> ROLLS_COUNT = new ConcurrentHashMap<>();
    private static final Map<UUID, ServerBossEvent> BOSS_BARS = new ConcurrentHashMap<>();

    public static int getChaos(ServerPlayer player) {
        if (player == null) return 0;
        return com.d20chaos.data.ModAttachments.getData(player).getChaosLevel();
    }

    public static int getChaos(UUID playerUuid) {
        return CHAOS_PERCENT.getOrDefault(playerUuid, 0);
    }

    public static int getRollsCount(UUID playerUuid) {
        return ROLLS_COUNT.getOrDefault(playerUuid, 0);
    }

    public static int getRollsLeft(UUID playerUuid) {
        return Math.max(1, 5 - getRollsCount(playerUuid));
    }

    public static void setChaos(ServerPlayer player, int chaos) {
        if (player == null) return;
        int clamped = Math.max(0, Math.min(100, chaos));
        CHAOS_PERCENT.put(player.getUUID(), clamped);
        com.d20chaos.data.ModAttachments.getData(player).setChaosLevel(clamped);
        com.d20chaos.engine.D20Manager.syncToPlayer(player);
    }

    public static void setChaos(UUID playerUuid, int chaos) {
        CHAOS_PERCENT.put(playerUuid, Math.max(0, Math.min(100, chaos)));
    }

    public static void updatePlayerBar(ServerPlayer player) {
        UUID uuid = player.getUUID();
        int chaos = getChaos(uuid);
        int rollsLeft = getRollsLeft(uuid);
        String tierName = getTierName(chaos);
        String tierColor = getTierColor(chaos);

        ServerBossEvent bar = BOSS_BARS.computeIfAbsent(uuid, id -> {
            ServerBossEvent newBar = new ServerBossEvent(
                Component.literal("§6🎲 Chaos: 0% §e(Normal) §7| +5% in 5 rolls"),
                BossBarColor.YELLOW,
                BossBarOverlay.NOTCHED_20
            );
            newBar.addPlayer(player);
            return newBar;
        });

        bar.setProgress(chaos / 100.0f);
        bar.setName(Component.literal("§6🎲 Chaos: §f" + chaos + "% " + tierColor + "(" + tierName + ") §7| +5% in " + rollsLeft + " rolls"));

        // Update BossBar color by Tier
        if (chaos < 30) {
            bar.setColor(BossBarColor.YELLOW);
        } else if (chaos < 60) {
            bar.setColor(BossBarColor.YELLOW);
        } else if (chaos < 90) {
            bar.setColor(BossBarColor.RED);
        } else {
            bar.setColor(BossBarColor.PURPLE);
        }
    }

    public static void recordRoll(ServerPlayer player) {
        UUID uuid = player.getUUID();
        int rolls = ROLLS_COUNT.getOrDefault(uuid, 0) + 1;
        int current = getChaos(uuid);

        if (rolls >= 5) {
            rolls = 0;
            int next = Math.min(100, current + 5);
            setChaos(uuid, next);

            String oldTier = getTierName(current);
            String newTier = getTierName(next);

            if (!oldTier.equals(newTier)) {
                // Tier escalated Title popup (like in the video!)
                String color = getTierColor(next);
                TitleHelper.sendTitle(
                    player,
                    Component.literal(color + "§l" + newTier.toUpperCase() + " MODE"),
                    Component.literal("§7Chaos " + next + "%: the dice are growing wilder...")
                );
                player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 0.8f, 1.2f);
            }

            // Calamity event triggered at 100% Chaos!
            if (next >= 100 && current < 100) {
                triggerCalamity(player);
            }
        }

        ROLLS_COUNT.put(uuid, rolls);
        updatePlayerBar(player);
    }

    private static void triggerCalamity(ServerPlayer player) {
        TitleHelper.sendTitle(
            player,
            Component.literal("§4§l⚡ CALAMITY! ⚡"),
            Component.literal("§cChaos reached 100%! The Wrath of the Gods is upon you...")
        );
        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1.0f, 0.7f);
        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0, false, false));
    }

    public static void onSleep(ServerPlayer player) {
        UUID uuid = player.getUUID();
        int current = getChaos(uuid);
        setChaos(uuid, Math.max(0, current - 20));
        updatePlayerBar(player);
    }

    public static void onDeath(ServerPlayer player) {
        UUID uuid = player.getUUID();
        setChaos(uuid, 0);
        ROLLS_COUNT.put(uuid, 0);
        updatePlayerBar(player);
    }

    public static void onLogout(UUID playerUuid) {
        ServerBossEvent bar = BOSS_BARS.remove(playerUuid);
        if (bar != null) {
            bar.removeAllPlayers();
        }
    }

    public static String getTierName(int chaos) {
        if (chaos < 30) return "Normal";
        if (chaos < 60) return "Chaotic";
        if (chaos < 90) return "Insane";
        return "Beyond";
    }

    public static String getTierColor(int chaos) {
        if (chaos < 30) return "§e"; // Yellow
        if (chaos < 60) return "§6"; // Orange
        if (chaos < 90) return "§c"; // Red
        return "§5"; // Purple
    }
}
