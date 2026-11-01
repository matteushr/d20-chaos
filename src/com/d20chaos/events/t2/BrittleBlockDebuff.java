package com.d20chaos.events.t2;

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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrittleBlockDebuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrittleBlockDebuff.class);

    @Override
    public String getId() {
        return "debuff_brittle_block";
    }

    @Override
    public String getName() {
        return "Brittle";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.BLOCK_PLACE;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_2_DEBUFF;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        BlockPos pos = ctx.targetPos();
        if (player == null || level == null || pos == null) return false;

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(Blocks.BEDROCK)) return false;

        // Destroys the placed block without dropping the item (brittle material)
        level.destroyBlock(pos, false);

        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 0.8f);

        player.sendSystemMessage(Component.literal("§6⚠ [PENALTY] §eThe placed block was brittle and crumbled to dust!"), true);
        LOGGER.info("[D20/Brittle] Block at [{}, {}, {}] placed by {} crumbled.",
                pos.getX(), pos.getY(), pos.getZ(), player.getScoreboardName());
        return true;
    }
}
