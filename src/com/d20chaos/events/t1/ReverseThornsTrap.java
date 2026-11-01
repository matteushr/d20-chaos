package com.d20chaos.events.t1;

import com.d20chaos.api.ActionCategory;
import com.d20chaos.api.D20EventContext;
import com.d20chaos.api.DiceTier;
import com.d20chaos.api.ID20ActionEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReverseThornsTrap implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseThornsTrap.class);

    @Override
    public String getId() {
        return "trap_reverse_thorns";
    }

    @Override
    public String getName() {
        return "Reverse Thorns";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.COMBAT_EXPLORE;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_1_CURSE;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        Entity target = ctx.targetEntity();
        if (player == null || ctx.level() == null) return false;

        // Deal 3.0 damage (1.5 hearts) back to player
        player.hurt(ctx.level().damageSources().thorns(target != null ? target : player), 3.0f);

        ctx.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.THORNS_HIT, SoundSource.PLAYERS, 1.0f, 1.0f);

        player.sendSystemMessage(Component.literal("§4🌵 [CALAMITY] §cREVERSE THORNS! 50% of dealt damage bounced back at you!"), true);
        LOGGER.info("[D20/ReverseThorns] 3.0 thorns damage reflected back to {} (Remaining HP: {}/{}).",
                player.getScoreboardName(), (int) player.getHealth(), (int) player.getMaxHealth());
        return true;
    }
}
