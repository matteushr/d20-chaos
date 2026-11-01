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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoweredGuardDebuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoweredGuardDebuff.class);

    @Override
    public String getId() {
        return "debuff_lowered_guard";
    }

    @Override
    public String getName() {
        return "Low Guard";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.COMBAT_EXPLORE;
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

        // Extra wear on main hand weapon/tool
        ItemStack weapon = player.getMainHandItem();
        if (!weapon.isEmpty() && weapon.isDamageableItem()) {
            weapon.hurtAndBreak(6, player, EquipmentSlot.MAINHAND);
        }

        // Apply Weakness I for 6 seconds (120 ticks) and Mining Fatigue for 6 seconds
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 0, false, true));

        level.sendParticles(ParticleTypes.CRIT, player.getX(), player.getY() + 1.0, player.getZ(),
                15, 0.4, 0.4, 0.4, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.0f, 1.1f);

        player.sendSystemMessage(Component.literal("§6⚠ [PENALTY] §eYour guard broke! Tool chipped and temporary weakness applied."), true);
        LOGGER.info("[D20/LowGuard] {} suffered broken guard in combat at [{}, {}, {}].",
                player.getScoreboardName(), (int)player.getX(), (int)player.getY(), (int)player.getZ());
        return true;
    }
}
