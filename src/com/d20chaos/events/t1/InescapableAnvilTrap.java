package com.d20chaos.events.t1;

import com.d20chaos.engine.PlayerConstraintHelper;
import com.d20chaos.api.ActionCategory;
import com.d20chaos.api.D20EventContext;
import com.d20chaos.api.DiceTier;
import com.d20chaos.api.ID20ActionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InescapableAnvilTrap implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(InescapableAnvilTrap.class);

    @Override
    public String getId() {
        return "trap_inescapable_anvil";
    }

    @Override
    public String getName() {
        return "Inescapable Anvil";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.MOVEMENT;
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

        // 1. Center the player precisely on the block grid and kill all momentum
        BlockPos playerBlock = player.blockPosition();
        double targetX = playerBlock.getX() + 0.5;
        double targetZ = playerBlock.getZ() + 0.5;
        player.teleportTo(targetX, player.getY(), targetZ);
        player.setDeltaMovement(0, 0, 0);
        player.connection.send(new ClientboundSetEntityMotionPacket(player));

        // 2. Lock movement (Slowness 255) and lock jumping for 25 ticks (1.25s)
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 254, false, false));
        PlayerConstraintHelper.applyJumpLock(player, 25);

        // 3. Spawn falling DAMAGED ANVIL directly above the player's head (4 blocks up)
        BlockPos spawnPos = playerBlock.above(4);
        FallingBlockEntity anvil = FallingBlockEntity.fall(level, spawnPos, Blocks.DAMAGED_ANVIL.defaultBlockState());

        if (anvil != null) {
            anvil.setPos(targetX, spawnPos.getY(), targetZ);
            anvil.setHurtsEntities(2.5f, 40); // Crushing contact damage
            anvil.disableDrop(); // Shatters without dropping block
            anvil.setDeltaMovement(0, -0.35, 0); // Immediate downward velocity
        }

        level.playSound(null, targetX, player.getY(), targetZ,
                SoundEvents.CHAIN_FALL, SoundSource.PLAYERS, 1.0f, 0.8f);

        player.sendSystemMessage(Component.literal("§4⛓ [CALAMITY] §cChains bind your feet! Look out above!"), true);
        LOGGER.info("[D20/Anvil] Damaged anvil dropped at [{}, {}, {}] onto player {}.",
                targetX, spawnPos.getY(), targetZ, player.getScoreboardName());
        return true;
    }
}
