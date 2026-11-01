package com.d20chaos.events;

import com.d20chaos.engine.D20EventDispatcher;
import com.d20chaos.events.t1.*;
import com.d20chaos.events.t2.*;
import com.d20chaos.events.t3.*;
import com.d20chaos.events.t4.*;
import com.d20chaos.events.t5.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dedicated bootstrap registry for all D20 action event modules across Tiers 1 to 5.
 */
public class ModEventRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModEventRegistry.class);
    private static boolean initialized = false;

    public static synchronized void init() {
        if (initialized) return;
        LOGGER.info("[D20/Registry] Registrando catálogo modular de eventos para todos os Tiers...");

        // Tier 1: Calamity / Curse Modules (Mortal)
        D20EventDispatcher.register(new InescapableAnvilTrap());
        D20EventDispatcher.register(new AbyssalNestTrap());
        D20EventDispatcher.register(new UnstableGravityTrap());
        D20EventDispatcher.register(new ReverseThornsTrap());
        D20EventDispatcher.register(new ScrapCraftTrap());

        // Tier 2: Debuff / Sabotage Modules (Penalidade)
        D20EventDispatcher.register(new StumbleDebuff());
        D20EventDispatcher.register(new ToxicGasDebuff());
        D20EventDispatcher.register(new BrittleBlockDebuff());
        D20EventDispatcher.register(new LoweredGuardDebuff());
        D20EventDispatcher.register(new ToolWearDebuff());

        // Tier 3: Neutral / Vanilla Equilibrium (Zona Neutra)
        D20EventDispatcher.register(new SereneBreezeNeutral());

        // Tier 4: Buff / Favorable Winds Modules (Bênção)
        D20EventDispatcher.register(new MomentumBuff());
        D20EventDispatcher.register(new RichVeinBuff());
        D20EventDispatcher.register(new ArchitectBuff());
        D20EventDispatcher.register(new HunterBuff());
        D20EventDispatcher.register(new ReinforcedCraftBuff());

        // Tier 5: Divine / God Mode Modules (Divino)
        D20EventDispatcher.register(new HermesStepsBuff());
        D20EventDispatcher.register(new MidasTransmutationBuff());
        D20EventDispatcher.register(new FreeBlockBuff());
        D20EventDispatcher.register(new ThorExecutionBuff());
        D20EventDispatcher.register(new MasterpieceCraftBuff());

        initialized = true;
        LOGGER.info("[D20/Registry] Catálogo completo de eventos inicializado com sucesso ({} módulos registrados).",
                D20EventDispatcher.getRegistry().size());
    }
}
