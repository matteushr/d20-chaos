package com.d20chaos.events.t4;

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

public class MomentumBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(MomentumBuff.class);

    @Override
    public String getId() {
        return "buff_momentum";
    }

    @Override
    public String getName() {
        return "Ímpeto";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.MOVEMENT;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_4_BUFF;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        if (player == null || level == null) return false;

        // Concede Velocidade II e Salto II por 15 segundos (300 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 300, 1, false, true));

        // Partículas de rajada de vento e nuvens
        level.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.2, player.getZ(),
                20, 0.4, 0.2, 0.4, 0.05);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 1.0f, 1.5f);

        player.sendSystemMessage(Component.literal("§a★ [BÊNÇÃO] §2Ventos favoráveis aceleram seus passos! Velocidade II e Salto II."), true);
        LOGGER.info("[D20/Impeto] Velocidade II e Salto II concedidos a {} em [{}, {}, {}].",
                player.getScoreboardName(), (int)player.getX(), (int)player.getY(), (int)player.getZ());
        return true;
    }
}
