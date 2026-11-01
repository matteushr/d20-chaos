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

public class HunterBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(HunterBuff.class);

    @Override
    public String getId() {
        return "buff_hunter";
    }

    @Override
    public String getName() {
        return "Hunter";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.COMBAT_EXPLORE;
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

        // Vampiric heal: regenerates 4 health points (2 full hearts)
        player.heal(4.0f);

        // Grants Strength II for 10 seconds (200 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1, false, true));

        // Healing and critical sweep particles
        level.sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.2, player.getZ(),
                8, 0.4, 0.4, 0.4, 0.05);
        level.sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getY() + 0.8, player.getZ(),
                3, 0.2, 0.2, 0.2, 0.0);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0f, 1.2f);

        player.sendSystemMessage(Component.literal("§a★ [BLESSING] §2Hunter's Strike! +2 Hearts healed and Strength II granted."), true);
        LOGGER.info("[D20/Hunter] Hunter's Strike triggered for {} at [{}, {}, {}].",
                player.getScoreboardName(), (int)player.getX(), (int)player.getY(), (int)player.getZ());
        return true;
    }
}
