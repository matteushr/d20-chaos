package com.d20chaos.engine;

import com.d20chaos.D20Chaos;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Isolated service responsible for transient physical restrictions (e.g. jump lock, ground pinning).
 * Keeps sensor classes (ActionListeners) completely decoupled from event trap states.
 */
public class PlayerConstraintHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerConstraintHelper.class);

    private static final ResourceLocation JUMP_LOCK_ID = ResourceLocation.fromNamespaceAndPath(D20Chaos.MOD_ID, "player_jump_lock");
    private static final AttributeModifier JUMP_LOCK_MODIFIER = new AttributeModifier(
            JUMP_LOCK_ID,
            -1.0,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
    );

    private static final Map<UUID, Integer> JUMP_LOCKED_PLAYERS = new ConcurrentHashMap<>();

    public static void applyJumpLock(ServerPlayer player, int durationTicks) {
        if (player == null) return;
        JUMP_LOCKED_PLAYERS.put(player.getUUID(), durationTicks);

        AttributeInstance attr = player.getAttribute(Attributes.JUMP_STRENGTH);
        if (attr != null && !attr.hasModifier(JUMP_LOCK_ID)) {
            attr.addTransientModifier(JUMP_LOCK_MODIFIER);
        }
        LOGGER.debug("[D20/Constraint] Jump lock applied to {} for {} ticks.", player.getScoreboardName(), durationTicks);
    }

    public static boolean isJumpLocked(UUID playerUuid) {
        return playerUuid != null && JUMP_LOCKED_PLAYERS.containsKey(playerUuid);
    }

    public static void tickPlayerConstraints(ServerPlayer player) {
        if (player == null) return;
        UUID uuid = player.getUUID();
        Integer lockTicks = JUMP_LOCKED_PLAYERS.get(uuid);

        if (lockTicks != null) {
            if (lockTicks <= 1) {
                clearJumpLock(player);
            } else {
                JUMP_LOCKED_PLAYERS.put(uuid, lockTicks - 1);
                // Maintain ground containment while pinned
                player.setDeltaMovement(0, Math.min(0, player.getDeltaMovement().y), 0);
            }
        }
    }

    public static void clearJumpLock(ServerPlayer player) {
        if (player == null) return;
        JUMP_LOCKED_PLAYERS.remove(player.getUUID());
        AttributeInstance attr = player.getAttribute(Attributes.JUMP_STRENGTH);
        if (attr != null) {
            attr.removeModifier(JUMP_LOCK_ID);
        }
        LOGGER.debug("[D20/Constraint] Jump lock removed from {}.", player.getScoreboardName());
    }

    public static void cancelJumpImpulse(ServerPlayer player) {
        if (player == null) return;
        player.setDeltaMovement(0, -0.1, 0);
        player.connection.send(new ClientboundSetEntityMotionPacket(player));
    }
}
