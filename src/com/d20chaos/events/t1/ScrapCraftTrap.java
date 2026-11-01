package com.d20chaos.events.t1;

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

public class ScrapCraftTrap implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapCraftTrap.class);

    @Override
    public String getId() {
        return "trap_scrap_craft";
    }

    @Override
    public String getName() {
        return "Scrap Craft";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.CRAFTING;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_1_CURSE;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        if (player == null || level == null) return false;

        // Damage main hand tool if present, or play failure smoke
        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.isEmpty() && mainHand.isDamageableItem()) {
            mainHand.hurtAndBreak(15, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 0.7f);
        level.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1.0, player.getZ(),
                20, 0.3, 0.3, 0.3, 0.05);

        player.sendSystemMessage(Component.literal("§4🔨 [CALAMITY] §cThe materials crumbled and chipped your equipment!"), true);
        LOGGER.info("[D20/ScrapCraft] Equipment of {} chipped by 15 durability during crafting.", player.getScoreboardName());
        return true;
    }
}
