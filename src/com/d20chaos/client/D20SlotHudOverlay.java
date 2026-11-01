package com.d20chaos.client;

import com.d20chaos.D20Chaos;
import com.d20chaos.api.ActionCategory;
import com.d20chaos.api.DiceTier;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public class D20SlotHudOverlay {

    private static final ResourceLocation D20_T1 = ResourceLocation.fromNamespaceAndPath(D20Chaos.MOD_ID, "textures/gui/d20_tier1.png");
    private static final ResourceLocation D20_T2 = ResourceLocation.fromNamespaceAndPath(D20Chaos.MOD_ID, "textures/gui/d20_tier2.png");
    private static final ResourceLocation D20_T3 = ResourceLocation.fromNamespaceAndPath(D20Chaos.MOD_ID, "textures/gui/d20_tier3.png");
    private static final ResourceLocation D20_T4 = ResourceLocation.fromNamespaceAndPath(D20Chaos.MOD_ID, "textures/gui/d20_tier4.png");
    private static final ResourceLocation D20_T5 = ResourceLocation.fromNamespaceAndPath(D20Chaos.MOD_ID, "textures/gui/d20_tier5.png");

    // =========================================================================
    // TIPOGRAFIA PIXEL ART AUTÊNTICA (3x5 DIGITS) - CÓPIA EXATA DA ARTE ORIGINAL
    // =========================================================================
    private static final byte[][] DIGIT_GLYPHS = {
        // 0 (3x5)
        { 1,1,1,  1,0,1,  1,0,1,  1,0,1,  1,1,1 },
        // 1 (2x5) - Esguio e elegante, exatamente como no 16 original
        { 1,1,    0,1,    0,1,    0,1,    1,1 },
        // 2 (3x5)
        { 1,1,1,  0,0,1,  1,1,1,  1,0,0,  1,1,1 },
        // 3 (3x5)
        { 1,1,1,  0,0,1,  0,1,1,  0,0,1,  1,1,1 },
        // 4 (3x5)
        { 1,0,1,  1,0,1,  1,1,1,  0,0,1,  0,0,1 },
        // 5 (3x5)
        { 1,1,1,  1,0,0,  1,1,1,  0,0,1,  1,1,1 },
        // 6 (3x5)
        { 0,1,1,  1,0,0,  1,1,1,  1,0,1,  1,1,1 },
        // 7 (3x5)
        { 1,1,1,  0,0,1,  0,1,0,  0,1,0,  0,1,0 },
        // 8 (3x5)
        { 1,1,1,  1,0,1,  1,1,1,  1,0,1,  1,1,1 },
        // 9 (3x5)
        { 1,1,1,  1,0,1,  1,1,1,  0,0,1,  1,1,0 }
    };
    private static final int[] DIGIT_WIDTHS = { 3, 2, 3, 3, 3, 3, 3, 3, 3, 3 };

    // Partículas ancoradas ao redor do dado: [relX, relY, formato (0=Cruz, 1=Quadrado, 2=Ponto), secundária (0/1), fase]
    private static final float[][] ANCHOR_PARTICLES = {
        { -4f,   6f, 0f, 1f, 0.0f }, // Cruz roxa esquerda
        { -3f,  20f, 1f, 0f, 1.2f }, // Quadrado dourado inferior-esquerdo
        {  4f,  -4f, 2f, 0f, 2.5f }, // Ponto dourado superior
        { 16f,  -6f, 1f, 0f, 0.7f }, // Quadrado dourado topo
        { 28f,  -3f, 0f, 0f, 3.1f }, // Cruz dourada superior-direita
        { 34f,   8f, 2f, 1f, 1.9f }, // Ponto roxo direita
        { 32f,  22f, 1f, 0f, 0.4f }, // Quadrado dourado inferior-direito
        { 22f,  32f, 0f, 1f, 2.8f }, // Cruz roxa base
        {  8f,  31f, 2f, 1f, 1.5f }, // Ponto roxo inferior
        { -5f,  13f, 2f, 0f, 3.8f }  // Ponto dourado esquerda
    };

    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(D20Chaos.MOD_ID, "d20_slot_hud"),
                D20SlotHudOverlay::render
        );
    }

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        // Estado sincronizado do D20
        DiceTier tier = ClientD20State.getCurrentTier();
        int ticksRemaining = ClientD20State.getTicksRemaining();
        int minutes = (ticksRemaining / 20) / 60;
        int seconds = (ticksRemaining / 20) % 60;
        int chaos = ClientD20State.getChaosLevel();
        float chance = ClientD20State.getTriggerChance();
        int rolledNum = ClientD20State.getLastRolledNumber();
        String lastEvent = ClientD20State.getLastEventId();

        float highlightProgress = ClientD20State.getHighlightProgress();
        boolean highlight = highlightProgress > 0.0f;

        int tierColor = tier.getColorRgb();
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        // =========================================================================
        // 1. CARDS NO CANTO SUPERIOR ESQUERDO
        // =========================================================================
        int startX = 10;
        int startY = 10;
        int panelWidth = 156;
        int cardHeight = 15;
        int gap = 2;

        // 1.1 Banner Superior da Tier
        graphics.fill(startX, startY, startX + panelWidth, startY + 24, 0xAA000000);
        graphics.renderOutline(startX, startY, panelWidth, 24, tierColor);

        // Linha 1: Nome da Tier + Tempo
        String tierTitle = tier.getTitle();
        graphics.drawString(mc.font, "§6§l" + tierTitle, startX + 5, startY + 4, 0xFFFFFFFF, true);
        String timerStr = String.format("§e%02d:%02d", minutes, seconds);
        graphics.drawString(mc.font, timerStr, startX + panelWidth - mc.font.width(timerStr) - 5, startY + 4, 0xFFFFFFFF, true);

        // Linha 2: Caos + Taxa de Gatilho
        String subInfo = String.format("§7Caos: §c%d%% §8| §7Gatilho: §e%.0f%%", chaos, chance * 100);
        graphics.drawString(mc.font, subInfo, startX + 5, startY + 14, 0xFFAAAAAA, true);

        // 1.2 Cards Individuais para Cada Ação Essencial
        int currentY = startY + 27;

        renderActionCard(graphics, mc, startX, currentY, panelWidth, cardHeight, "Mineração", tier.getArchetype(ActionCategory.MINING));
        currentY += cardHeight + gap;

        renderActionCard(graphics, mc, startX, currentY, panelWidth, cardHeight, "Locomoção", tier.getArchetype(ActionCategory.MOVEMENT));
        currentY += cardHeight + gap;

        renderActionCard(graphics, mc, startX, currentY, panelWidth, cardHeight, "Construção", tier.getArchetype(ActionCategory.BLOCK_PLACE));
        currentY += cardHeight + gap;

        renderActionCard(graphics, mc, startX, currentY, panelWidth, cardHeight, "Combate", tier.getArchetype(ActionCategory.COMBAT_EXPLORE));
        currentY += cardHeight + gap;

        renderActionCard(graphics, mc, startX, currentY, panelWidth, cardHeight, "Fabricação", tier.getArchetype(ActionCategory.CRAFTING));
        currentY += cardHeight + gap;

        // 1.3 Card do Último Gatilho (4%)
        int lastBg = highlight ? 0xCC553300 : 0x55000000;
        int lastBorder = highlight ? 0xFFFFEE55 : 0x22FFFFFF;
        graphics.fill(startX, currentY, startX + panelWidth, currentY + cardHeight, lastBg);
        graphics.renderOutline(startX, currentY, panelWidth, cardHeight, lastBorder);

        String lastText;
        if (highlight) {
            lastText = "§e★ GATILHO: §a" + lastEvent;
        } else if (lastEvent.equals("none")) {
            lastText = "§8Último: Aguardando...";
        } else {
            lastText = "§7Último: §f" + lastEvent;
        }
        graphics.drawString(mc.font, lastText, startX + 5, currentY + 4, 0xFFFFFFFF, true);

        // =========================================================================
        // 2. DADO D20 PIXEL-PERFECT (32x32 NATIVO 1:1) COM TIPOGRAFIA DA ARTE ORIGINAL
        // =========================================================================
        int d20Size = 32;
        int d20X = (screenWidth - d20Size) / 2;
        int d20Y = screenHeight - 78; // Flutua perfeitamente acima da vida e da fome

        // Textura base 32x32 nítida 1:1
        ResourceLocation d20Texture = switch (tier) {
            case TIER_1_CURSE -> D20_T1;
            case TIER_2_DEBUFF -> D20_T2;
            case TIER_3_NEUTRAL -> D20_T3;
            case TIER_4_BUFF -> D20_T4;
            case TIER_5_DIVINE -> D20_T5;
        };

        // Renderiza partículas orgânicas da arte de referência (Cruzes, estrelas e poeira estelar)
        renderSurroundingEffects(graphics, tier, d20X, d20Y, highlightProgress);

        // Renderiza o dado D20 em amostragem nativa 1:1 (ZERO distorção, contorno limpo)
        graphics.blit(d20Texture, d20X, d20Y, 32, 32, 0.0f, 0.0f, 32, 32, 32, 32);

        // Renderiza o número estampado com a micro-fonte pixel art exata da arte de referência
        if (rolledNum > 0) {
            renderPixelArtNumber(graphics, rolledNum, d20X, d20Y, highlight);
        }

        // Mini toast flutuante estilizado acima do D20 ao acionar micro-rolagem de 4%
        if (highlight && lastEvent != null && !lastEvent.equals("none")) {
            String toast = "§e★ " + lastEvent;
            int toastWidth = mc.font.width(toast);
            int toastX = (screenWidth - toastWidth) / 2;
            int toastY = d20Y - 14;

            graphics.fill(toastX - 4, toastY - 2, toastX + toastWidth + 4, toastY + 10, 0xCC000000);
            graphics.renderOutline(toastX - 4, toastY - 2, toastWidth + 8, 12, tierColor);
            graphics.drawString(mc.font, toast, toastX, toastY, 0xFFFFFFFF, true);
        }
    }

    private static void renderActionCard(GuiGraphics graphics, Minecraft mc, int x, int y, int width, int height, String actionName, String archetype) {
        graphics.fill(x, y, x + width, y + height, 0x66000000);
        graphics.renderOutline(x, y, width, height, 0x33FFFFFF);

        graphics.drawString(mc.font, "§f" + actionName, x + 5, y + 4, 0xFFFFFFFF, true);

        int archWidth = mc.font.width(archetype);
        graphics.drawString(mc.font, archetype, x + width - archWidth - 5, y + 4, 0xFFFFFFFF, true);
    }

    // =========================================================================
    // 3. ESTAMPARIA DO NÚMERO EM MICRO-FONTE PIXEL ART (100% FIEL AO ORIGINAL)
    // =========================================================================
    private static void renderPixelArtNumber(GuiGraphics graphics, int rolledNum, int d20X, int d20Y, boolean highlight) {
        String numStr = String.valueOf(rolledNum);
        int totalWidth = 0;
        for (int i = 0; i < numStr.length(); i++) {
            int d = numStr.charAt(i) - '0';
            if (d >= 0 && d <= 9) {
                totalWidth += DIGIT_WIDTHS[d];
            }
        }
        totalWidth += (numStr.length() - 1); // 1 pixel de espaçamento entre dígitos

        // Centro exato da face triangular invertida na textura 32x32
        int startX = d20X + 18 - totalWidth / 2;
        int startY = d20Y + 15;

        // Cores da arte original: Lilás/Branco (#BEAAC3) com sombra profunda
        int textColor;
        int shadowColor = 0xCC110419;

        if (highlight) {
            textColor = 0xFFFFEE55; // Ouro no momento do gatilho
        } else if (rolledNum == 20) {
            textColor = 0xFFFFD700; // Ouro Divino Nat 20
        } else if (rolledNum == 1) {
            textColor = 0xFFFF4444; // Vermelho Sangue Nat 1
        } else {
            textColor = 0xFFBEAAC3; // Lilás estelar idêntico à arte de referência!
        }

        int curX = startX;
        for (int i = 0; i < numStr.length(); i++) {
            int d = numStr.charAt(i) - '0';
            if (d < 0 || d > 9) continue;
            byte[] glyph = DIGIT_GLYPHS[d];
            int gw = DIGIT_WIDTHS[d];

            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < gw; c++) {
                    if (glyph[r * gw + c] == 1) {
                        int px = curX + c;
                        int py = startY + r;
                        // Sombra sutil de 1 pixel abaixo
                        graphics.fill(px, py + 1, px + 1, py + 2, shadowColor);
                        // Pixel da fonte
                        graphics.fill(px, py, px + 1, py + 1, textColor);
                    }
                }
            }
            curX += gw + 1;
        }
    }

    // =========================================================================
    // 4. EFEITOS VISUAIS PROCEDURAIS VIVOS (CRUZES, POEIRA ESTELAR E AURA)
    // =========================================================================
    private static void renderSurroundingEffects(GuiGraphics graphics, DiceTier tier, int d20X, int d20Y, float highlightProgress) {
        long now = System.currentTimeMillis();
        double timeSec = (now % 1000000L) / 1000.0;
        boolean highlight = highlightProgress > 0.0f;

        int primaryColor = getParticlePrimaryColor(tier);
        int secondaryColor = getParticleSecondaryColor(tier);

        int centerX = d20X + 16;
        int centerY = d20Y + 16;

        // 4.1 Explosão radial em 8 direções ao acionar gatilho
        if (highlight) {
            int auraAlpha = (int) (highlightProgress * 70);
            int auraColor = (auraAlpha << 24) | (tier.getColorRgb() & 0x00FFFFFF);
            graphics.fill(centerX - 18, centerY - 18, centerX + 18, centerY + 18, auraColor);

            for (int i = 0; i < 8; i++) {
                double burstAngle = (i * Math.PI / 4.0) + (timeSec * 0.4);
                double burstDist = 20.0 + (1.0f - highlightProgress) * 16.0;
                int bx = centerX + (int) Math.round(Math.cos(burstAngle) * burstDist);
                int by = centerY + (int) Math.round(Math.sin(burstAngle) * burstDist);
                int burstAlpha = Math.min(255, (int) (highlightProgress * 255));
                int burstColor = (burstAlpha << 24) | ((i % 2 == 0 ? primaryColor : secondaryColor) & 0x00FFFFFF);
                drawSparkleCross(graphics, bx, by, burstColor);
            }
        }

        // 4.2 Partículas orgânicas ancoradas com leve flutuação (breathing)
        for (float[] def : ANCHOR_PARTICLES) {
            float baseRelX = def[0];
            float baseRelY = def[1];
            int shapeType = (int) def[2];
            boolean isSec = def[3] > 0.5f;
            float phase = def[4];

            // Flutuação suave de ±1 pixel
            int wobbleY = (int) Math.round(Math.sin(timeSec * 1.8 + phase));
            int wobbleX = (int) Math.round(Math.cos(timeSec * 1.2 + phase));

            int px = d20X + (int) baseRelX + wobbleX;
            int py = d20Y + (int) baseRelY + wobbleY;

            int baseColor = isSec ? secondaryColor : primaryColor;

            // Cintilação orgânica suave
            double pulse = 0.75 + 0.25 * Math.sin(timeSec * 2.2 + phase);
            int alpha = (int) Math.round(255 * pulse);
            int finalColor = (alpha << 24) | (baseColor & 0x00FFFFFF);

            if (shapeType == 0) {
                drawSparkleCross(graphics, px, py, finalColor);
            } else if (shapeType == 1) {
                drawSparkleSquare(graphics, px, py, finalColor);
            } else {
                drawSparkleDot(graphics, px, py, finalColor);
            }
        }
    }

    private static int getParticlePrimaryColor(DiceTier tier) {
        return switch (tier) {
            case TIER_1_CURSE   -> 0xFFFF2233; // Carmesim Sombrio
            case TIER_2_DEBUFF  -> 0xFFFF7700; // Laranja Ferrugem
            case TIER_3_NEUTRAL -> 0xFFE0E8FF; // Prata Estelar
            case TIER_4_BUFF    -> 0xFF22FF66; // Esmeralda Radiante
            case TIER_5_DIVINE  -> 0xFFFFD700; // Ouro Imperial (Fiel à arte)
        };
    }

    private static int getParticleSecondaryColor(DiceTier tier) {
        return switch (tier) {
            case TIER_1_CURSE   -> 0xFFFF8800; // Brasa Cúprica
            case TIER_2_DEBUFF  -> 0xFFEEAA22; // Bronze Queimado
            case TIER_3_NEUTRAL -> 0xFF77DDFF; // Ciano Diamante
            case TIER_4_BUFF    -> 0xFF66FFAA; // Menta Celestial
            case TIER_5_DIVINE  -> 0xFFC740E7; // Roxo Ametista (Fiel à arte)
        };
    }

    // Cruz de pixel art de 5 pixels (+)
    private static void drawSparkleCross(GuiGraphics graphics, int cx, int cy, int color) {
        graphics.fill(cx, cy, cx + 1, cy + 1, color);
        graphics.fill(cx, cy - 1, cx + 1, cy, color);
        graphics.fill(cx, cy + 1, cx + 1, cy + 2, color);
        graphics.fill(cx - 1, cy, cx, cy + 1, color);
        graphics.fill(cx + 1, cy, cx + 2, cy + 1, color);
    }

    // Bloco quadrado de pixel art de 2x2 pixels
    private static void drawSparkleSquare(GuiGraphics graphics, int cx, int cy, int color) {
        graphics.fill(cx, cy, cx + 2, cy + 2, color);
    }

    // Ponto estelar único de 1x1 pixel
    private static void drawSparkleDot(GuiGraphics graphics, int cx, int cy, int color) {
        graphics.fill(cx, cy, cx + 1, cy + 1, color);
    }
}
