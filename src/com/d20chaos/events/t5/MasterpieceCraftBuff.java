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
import net.minecraft.world.entity.ExperienceOrb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterpieceCraftBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterpieceCraftBuff.class);

    @Override
    public String getId() {
        return "buff_masterpiece_craft";
    }

    @Override
    public String getName() {
        return "Masterpiece";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.CRAFTING;
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

        // Award bonus experience orbs
        ExperienceOrb.award(level, player.position(), 30);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.6f);
        level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(), player.getY() + 1.0, player.getZ(),
                20, 0.4, 0.4, 0.4, 0.1);

        player.sendSystemMessage(Component.literal("§e🔨 [MASTERPIECE] §aMagical creation! You received +30 mastery XP!"), true);
        LOGGER.info("[D20/Masterpiece] Granted +30 mastery XP to {} for crafting.", player.getScoreboardName());
        return true;
    }
}
