package com.d20chaos.events.t3;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SereneBreezeNeutral implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SereneBreezeNeutral.class);

    @Override
    public String getId() {
        return "neutral_serene_breeze";
    }

    @Override
    public String getName() {
        return "Serene Breeze";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.MOVEMENT;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_3_NEUTRAL;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        if (player == null || level == null) return false;

        // Purely cosmetic relief effect: white end rod particles and gentle chime
        level.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(),
                20, 0.5, 0.5, 0.5, 0.03);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 1.0f, 1.2f);

        player.sendSystemMessage(Component.literal("§f★ [NEUTRAL] §7A serene breeze blows... The world is in perfect balance."), true);
        LOGGER.info("[D20/SereneBreeze] Neutral zone confirmed for {} at [{}, {}, {}].",
                player.getScoreboardName(), (int)player.getX(), (int)player.getY(), (int)player.getZ());
        return true;
    }
}
