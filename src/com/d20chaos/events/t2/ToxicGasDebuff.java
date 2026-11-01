package com.d20chaos.events.t2;

import com.d20chaos.api.ActionCategory;
import com.d20chaos.api.D20EventContext;
import com.d20chaos.api.DiceTier;
import com.d20chaos.api.ID20ActionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToxicGasDebuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToxicGasDebuff.class);

    @Override
    public String getId() {
        return "debuff_toxic_gas";
    }

    @Override
    public String getName() {
        return "Toxic Gas";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.MINING;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_2_DEBUFF;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        BlockPos pos = ctx.targetPos();
        if (player == null || level == null) return false;

        BlockPos emitPos = pos != null ? pos : player.blockPosition();

        // Apply Nausea I for 6 seconds (120 ticks) and Poison I for 4 seconds (80 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0, false, true));

        // Dense smoke and sulfur gas particles
        level.sendParticles(ParticleTypes.SNEEZE, emitPos.getX() + 0.5, emitPos.getY() + 0.5, emitPos.getZ() + 0.5,
                35, 0.5, 0.5, 0.5, 0.08);
        level.sendParticles(ParticleTypes.SMOKE, emitPos.getX() + 0.5, emitPos.getY() + 0.5, emitPos.getZ() + 0.5,
                20, 0.4, 0.4, 0.4, 0.05);

        level.playSound(null, emitPos.getX(), emitPos.getY(), emitPos.getZ(),
                SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 0.7f);

        player.sendSystemMessage(Component.literal("§6⚠ [PENALTY] §eSubterranean gas pocket ruptured! Nausea and poison."), true);
        LOGGER.info("[D20/ToxicGas] Toxic gas released for {} during mining at [{}, {}, {}].",
                player.getScoreboardName(), emitPos.getX(), emitPos.getY(), emitPos.getZ());
        return true;
    }
}
