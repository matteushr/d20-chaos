package com.d20chaos;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, D20Chaos.MOD_ID);

    public static final DeferredHolder<Item, Item> D20_NAT20 = ITEMS.register("d20_nat20", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> D20_NAT1 = ITEMS.register("d20_nat1", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> D20_CRIT = ITEMS.register("d20_crit", () -> new Item(new Item.Properties()));

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
