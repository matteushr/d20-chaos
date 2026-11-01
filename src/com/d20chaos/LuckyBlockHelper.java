package com.d20chaos;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class LuckyBlockHelper {
    public static final String LUCKY_BLOCK_ID = "lucky:lucky_block";

    public static ItemStack createLuckyBlock(int luck) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(LUCKY_BLOCK_ID));
        if (item == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(item);
        // Note: in 1.21.1 lucky block mod uses custom DataComponent 'lucky:luck'
        // We can execute /give or set custom components
        return stack;
    }

    public static void giveLuckyBlock(ServerPlayer player, int luck) {
        // Execute the native /lucky give or /give command to guarantee 100% component compatibility with the mod
        String cmd = String.format("give %s lucky:lucky_block[lucky:luck=%d] 1", player.getScoreboardName(), luck);
        player.server.getCommands().performPrefixedCommand(player.server.createCommandSourceStack(), cmd);
    }
}
