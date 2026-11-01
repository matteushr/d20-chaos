package com.d20chaos.engine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clean, lightweight Forcefield Containment Manager.
 * Creates an impenetrable energy barrier without altering or destroying world terrain blocks.
 */
public class ForcefieldManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForcefieldManager.class);

    public static class ActiveForcefield {
        public final UUID playerUuid;
        public final ServerLevel level;
        public final BlockPos centerPos;
        public final double radius;
        public int remainingTicks;
        public int particleTick = 0;

        public ActiveForcefield(UUID playerUuid, ServerLevel level, BlockPos centerPos, double radius, int durationTicks) {
            this.playerUuid = playerUuid;
            this.level = level;
            this.centerPos = centerPos;
            this.radius = radius;
            this.remainingTicks = durationTicks;
        }

        public boolean isInside(BlockPos pos) {
            double dx = pos.getX() - centerPos.getX();
            double dz = pos.getZ() - centerPos.getZ();
            return (dx * dx + dz * dz) <= (radius * radius);
        }
    }

    private static final Map<UUID, ActiveForcefield> ACTIVE_FORCEFIELDS = new ConcurrentHashMap<>();

    public static boolean createForcefield(ServerPlayer player, BlockPos center, double radius, int durationTicks) {
        if (player == null) return false;
        ServerLevel level = player.serverLevel();
        BlockPos centerPos = center != null ? center : player.blockPosition();

        ActiveForcefield field = new ActiveForcefield(player.getUUID(), level, centerPos, radius, durationTicks);
        ACTIVE_FORCEFIELDS.put(player.getUUID(), field);

        level.playSound(null, centerPos.getX(), centerPos.getY(), centerPos.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 0.9f);

        player.sendSystemMessage(Component.literal("§c⚡ [FORCEFIELD] §6A Judgment Dome has sealed the perimeter around you!"), false);
        LOGGER.info("[D20/Forcefield] Forcefield activated for {} at [{}, {}, {}] (radius: {}m, duration: {}s).",
                player.getScoreboardName(), centerPos.getX(), centerPos.getY(), centerPos.getZ(), radius, durationTicks / 20);
        return true;
    }

    public static boolean isPlayerInForcefield(UUID playerUuid) {
        return playerUuid != null && ACTIVE_FORCEFIELDS.containsKey(playerUuid);
    }

    public static void clearForPlayer(UUID playerUuid) {
        ActiveForcefield removed = ACTIVE_FORCEFIELDS.remove(playerUuid);
        if (removed != null) {
            removed.level.playSound(null, removed.centerPos.getX(), removed.centerPos.getY(), removed.centerPos.getZ(),
                    SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 1.2f);
            LOGGER.info("[D20/Forcefield] Forcefield dissipated for {}.", playerUuid);
        }
    }

    public static void forceClearAll() {
        for (ActiveForcefield f : ACTIVE_FORCEFIELDS.values()) {
            f.level.playSound(null, f.centerPos.getX(), f.centerPos.getY(), f.centerPos.getZ(),
                    SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 1.2f);
        }
        ACTIVE_FORCEFIELDS.clear();
        LOGGER.info("[D20/Forcefield] All forcefields forcefully terminated.");
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (ACTIVE_FORCEFIELDS.isEmpty()) return;
        Vec3 center = event.getExplosion().center();
        BlockPos pos = BlockPos.containing(center);

        for (ActiveForcefield field : ACTIVE_FORCEFIELDS.values()) {
            if (field.isInside(pos)) {
                // Cancel block destruction: protects terrain, structures and chests while forcefield is active
                event.getExplosion().clearToBlow();
                break;
            }
        }
    }

    public static void onServerTick() {
        if (ACTIVE_FORCEFIELDS.isEmpty()) return;

        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, ActiveForcefield> entry : ACTIVE_FORCEFIELDS.entrySet()) {
            ActiveForcefield field = entry.getValue();
            ServerPlayer player = field.level.getServer().getPlayerList().getPlayer(field.playerUuid);

            if (player != null && player.isAlive()) {
                double px = player.getX();
                double py = player.getY();
                double pz = player.getZ();

                double cx = field.centerPos.getX() + 0.5;
                double cz = field.centerPos.getZ() + 0.5;

                double distX = px - cx;
                double distZ = pz - cz;
                double currentDist = Math.sqrt(distX * distX + distZ * distZ);

                field.particleTick++;

                // 1. Proximity particle warning when within 1.5m of perimeter
                if (currentDist >= (field.radius - 1.5)) {
                    if (field.particleTick % 4 == 0) {
                        field.level.sendParticles(ParticleTypes.REVERSE_PORTAL, px, py + 1.0, pz, 10, 0.3, 0.5, 0.3, 0.05);
                    }
                }

                // 2. Barrier Contact: Repel player back toward center
                if (currentDist >= field.radius) {
                    double angle = Math.atan2(distZ, distX);
                    double pushX = -Math.cos(angle) * 0.75;
                    double pushZ = -Math.sin(angle) * 0.75;

                    player.setDeltaMovement(pushX, 0.25, pushZ);
                    player.hurtMarked = true;

                    field.level.playSound(null, px, py, pz, SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 0.8f, 1.5f);
                    field.level.sendParticles(ParticleTypes.PORTAL, px, py + 1.0, pz, 20, 0.4, 0.5, 0.4, 0.1);
                    player.sendSystemMessage(Component.literal("§c⚡ The Energy Forcefield repels your passage!"), true);
                }

                // 3. Hard teleport containment if escaped via lag or ender pearls
                if (currentDist >= (field.radius + 4.0)) {
                    player.teleportTo(cx, field.centerPos.getY(), cz);
                    field.level.playSound(null, cx, field.centerPos.getY(), cz, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
                    player.sendSystemMessage(Component.literal("§c🔒 The Forcefield prevents your escape!"), true);
                }
            }

            field.remainingTicks--;
            if (field.remainingTicks <= 0) {
                toRemove.add(entry.getKey());
            }
        }

        for (UUID uuid : toRemove) {
            clearForPlayer(uuid);
        }
    }
}
