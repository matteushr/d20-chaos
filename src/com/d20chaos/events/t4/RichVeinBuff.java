package com.d20chaos.events.t4;

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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RichVeinBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(RichVeinBuff.class);

    @Override
    public String getId() {
        return "buff_rich_vein";
    }

    @Override
    public String getName() {
        return "Rich Vein";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.MINING;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_4_BUFF;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        BlockPos pos = ctx.targetPos();
        if (player == null || level == null) return false;

        BlockPos dropPos = pos != null ? pos : player.blockPosition();

        // Grants Haste II for 20 seconds (400 ticks)
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 400, 1, false, true));

        // Bonus mineral drop
        ItemEntity bonus = new ItemEntity(level, dropPos.getX() + 0.5, dropPos.getY() + 0.5, dropPos.getZ() + 0.5,
                new ItemStack(Items.RAW_IRON, 2));
        level.addFreshEntity(bonus);

        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, dropPos.getX() + 0.5, dropPos.getY() + 0.5, dropPos.getZ() + 0.5,
                25, 0.4, 0.4, 0.4, 0.05);

        level.playSound(null, dropPos.getX(), dropPos.getY(), dropPos.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8f, 1.5f);

        player.sendSystemMessage(Component.literal("§a★ [BLESSING] §2Rich Vein revealed! Haste II active and bonus minerals."), true);
        LOGGER.info("[D20/RichVein] Haste II and bonus ore granted to {} at [{}, {}, {}].",
                player.getScoreboardName(), dropPos.getX(), dropPos.getY(), dropPos.getZ());
        return true;
    }
}
