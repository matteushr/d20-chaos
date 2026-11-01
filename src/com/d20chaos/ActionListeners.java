package com.d20chaos;

import com.d20chaos.api.ActionCategory;
import com.d20chaos.data.ModAttachments;
import com.d20chaos.data.PlayerD20Attachment;
import com.d20chaos.engine.D20Manager;
import com.d20chaos.engine.PlayerConstraintHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pure action sensor layer: listens to player movement, mining, placement, combat and crafting.
 * Decoupled from event trap logic and state management.
 */
public class ActionListeners {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListeners.class);

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isSpectator() || !player.isAlive()) return;

        // Process any active movement constraints
        PlayerConstraintHelper.tickPlayerConstraints(player);
        if (PlayerConstraintHelper.isJumpLocked(player.getUUID())) {
            return; // While pinned, walk accumulator does not tick
        }

        double dx = player.getX() - player.xOld;
        double dz = player.getZ() - player.zOld;
        double dist = Math.sqrt(dx * dx + dz * dz);

        // Filter out tiny micro-jitters (<0.005) and teleports (>8.0)
        if (dist > 0.005 && dist < 8.0) {
            PlayerD20Attachment data = ModAttachments.getData(player);
            data.addWalkDistance((float) dist);

            // Trigger micro check every 1 block traveled
            if (data.getWalkAccumulator() >= 1.0f) {
                data.setWalkAccumulator(data.getWalkAccumulator() - 1.0f);
                D20Manager.checkActionTrigger(player, ActionCategory.MOVEMENT, player.blockPosition(), null);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (PlayerConstraintHelper.isJumpLocked(player.getUUID())) {
                PlayerConstraintHelper.cancelJumpImpulse(player);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (player.isCreative()) return;

        BlockState state = event.getState();
        if (state.getDestroySpeed(event.getLevel(), event.getPos()) < 0) return;

        D20Manager.checkActionTrigger(player, ActionCategory.MINING, event.getPos(), null);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isCreative()) return;

        BlockPos pos = event.getPos();
        D20Manager.checkActionTrigger(player, ActionCategory.BLOCK_PLACE, pos, null);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        LivingEntity victim = event.getEntity();
        if (victim == player) return;

        D20Manager.checkActionTrigger(player, ActionCategory.COMBAT_EXPLORE, victim.blockPosition(), victim);
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getCrafting().isEmpty()) return;

        D20Manager.checkActionTrigger(player, ActionCategory.CRAFTING, player.blockPosition(), null);
    }
}
