package com.d20chaos.events.t5;

import com.d20chaos.api.ActionCategory;
import com.d20chaos.api.D20EventContext;
import com.d20chaos.api.DiceTier;
import com.d20chaos.api.ID20ActionEvent;
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

public class HermesStepsBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(HermesStepsBuff.class);

    @Override
    public String getId() {
        return "buff_hermes_steps";
    }

    @Override
    public String getName() {
        return "Passos de Hermes";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.MOVEMENT;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_5_DIVINE;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        if (player == null || level == null) return false;

        // Apply Speed II (15s) and Jump Boost I (15s)
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 0, false, true));

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.4f);
        level.sendParticles(ParticleTypes.GLOW, player.getX(), player.getY() + 0.2, player.getZ(),
                25, 0.4, 0.1, 0.4, 0.05);

        player.sendSystemMessage(Component.literal("§6⚡ [MODO DEUS] §ePASSOS DE HERMES! Velocidade e leveza divinas ativadas!"), true);
        LOGGER.info("[D20/Hermes] Passos de Hermes concedidos a {} (Speed II e Jump Boost I por 15s).", player.getScoreboardName());
        return true;
    }
}
