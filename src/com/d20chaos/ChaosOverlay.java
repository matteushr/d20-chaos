package com.d20chaos;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

public class ChaosOverlay {

    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(D20Chaos.MOD_ID, "chaos_hud"), ChaosOverlay::render);
    }

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        int chaos = ChaosManager.getChaos(mc.player.getUUID());
        int rollsLeft = ChaosManager.getRollsLeft(mc.player.getUUID());

        // Top-Left Box: Fixed Chaos Meter Panel
        int x = 10;
        int y = 10;
        int width = 126;
        int height = 36;

        // Dark semi-transparent box
        graphics.fill(x, y, x + width, y + height, 0x88000000);

        // Border (colored dynamically by tier)
        int borderColor = getBorderColor(chaos);
        graphics.renderOutline(x, y, width, height, borderColor);

        // Text Line 1: 🎲 Chaos: X% (Tier)
        String tier = ChaosManager.getTierName(chaos);
        graphics.drawString(mc.font, "§6🎲 Chaos: §f" + chaos + "% §7(" + tier + ")", x + 6, y + 5, 0xFFFFFFFF, false);

        // Progress bar background & fill
        int barX = x + 6;
        int barY = y + 17;
        int barWidth = width - 12;
        int barHeight = 4;
        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);

        int fillWidth = (int) (barWidth * (chaos / 100.0f));
        if (fillWidth > 0) {
            graphics.fill(barX, barY, barX + fillWidth, barY + barHeight, borderColor);
        }

        // Text Line 3: Escalation counter (+5% in X rolls)
        graphics.drawString(mc.font, "§7+5% em " + rollsLeft + " rolagens", x + 6, y + 24, 0xFFAAAAAA, false);
    }

    private static int getBorderColor(int chaos) {
        if (chaos < 30) return 0xFFFFFF55; // Yellow
        if (chaos < 60) return 0xFFFFAA00; // Orange
        if (chaos < 90) return 0xFFFF5555; // Red
        return 0xFFAA00AA; // Purple
    }
}
