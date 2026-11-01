package com.d20chaos.events.t2;

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
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StumbleDebuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(StumbleDebuff.class);

    @Override
    public String getId() {
        return "debuff_stumble";
    }

    @Override
    public String getName() {
        return "Tropeço";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.MOVEMENT;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_2_DEBUFF;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        if (player == null || level == null) return false;

        // Tropeço físico: impulso súbito para frente/lado com perda de equilíbrio
        Vec3 look = player.getLookAngle();
        player.setDeltaMovement(look.x * 0.4, 0.05, look.z * 0.4);
        player.hurtMarked = true;

        // Aplica Lentidão I por 5 segundos (100 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, true));

        // Partículas de poeira nos pés
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, player.getX(), player.getY(), player.getZ(),
                12, 0.3, 0.1, 0.3, 0.02);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH, SoundSource.PLAYERS, 1.0f, 0.9f);

        player.sendSystemMessage(Component.literal("§6⚠ [PENALIDADE] §eVocê tropeçou no próprio passo! Lentidão temporária."), true);
        LOGGER.info("[D20/Tropeco] {} tropeçou na locomoção em [{}, {}, {}].",
                player.getScoreboardName(), (int)player.getX(), (int)player.getY(), (int)player.getZ());
        return true;
    }
}
