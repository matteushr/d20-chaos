package com.d20chaos.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class D20Network {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0.0");
        registrar.playToClient(
                D20SyncPayload.TYPE,
                D20SyncPayload.STREAM_CODEC,
                D20SyncPayload::handleClient
        );
    }
}
