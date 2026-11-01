package com.d20chaos;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.loading.FMLEnvironment;

public class TitleHelper {

    public static void sendTitle(ServerPlayer player, Component title, Component subtitle) {
        // Standard Minecraft Title timings: 10 ticks (0.5s) fade in, 60 ticks (3s) stay, 20 ticks (1s) fade out
        sendTitle(player, title, subtitle, 10, 60, 20);
    }

    public static void sendTitle(ServerPlayer player, Component title, Component subtitle, int fadeIn, int stay, int fadeOut) {
        if (player == null || player.connection == null) return;

        // Standard native Minecraft Title packets (large font, smooth fade animations)
        player.connection.send(new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
        player.connection.send(new ClientboundSetTitleTextPacket(title));
        if (subtitle != null) {
            player.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
        }
    }

    public static void triggerTotemAnimation(ItemStack item) {
        if (FMLEnvironment.dist.isClient()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null) {
                mc.execute(() -> {
                    if (mc.gameRenderer != null) {
                        ItemStack displayStack = (item != null && !item.isEmpty()) ? item : Items.TOTEM_OF_UNDYING.getDefaultInstance();
                        mc.gameRenderer.displayItemActivation(displayStack);
                    }
                });
            }
        }
    }
}
