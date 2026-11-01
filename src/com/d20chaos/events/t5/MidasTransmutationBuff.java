package com.d20chaos.events.t5;

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
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class MidasTransmutationBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(MidasTransmutationBuff.class);

    @Override
    public String getId() {
        return "buff_midas_transmutation";
    }

    @Override
    public String getName() {
        return "Toque de Midas";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.MINING;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_5_DIVINE;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        BlockPos pos = ctx.targetPos();
        if (player == null || level == null || pos == null) return false;

        // Choose reward: Gold Ingot (60%), Diamond (30%), Emerald (10%)
        int roll = ThreadLocalRandom.current().nextInt(100);
        Item rewardItem;
        int count;
        if (roll < 60) {
            rewardItem = Items.RAW_GOLD;
            count = ThreadLocalRandom.current().nextInt(2, 5);
        } else if (roll < 90) {
            rewardItem = Items.DIAMOND;
            count = ThreadLocalRandom.current().nextInt(1, 3);
        } else {
            rewardItem = Items.EMERALD;
            count = ThreadLocalRandom.current().nextInt(2, 6);
        }

        // Spawn item entity
        ItemEntity itemEntity = new ItemEntity(level,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                new ItemStack(rewardItem, count));
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);

        // Spawn bonus XP
        ExperienceOrb.award(level, player.position(), 15);

        // Sound and visual effects
        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.8f);
        level.sendParticles(ParticleTypes.WAX_OFF,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                25, 0.4, 0.4, 0.4, 0.1);

        player.sendSystemMessage(Component.literal(String.format(
                "§6✨ [BÊNÇÃO DE MIDAS] §eO bloco se transmutou em %dx %s!",
                count, rewardItem.getDescription().getString()
        )), true);

        LOGGER.info("[D20/Midas] Bloco transmutado em {}x {} (+15 XP) em [{}, {}, {}] para {}.",
                count, rewardItem.getDescription().getString(), pos.getX(), pos.getY(), pos.getZ(), player.getScoreboardName());

        return true;
    }
}
