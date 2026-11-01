package com.d20chaos;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;

public class D20Roller {
    private static final Random RANDOM = new Random();
    private static final Map<UUID, Integer> FORCED_ROLLS = new ConcurrentHashMap<>();

    public enum ResultCategory {
        NATURAL_1("§4§l💀 NATURAL 1 💀", "Falha Crítica!"),
        FAIL("§c§l💀 FALHA 💀", "Perda e Desastre!"),
        POOR("§7Desgaste", "Redução leve"),
        NORMAL("§fNormal", "Vanilla"),
        SUCCESS("§aSucesso!", "Drops x2 e XP"),
        GREAT_SUCCESS("§b★ GRANDE SUCESSO ★", "Drops x4 e Lucky Block!"),
        NATURAL_20("§6§l★ NATURAL 20 ★", "Jackpot Épico x8 + Lucky Block +100!");

        public final String title;
        public final String description;

        ResultCategory(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    public static class RollResult {
        public final int baseRoll;
        public final int luckMod;
        public final int total;
        public final ResultCategory category;

        public RollResult(int baseRoll, int luckMod, int total, ResultCategory category) {
            this.baseRoll = baseRoll;
            this.luckMod = luckMod;
            this.total = total;
            this.category = category;
        }

        public Component getHologramText() {
            switch (category) {
                case NATURAL_20:
                    return Component.literal("§6§l★ 20 - NATURAL 20 ★");
                case GREAT_SUCCESS:
                    return Component.literal("§b★ " + total + " - Great Success! ★");
                case SUCCESS:
                    return Component.literal("§a" + total + " - Success!");
                case NORMAL:
                    return Component.literal("§f" + total + " - Normal");
                case POOR:
                    return Component.literal("§7" + total + " - Poor");
                case FAIL:
                    return Component.literal("§c💀 " + total + " - Fail!");
                case NATURAL_1:
                    return Component.literal("§4§l💀 1 - NATURAL 1 💀");
                default:
                    return Component.literal("§6🎲 " + total);
            }
        }
    }

    public static void setForcedRoll(UUID uuid, int value) {
        FORCED_ROLLS.put(uuid, value);
    }

    public static RollResult roll(ServerPlayer player, String actionName) {
        UUID uuid = player.getUUID();
        int baseRoll;

        if (FORCED_ROLLS.containsKey(uuid)) {
            baseRoll = Math.max(1, Math.min(20, FORCED_ROLLS.remove(uuid)));
        } else {
            baseRoll = 1 + RANDOM.nextInt(20);
        }

        int luckMod = 0;
        if (player.hasEffect(MobEffects.LUCK)) {
            luckMod += player.getEffect(MobEffects.LUCK).getAmplifier() + 1;
        }
        if (player.hasEffect(MobEffects.UNLUCK)) {
            luckMod -= (player.getEffect(MobEffects.UNLUCK).getAmplifier() + 1);
        }

        ResultCategory category;
        int total = Math.max(1, Math.min(20, baseRoll + luckMod));

        if (baseRoll == 1) {
            category = ResultCategory.NATURAL_1;
            total = 1;
        } else if (baseRoll == 20) {
            category = ResultCategory.NATURAL_20;
            total = 20;
        } else if (total <= 3) {
            category = ResultCategory.FAIL;
        } else if (total <= 6) {
            category = ResultCategory.POOR;
        } else if (total <= 12) {
            category = ResultCategory.NORMAL;
        } else if (total <= 17) {
            category = ResultCategory.SUCCESS;
        } else {
            category = ResultCategory.GREAT_SUCCESS;
        }

        // Record roll towards chaos meter & sync to player D20 slot HUD
        ChaosManager.recordRoll(player);
        com.d20chaos.data.PlayerD20Attachment attachData = com.d20chaos.data.ModAttachments.getData(player);
        attachData.setLastRolledNumber(total);
        com.d20chaos.engine.D20Manager.syncToPlayer(player);

        int chaos = ChaosManager.getChaos(uuid);
        String tierColor = ChaosManager.getTierColor(chaos);

        // Send Action Bar message
        String luckStr = (luckMod >= 0 ? "+" : "") + luckMod + " luck";
        String msg = String.format("§6🎲 §f%d §7(%s) = §e%d §7- %s: §f%s  %s[%d%%]",
                baseRoll, luckStr, total, category.title, category.description, tierColor, chaos);
        player.sendSystemMessage(Component.literal(msg), true);

        // Pop-up Banner and Totem Screen Animations!
        switch (category) {
            case NATURAL_20:
                TitleHelper.sendTitle(player, Component.literal("§6§l★ NATURAL 20 ★"), Component.literal("§eJackpot Épico x8 + Lucky Block +100!"));
                TitleHelper.triggerTotemAnimation(new ItemStack(ModItems.D20_NAT20.get()));
                player.serverLevel().sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 40, 0.5, 0.5, 0.5, 0.2);
                player.serverLevel().sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                break;

            case NATURAL_1:
                TitleHelper.sendTitle(player, Component.literal("§4§l💀 NATURAL 1 💀"), Component.literal("§cCampo de Força do Julgamento!"));
                TitleHelper.triggerTotemAnimation(new ItemStack(ModItems.D20_NAT1.get()));
                player.serverLevel().sendParticles(ParticleTypes.WITCH, player.getX(), player.getY() + 1.0, player.getZ(), 35, 0.5, 0.5, 0.5, 0.1);
                player.serverLevel().sendParticles(ParticleTypes.SOUL_FIRE_FLAME, player.getX(), player.getY() + 1.0, player.getZ(), 25, 0.5, 0.5, 0.5, 0.05);
                break;

            case GREAT_SUCCESS:
                TitleHelper.sendTitle(player, Component.literal("§b§l★ CRITICAL " + total + " ★"), Component.literal("§bGrande Sucesso!"));
                TitleHelper.triggerTotemAnimation(new ItemStack(ModItems.D20_CRIT.get()));
                player.serverLevel().sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.5, 0.5, 0.5, 0.2);
                break;

            case FAIL:
                TitleHelper.sendTitle(player, Component.literal("§c§l💀 CRITICAL " + total + " 💀"), Component.literal("§cPerda de Recursos!"));
                break;

            default:
                break;
        }

        // Play sounds according to outcome
        playAudioFeedback(player, category);

        return new RollResult(baseRoll, luckMod, total, category);
    }

    private static void playAudioFeedback(ServerPlayer player, ResultCategory category) {
        double x = player.getX(), y = player.getY(), z = player.getZ();
        switch (category) {
            case NATURAL_20:
                player.serverLevel().playSound(null, x, y, z, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
                player.serverLevel().playSound(null, x, y, z, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
                break;
            case GREAT_SUCCESS:
                player.serverLevel().playSound(null, x, y, z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8f, 1.2f);
                player.serverLevel().playSound(null, x, y, z, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.7f, 1.4f);
                break;
            case SUCCESS:
                player.serverLevel().playSound(null, x, y, z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8f, 1.2f);
                break;
            case NATURAL_1:
                player.serverLevel().playSound(null, x, y, z, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.8f, 0.5f);
                player.serverLevel().playSound(null, x, y, z, SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.0f, 0.6f);
                player.serverLevel().playSound(null, x, y, z, SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1.0f, 0.5f);
                break;
            case FAIL:
                player.serverLevel().playSound(null, x, y, z, SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 0.8f, 0.8f);
                break;
            default:
                break;
        }
    }
}
