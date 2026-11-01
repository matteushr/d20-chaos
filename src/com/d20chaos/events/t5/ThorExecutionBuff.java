package com.d20chaos.events.t5;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThorExecutionBuff implements ID20ActionEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThorExecutionBuff.class);

    @Override
    public String getId() {
        return "buff_thor_execution";
    }

    @Override
    public String getName() {
        return "Ira de Thor";
    }

    @Override
    public ActionCategory getCategory() {
        return ActionCategory.COMBAT_EXPLORE;
    }

    @Override
    public DiceTier getTier() {
        return DiceTier.TIER_5_DIVINE;
    }

    @Override
    public boolean execute(D20EventContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = ctx.level();
        Entity target = ctx.targetEntity();
        if (player == null || level == null) return false;

        Entity strikeTarget = target != null ? target : player;

        // Summon visual lightning bolt that doesn't incinerate item drops
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        if (lightning != null) {
            lightning.moveTo(strikeTarget.position());
            lightning.setVisualOnly(true);
            level.addFreshEntity(lightning);
        }

        // Deal 12.0 bonus divine lightning damage to target if it's a living entity
        if (strikeTarget instanceof LivingEntity living && strikeTarget != player) {
            living.hurt(level.damageSources().lightningBolt(), 12.0f);
        }

        level.playSound(null, strikeTarget.getX(), strikeTarget.getY(), strikeTarget.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1.0f, 1.2f);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                strikeTarget.getX(), strikeTarget.getY() + 1.0, strikeTarget.getZ(),
                30, 0.5, 0.5, 0.5, 0.1);

        player.sendSystemMessage(Component.literal("§e⚡ [IRA DE THOR] §bRelâmpago divino aniquilou seu alvo!"), true);
        LOGGER.info("[D20/Thor] Raio de Thor invocado por {} contra '{}' (+12.0 dano de raio).",
                player.getScoreboardName(), strikeTarget.getName().getString());
        return true;
    }
}
