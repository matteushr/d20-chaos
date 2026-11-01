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
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReinforcedCraftBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReinforcedCraftBuff.class);

    @Override
    public String getId() {
        return "buff_reinforced_craft";
    }

    @Override
    public String getName() {
        return "Reinforced";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.CRAFTING;
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

        // Grants 40 experience points
        player.giveExperiencePoints(40);

        // Repairs 40 durability on main hand tool or offhand item
        ItemStack item = player.getMainHandItem();
        if (item.isEmpty() || !item.isDamageableItem()) {
            item = player.getOffhandItem();
        }

        if (!item.isEmpty() && item.isDamageableItem() && item.isDamaged()) {
            item.setDamageValue(Math.max(0, item.getDamageValue() - 40));
        }

        // Mystical enchantment particles
        level.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.0, player.getZ(),
                30, 0.5, 0.5, 0.5, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0f, 1.3f);

        player.sendSystemMessage(Component.literal("§a★ [BLESSING] §2Reinforced Crafting! Tool repaired and +40 XP granted."), true);
        LOGGER.info("[D20/Reinforced] Repair and XP granted to {} during crafting at [{}, {}, {}].",
                player.getScoreboardName(), (int)player.getX(), (int)player.getY(), (int)player.getZ());
        return true;
    }
}
