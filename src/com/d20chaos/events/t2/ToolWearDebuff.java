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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToolWearDebuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolWearDebuff.class);

    @Override
    public String getId() {
        return "debuff_tool_wear";
    }

    @Override
    public String getName() {
        return "Desgaste";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.CRAFTING;
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

        // Desgaste súbito em ferramenta equipada na mão principal ou armadura
        ItemStack item = player.getMainHandItem();
        EquipmentSlot slot = EquipmentSlot.MAINHAND;
        if (item.isEmpty() || !item.isDamageableItem()) {
            item = player.getItemBySlot(EquipmentSlot.CHEST);
            slot = EquipmentSlot.CHEST;
        }

        if (!item.isEmpty() && item.isDamageableItem()) {
            item.hurtAndBreak(12, player, slot);
        }

        level.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1.0, player.getZ(),
                20, 0.3, 0.3, 0.3, 0.05);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.8f, 1.4f);

        player.sendSystemMessage(Component.literal("§6⚠ [PENALIDADE] §eA bancada lascou seus instrumentos! Desgaste de equipamento."), true);
        LOGGER.info("[D20/Desgaste] Equipamento desgastado para {} na fabricação em [{}, {}, {}].",
                player.getScoreboardName(), (int)player.getX(), (int)player.getY(), (int)player.getZ());
        return true;
    }
}
