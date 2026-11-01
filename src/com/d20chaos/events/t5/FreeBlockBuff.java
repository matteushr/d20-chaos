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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FreeBlockBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreeBlockBuff.class);

    @Override
    public String getId() {
        return "buff_free_block";
    }

    @Override
    public String getName() {
        return "Construção Infinita";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.BLOCK_PLACE;
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

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(Blocks.BEDROCK)) return false;

        Block block = state.getBlock();
        ItemStack refund = new ItemStack(block.asItem(), 1);
        if (!refund.isEmpty()) {
            boolean added = player.getInventory().add(refund);
            if (!added) {
                player.drop(refund, false);
            }
        }

        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0f, 1.2f);
        level.sendParticles(ParticleTypes.END_ROD,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                15, 0.3, 0.3, 0.3, 0.05);

        player.sendSystemMessage(Component.literal("§e🧱 [BÊNÇÃO INFINITA] §aConstrução gratuita! O bloco foi reembolsado!"), true);
        LOGGER.info("[D20/ConstrucaoInfinita] Bloco '{}' reembolsado para {} em [{}, {}, {}].",
                block.getName().getString(), player.getScoreboardName(), pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
