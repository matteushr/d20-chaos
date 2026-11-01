package com.d20chaos.engine;

import com.d20chaos.ModItems;
import com.d20chaos.TitleHelper;
import com.d20chaos.api.ActionCategory;
import com.d20chaos.api.D20EventContext;
import com.d20chaos.api.DiceTier;
import com.d20chaos.data.ModAttachments;
import com.d20chaos.data.PlayerD20Attachment;
import com.d20chaos.network.D20SyncPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class D20Manager {

    private static final Logger LOGGER = LoggerFactory.getLogger(D20Manager.class);

    public static final float DEFAULT_TRIGGER_CHANCE = 0.04f; // 4% default rate
    private static float triggerChance = DEFAULT_TRIGGER_CHANCE;

    public static final int DEFAULT_CYCLE_TICKS = 24000; // 20 minutes (24000 ticks)
    private static int cycleDurationTicks = DEFAULT_CYCLE_TICKS;

    public static float getTriggerChance() {
        return triggerChance;
    }

    public static void setTriggerChance(float chance) {
        triggerChance = Math.max(0.0f, Math.min(1.0f, chance));
        LOGGER.info("[D20] Global trigger rate set to: {}%", (int) (triggerChance * 100));
    }

    public static int getCycleDurationTicks() {
        return cycleDurationTicks;
    }

    public static void setCycleDurationTicks(int ticks) {
        cycleDurationTicks = Math.max(20, ticks); // Minimum 1 second (20 ticks)
        LOGGER.info("[D20] Cycle duration set to: {} ticks ({}s)", cycleDurationTicks, cycleDurationTicks / 20);
    }

    public static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerD20Attachment data = ModAttachments.getData(player);
            data.tickCycleTimer();

            // Sync to client once every second (20 ticks) to keep timer and stats smoothly aligned
            if (server.getTickCount() % 20 == 0) {
                syncToPlayer(player);
            }

            // When cycle expires, roll a new Master D20
            if (data.getCycleTimerTicks() <= 0) {
                int masterRoll = ThreadLocalRandom.current().nextInt(1, 21);
                applyGeneralDecision(player, masterRoll, true);
                data.setCycleTimerTicks(cycleDurationTicks);
            }
        }
    }

    public static void applyGeneralDecision(ServerPlayer player, int decisionRoll, boolean announce) {
        PlayerD20Attachment data = ModAttachments.getData(player);
        data.setGeneralDecision(decisionRoll);
        data.setLastRolledNumber(decisionRoll);
        data.setLastTriggerTime(System.currentTimeMillis());
        DiceTier tier = DiceTier.fromDecision(decisionRoll);

        LOGGER.info("[D20] General Decision set to {} ({}) for player {}", decisionRoll, tier.getTitle(), player.getScoreboardName());

        syncToPlayer(player);

        if (announce) {
            String titleText = String.format("§6§l🎲 GENERAL DECISION: %d", decisionRoll);
            String subText = String.format("%s §f- %s", tier.getStatusTag(), tier.getTitle());

            TitleHelper.sendTitle(player, Component.literal(titleText), Component.literal(subText), 10, 70, 20);

            if (decisionRoll == 20) {
                TitleHelper.triggerTotemAnimation(new ItemStack(ModItems.D20_NAT20.get()));
                player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
            } else if (decisionRoll == 1) {
                TitleHelper.triggerTotemAnimation(new ItemStack(ModItems.D20_NAT1.get()));
                player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.9f, 0.8f);
            } else {
                player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8f, 1.2f);
            }

            player.sendSystemMessage(Component.literal(String.format(
                    "§e[D20] §6New General Decision (20min cycle): §f%d §7(%s %s)",
                    decisionRoll, tier.getStatusTag(), tier.getTitle()
            )));
        }
    }

    public static boolean checkActionTrigger(ServerPlayer player, ActionCategory category, BlockPos pos, Entity target) {
        float rollChance = ThreadLocalRandom.current().nextFloat();

        if (rollChance >= triggerChance) {
            // Micro 96% pass-through (no event triggered)
            return false;
        }

        // 4% Hit! Trigger micro-action event
        int microRoll = ThreadLocalRandom.current().nextInt(1, 21);
        PlayerD20Attachment data = ModAttachments.getData(player);
        data.setLastRolledNumber(microRoll);
        data.setLastTriggerTime(System.currentTimeMillis());

        DiceTier tier = DiceTier.fromDecision(data.getGeneralDecision());
        D20EventContext ctx = new D20EventContext(
                player,
                player.serverLevel(),
                pos != null ? pos : player.blockPosition(),
                target
        );

        BlockPos actionPos = pos != null ? pos : player.blockPosition();
        String dimension = player.serverLevel().dimension().location().getPath();
        LOGGER.info("[D20 4% Hit] {} triggered {} at [{}: {}, {}, {}] | MicroRoll: {} | Active Tier: {} ({})",
                player.getScoreboardName(), category.name(), dimension,
                actionPos.getX(), actionPos.getY(), actionPos.getZ(),
                microRoll, tier.getLevel(), tier.getTitle());

        boolean executed = D20EventDispatcher.dispatchRandom(category, tier, ctx);

        // Synchronize state immediately to client HUD
        syncToPlayer(player);

        // Feedback in Action Bar
        String msg = String.format("§6🎲 [%d] %s §7-> %s",
                microRoll, category.getDisplayName(),
                executed ? "§aTriggered!" : "§7(Neutral / No Module)");
        player.sendSystemMessage(Component.literal(msg), true);

        return executed;
    }

    public static void syncToPlayer(ServerPlayer player) {
        if (player == null || player.connection == null) return;
        PlayerD20Attachment data = ModAttachments.getData(player);
        D20SyncPayload payload = new D20SyncPayload(
                data.getLastRolledNumber(),
                data.getGeneralDecision(),
                data.getCycleTimerTicks(),
                data.getChaosLevel(),
                getTriggerChance(),
                data.getLastEventId(),
                data.getLastTriggerTime()
        );
        PacketDistributor.sendToPlayer(player, payload);
    }
}
