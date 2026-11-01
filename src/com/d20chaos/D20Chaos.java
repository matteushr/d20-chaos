package com.d20chaos;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(D20Chaos.MOD_ID)
public class D20Chaos {
    public static final String MOD_ID = "d20chaos";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public D20Chaos(IEventBus modEventBus) {
        LOGGER.info("[D20 Chaos] Initializing D20 Chaos Mod for Minecraft 1.21.1 NeoForge...");

        // Detect third-party optional mod integrations
        com.d20chaos.integration.ModIntegrations.init();

        // Register custom D20 items
        ModItems.register(modEventBus);

        // Register NeoForge Attachment types (Sprint 1)
        com.d20chaos.data.ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        // Register Network Payloads (Sprint 2)
        modEventBus.addListener(com.d20chaos.network.D20Network::register);

        // Register client GUI layer if on client physical side
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(com.d20chaos.client.D20SlotHudOverlay::registerGuiLayers);
        }

        // Initialize modular D20 action event catalog
        com.d20chaos.events.ModEventRegistry.init();

        // Register gameplay action listeners to NeoForge EVENT_BUS (Dual-layer 4% engine)
        NeoForge.EVENT_BUS.register(ActionListeners.class);
        NeoForge.EVENT_BUS.register(ChestListener.class);
        NeoForge.EVENT_BUS.register(com.d20chaos.engine.ForcefieldManager.class);
        NeoForge.EVENT_BUS.register(D20Commands.class);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        com.d20chaos.engine.ForcefieldManager.onServerTick();
        HologramManager.onServerTick();
        com.d20chaos.engine.D20Manager.onServerTick(event.getServer());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ChaosManager.updatePlayerBar(player);
            com.d20chaos.engine.D20Manager.syncToPlayer(player);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ChaosManager.onDeath(player);
            com.d20chaos.engine.D20Manager.syncToPlayer(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ChaosManager.onLogout(event.getEntity().getUUID());
        com.d20chaos.engine.ForcefieldManager.clearForPlayer(event.getEntity().getUUID());
    }
}
