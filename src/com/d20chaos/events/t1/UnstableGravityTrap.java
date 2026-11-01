package com.d20chaos.events.t1;

import com.d20chaos.api.ActionCategory;
import com.d20chaos.api.D20EventContext;
import com.d20chaos.api.DiceTier;
import com.d20chaos.api.ID20ActionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnstableGravityTrap implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnstableGravityTrap.class);

    @Override
    public String getId() {
        return "trap_unstable_gravity";
    }

    @Override
    public String getName() {
        return "Unstable Gravity";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.BLOCK_PLACE;
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

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(Blocks.BEDROCK)) return false;

        // Remove the block and convert it to a falling physics block
        level.removeBlock(pos, false);
        FallingBlockEntity.fall(level, pos, state);

        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.GRAVEL_FALL, SoundSource.BLOCKS, 1.0f, 0.8f);

        player.sendSystemMessage(Component.literal("§4⚠ [CALAMITY] §cThe placed block lost physical support and fell!"), true);
        LOGGER.info("[D20/UnstableGravity] Block '{}' at [{}, {}, {}] placed by {} collapsed into falling entity.",
                state.getBlock().getName().getString(), pos.getX(), pos.getY(), pos.getZ(), player.getScoreboardName());
        return true;
    }
}
