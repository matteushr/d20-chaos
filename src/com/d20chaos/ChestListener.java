package com.d20chaos;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ChestListener {

    @SubscribeEvent
    public static void onChestOpen(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.isCreative()) return;

        ServerLevel level = player.serverLevel();
        BlockPos pos = event.getPos();
        BlockEntity be = level.getBlockEntity(pos);

        // Only natural structure/dungeon chests with loot tables trigger D20 (never player base chests!)
        if (!(be instanceof RandomizableContainerBlockEntity container)) return;
        if (container.getLootTable() == null) return;

        // Roll D20 for natural chest discovery!
        D20Roller.RollResult result = D20Roller.roll(player, "Loot Chest");

        // Spawn 3D Hologram floating text above chest
        HologramManager.spawnHologram(
            level,
            pos.getX() + 0.5,
            pos.getY() + 1.2,
            pos.getZ() + 0.5,
            result.getHologramText(),
            40
        );

        switch (result.category) {
            case NATURAL_1:
                com.d20chaos.engine.ForcefieldManager.createForcefield(player, pos, 6.0, 300);
                break;

            case FAIL:
                // Ambush from chest!
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0f, 0.8f);
                for (int i = 0; i < 2; i++) {
                    Silverfish silverfish = EntityType.SILVERFISH.create(level);
                    if (silverfish != null) {
                        silverfish.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0, 0);
                        level.addFreshEntity(silverfish);
                    }
                }
                break;

            case SUCCESS:
                player.giveExperiencePoints(15);
                ItemEntity emeralds = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, new ItemStack(Items.EMERALD, 3));
                level.addFreshEntity(emeralds);
                break;

            case GREAT_SUCCESS:
                player.giveExperiencePoints(30);
                LuckyBlockHelper.giveLuckyBlock(player, 80);
                ItemEntity diamonds = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, new ItemStack(Items.DIAMOND, 2));
                level.addFreshEntity(diamonds);
                break;

            case NATURAL_20:
                player.giveExperiencePoints(60);
                LuckyBlockHelper.giveLuckyBlock(player, 100);
                ItemEntity jackpotDiamonds = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, new ItemStack(Items.DIAMOND, 6));
                level.addFreshEntity(jackpotDiamonds);
                break;

            default:
                break;
        }
    }
}
