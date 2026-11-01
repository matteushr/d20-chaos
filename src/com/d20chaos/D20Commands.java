package com.d20chaos;

import com.d20chaos.api.D20EventContext;
import com.d20chaos.api.DiceTier;
import com.d20chaos.data.ModAttachments;
import com.d20chaos.data.PlayerD20Attachment;
import com.d20chaos.engine.D20EventDispatcher;
import com.d20chaos.engine.D20Manager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class D20Commands {

    private static final Logger LOGGER = LoggerFactory.getLogger(D20Commands.class);

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("d20")
                .requires(s -> s.hasPermission(2))
                // /d20 setTier <1-20> - Forces General Decision immediately
                .then(Commands.literal("setTier")
                    .then(Commands.argument("value", IntegerArgumentType.integer(1, 20))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            int val = IntegerArgumentType.getInteger(ctx, "value");
                            D20Manager.applyGeneralDecision(player, val, true);
                            DiceTier tier = DiceTier.fromDecision(val);
                            LOGGER.info("[D20/Comando] {} forçou Decisão Geral para {} ({}) para o jogador {}",
                                    ctx.getSource().getTextName(), val, tier.getTitle(), player.getScoreboardName());
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    String.format("§a[D20] Decisão Geral forçada para: §e%d §7(%s %s)",
                                            val, tier.getStatusTag(), tier.getTitle())
                            ), true);
                            return 1;
                        })
                    )
                )
                // /d20 list - Lists all registered modules
                .then(Commands.literal("list")
                    .executes(ctx -> {
                        var reg = D20EventDispatcher.getRegistry();
                        StringBuilder sb = new StringBuilder("§6=== [D20 Módulos Registrados (" + reg.size() + ")] ===\n");
                        for (var entry : reg.values()) {
                            sb.append(String.format("§e- §f%s §7(%s) [%s | %s]\n",
                                    entry.getId(), entry.getName(), entry.getCategory(), entry.getTier().getTitle()));
                        }
                        ctx.getSource().sendSuccess(() -> Component.literal(sb.toString().trim()), false);
                        return 1;
                    })
                )
                // /d20 trigger <event_id> - Forces instant execution of a registered module
                .then(Commands.literal("trigger")
                    .then(Commands.argument("event_id", StringArgumentType.word())
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            String id = StringArgumentType.getString(ctx, "event_id");
                            D20EventContext eventCtx = new D20EventContext(
                                    player, player.serverLevel(), player.blockPosition(), null
                            );
                            boolean ok = D20EventDispatcher.forceTriggerById(id, eventCtx);
                            if (ok) {
                                ctx.getSource().sendSuccess(() -> Component.literal(
                                        "§a[D20] Módulo '" + id + "' executado com sucesso!"
                                ), true);
                            } else {
                                ctx.getSource().sendFailure(Component.literal(
                                        "§c[D20] Módulo não encontrado ou falhou ao executar: " + id
                                ));
                            }
                            return ok ? 1 : 0;
                        })
                    )
                )
                // /d20 chance <0.0 - 1.0> - Changes global micro trigger rate (e.g. 1.0 for testing)
                .then(Commands.literal("chance")
                    .then(Commands.argument("rate", FloatArgumentType.floatArg(0.0f, 1.0f))
                        .executes(ctx -> {
                            float rate = FloatArgumentType.getFloat(ctx, "rate");
                            D20Manager.setTriggerChance(rate);
                            LOGGER.info("[D20/Comando] {} alterou a taxa global de gatilho para {}%.",
                                    ctx.getSource().getTextName(), (int) (rate * 100));
                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    String.format("§a[D20] Taxa global de gatilho alterada para: §e%.1f%%", rate * 100)
                            ), true);
                            return 1;
                        })
                    )
                )
                // /d20 timer [seconds] - Changes or checks the Macro Cycle duration
                .then(Commands.literal("timer")
                    .executes(ctx -> {
                        int dur = D20Manager.getCycleDurationTicks() / 20;
                        int m = dur / 60;
                        int s = dur % 60;
                        ctx.getSource().sendSuccess(() -> Component.literal(
                                String.format("§6[D20] Duração atual do ciclo: §e%02d:%02d §7(%d segundos). Use §e/d20 timer <segundos>§7 para alterar.",
                                        m, s, dur)
                        ), false);
                        return 1;
                    })
                    .then(Commands.argument("seconds", IntegerArgumentType.integer(1, 86400))
                        .executes(ctx -> {
                            int sec = IntegerArgumentType.getInteger(ctx, "seconds");
                            D20Manager.setCycleDurationTicks(sec * 20);

                            MinecraftServer server = ctx.getSource().getServer();
                            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                                PlayerD20Attachment pData = ModAttachments.getData(p);
                                pData.setCycleTimerTicks(sec * 20);
                                D20Manager.syncToPlayer(p);
                            }

                            int m = sec / 60;
                            int s = sec % 60;
                            String formatted = m > 0 ? String.format("%dm %02ds", m, s) : String.format("%ds", s);

                            LOGGER.info("[D20/Comando] {} alterou a duração do ciclo para {} segundos ({}).",
                                    ctx.getSource().getTextName(), sec, formatted);

                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    String.format("§a[D20] Tempo de ciclo alterado para: §e%s §7(%d segundos).\n§7O dado girará e trocará as categorias a cada %s.",
                                            formatted, sec, formatted)
                            ), true);
                            return 1;
                        })
                    )
                )
                // /d20 cycle <seconds> - Alias for /d20 timer
                .then(Commands.literal("cycle")
                    .then(Commands.argument("seconds", IntegerArgumentType.integer(1, 86400))
                        .executes(ctx -> {
                            int sec = IntegerArgumentType.getInteger(ctx, "seconds");
                            D20Manager.setCycleDurationTicks(sec * 20);

                            MinecraftServer server = ctx.getSource().getServer();
                            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                                PlayerD20Attachment pData = ModAttachments.getData(p);
                                pData.setCycleTimerTicks(sec * 20);
                                D20Manager.syncToPlayer(p);
                            }

                            int m = sec / 60;
                            int s = sec % 60;
                            String formatted = m > 0 ? String.format("%dm %02ds", m, s) : String.format("%ds", s);

                            LOGGER.info("[D20/Comando] {} alterou a duração do ciclo para {} segundos ({}).",
                                    ctx.getSource().getTextName(), sec, formatted);

                            ctx.getSource().sendSuccess(() -> Component.literal(
                                    String.format("§a[D20] Tempo de ciclo alterado para: §e%s §7(%d segundos).\n§7O dado girará e trocará as categorias a cada %s.",
                                            formatted, sec, formatted)
                            ), true);
                            return 1;
                        })
                    )
                )
                // /d20 nextCycle - Manually rolls the next Macro Cycle right now
                .then(Commands.literal("nextCycle")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        PlayerD20Attachment data = ModAttachments.getData(player);
                        int masterRoll = java.util.concurrent.ThreadLocalRandom.current().nextInt(1, 21);
                        D20Manager.applyGeneralDecision(player, masterRoll, true);
                        data.setCycleTimerTicks(D20Manager.getCycleDurationTicks());
                        D20Manager.syncToPlayer(player);

                        LOGGER.info("[D20/Comando] {} avançou manualmente o ciclo macro para o resultado {}.",
                                ctx.getSource().getTextName(), masterRoll);

                        ctx.getSource().sendSuccess(() -> Component.literal(
                                "§6[D20] Próximo ciclo acionado manualmente com sucesso!"
                        ), true);
                        return 1;
                    })
                )
                // /d20 status - Shows current Macro Tier, Timer, Chaos, and Chance
                .then(Commands.literal("status")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        PlayerD20Attachment data = ModAttachments.getData(player);
                        int dec = data.getGeneralDecision();
                        DiceTier tier = DiceTier.fromDecision(dec);
                        int minutes = (data.getCycleTimerTicks() / 20) / 60;
                        int seconds = (data.getCycleTimerTicks() / 20) % 60;
                        int dur = D20Manager.getCycleDurationTicks() / 20;

                        ctx.getSource().sendSuccess(() -> Component.literal(
                                String.format("§6=== [D20 Status] ===\n" +
                                                "§eDecisão Geral: §f%d §7(%s %s)\n" +
                                                "§eTempo Restante: §f%02d:%02d\n" +
                                                "§eDuração do Ciclo: §f%ds\n" +
                                                "§eCaos: §f%d%%\n" +
                                                "§eTaxa de Gatilho: §f%.1f%%",
                                        dec, tier.getStatusTag(), tier.getTitle(),
                                        minutes, seconds, dur, data.getChaosLevel(),
                                        D20Manager.getTriggerChance() * 100)
                        ), false);
                        return 1;
                    })
                )
                // Legacy debug commands
                .then(Commands.literal("roll")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        D20Roller.roll(player, "Manual");
                        return 1;
                    })
                )
                .then(Commands.literal("room")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        boolean created = com.d20chaos.engine.ForcefieldManager.createForcefield(player, player.blockPosition(), 7.0, 300);
                        if (!created) {
                            ctx.getSource().sendFailure(Component.literal("§cNão foi possível criar o campo agora."));
                        }
                        return 1;
                    })
                )
                .then(Commands.literal("clear")
                    .executes(ctx -> {
                        com.d20chaos.engine.ForcefieldManager.forceClearAll();
                        ctx.getSource().sendSuccess(() -> Component.literal("§a[D20] Todos os campos de força foram desfeitos!"), true);
                        return 1;
                    })
                )
                .then(Commands.literal("resetchaos")
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        ChaosManager.setChaos(player.getUUID(), 0);
                        ChaosManager.updatePlayerBar(player);
                        ctx.getSource().sendSuccess(() -> Component.literal("§a[D20] Medidor de Caos resetado para 0%!"), false);
                        return 1;
                    })
                )
                .then(Commands.literal("setchaos")
                    .then(Commands.argument("percent", IntegerArgumentType.integer(0, 100))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            int pct = IntegerArgumentType.getInteger(ctx, "percent");
                            ChaosManager.setChaos(player.getUUID(), pct);
                            ChaosManager.updatePlayerBar(player);
                            ctx.getSource().sendSuccess(() -> Component.literal("§a[D20] Medidor de Caos definido para: §e" + pct + "%"), false);
                            return 1;
                        })
                    )
                )
        );
    }
}
