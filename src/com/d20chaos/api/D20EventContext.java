package com.d20chaos.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public record D20EventContext(
        ServerPlayer player,
        ServerLevel level,
        BlockPos targetPos,
        Entity targetEntity,
        CompoundTag extraData
) {
    public D20EventContext(ServerPlayer player, ServerLevel level, BlockPos targetPos, Entity targetEntity) {
        this(player, level, targetPos, targetEntity, new CompoundTag());
    }
}
