package com.d20chaos;

import com.d20chaos.integration.ModIntegrations;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuckyBlockHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LuckyBlockHelper.class);
    public static final String LUCKY_BLOCK_ID = "lucky:lucky_block";

    public static boolean isAvailable() {
        return ModIntegrations.isLuckyBlockLoaded();
    }

    public static ItemStack createLuckyBlock(int luck) {
        if (!isAvailable()) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(LUCKY_BLOCK_ID));
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item);
    }

    public static void giveLuckyBlock(ServerPlayer player, int luck) {
        if (!isAvailable()) {
            LOGGER.debug("[D20/Integration] Lucky Block not installed. Skipping Lucky Block grant for {}.", player.getScoreboardName());
            return;
        }
        // Execute the native /lucky give or /give command to guarantee 100% component compatibility with the mod
        String cmd = String.format("give %s lucky:lucky_block[lucky:luck=%d] 1", player.getScoreboardName(), luck);
        player.server.getCommands().performPrefixedCommand(player.server.createCommandSourceStack(), cmd);
    }
}
