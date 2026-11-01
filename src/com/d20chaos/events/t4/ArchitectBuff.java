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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchitectBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectBuff.class);

    @Override
    public String getId() {
        return "buff_architect";
    }

    @Override
    public String getName() {
        return "Arquiteto";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.BLOCK_PLACE;
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
        if (player == null || level == null || pos == null) return false;

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;

        // Reembolso do bloco: adiciona o item de volta ao inventário
        ItemStack refund = new ItemStack(state.getBlock().asItem(), 1);
        if (!refund.isEmpty()) {
            player.getInventory().add(refund);
        }

        // Concede Resistência I por 15 segundos
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 0, false, true));

        level.sendParticles(ParticleTypes.WAX_ON, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                20, 0.3, 0.3, 0.3, 0.05);

        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 1.0f, 1.4f);

        player.sendSystemMessage(Component.literal("§a★ [BÊNÇÃO] §2Engenho do Arquiteto! Bloco reembolsado e Resistência ativada."), true);
        LOGGER.info("[D20/Arquiteto] Bloco '{}' reembolsado para {} em [{}, {}, {}].",
                state.getBlock().getName().getString(), player.getScoreboardName(), pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
