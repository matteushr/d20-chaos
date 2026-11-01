package com.d20chaos.events.t1;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Vex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbyssalNestTrap implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbyssalNestTrap.class);

    @Override
    public String getId() {
        return "trap_abyssal_nest";
    }

    @Override
    public String getName() {
        return "Abyssal Nest";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.MINING;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_1_CURSE;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        BlockPos pos = ctx.targetPos();
        if (player == null || level == null || pos == null) return false;

        // Spawn Cave Spider
        CaveSpider spider = EntityType.CAVE_SPIDER.create(level);
        if (spider != null) {
            spider.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            spider.setTarget(player);
            level.addFreshEntity(spider);
        }

        // Spawn Vex with 30-second lifetime
        Vex vex = EntityType.VEX.create(level);
        if (vex != null) {
            vex.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            vex.setLimitedLife(600); // 30s = 600 ticks
            vex.setTarget(player);
            level.addFreshEntity(vex);
        }

        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.VEX_CHARGE, SoundSource.HOSTILE, 1.0f, 0.8f);
        level.sendParticles(ParticleTypes.SQUID_INK, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                30, 0.3, 0.3, 0.3, 0.05);

        player.sendSystemMessage(Component.literal("§4☠ [CALAMITY] §cThe ore fractured into an Abyssal Nest! Vex and Cave Spider spawned!"), true);
        LOGGER.info("[D20/AbyssalNest] Cave Spider and Vex spawned at [{}, {}, {}] targeting {}.",
                pos.getX(), pos.getY(), pos.getZ(), player.getScoreboardName());
        return true;
    }
}
