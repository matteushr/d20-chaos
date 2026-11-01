package com.d20chaos.network;

import com.d20chaos.D20Chaos;
import com.d20chaos.client.ClientD20State;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record D20SyncPayload(
        int rolledNumber,
        int generalDecision,
        int ticksRemaining,
        int chaosLevel,
        float triggerChance,
        String lastEventId,
        long timestamp
) implements CustomPacketPayload {

    public static final Type<D20SyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(D20Chaos.MOD_ID, "d20_sync"));

    public static final StreamCodec<FriendlyByteBuf, D20SyncPayload> STREAM_CODEC =
            StreamCodec.ofMember(D20SyncPayload::write, D20SyncPayload::read);

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(rolledNumber);
        buf.writeInt(generalDecision);
        buf.writeInt(ticksRemaining);
        buf.writeInt(chaosLevel);
        buf.writeFloat(triggerChance);
        buf.writeUtf(lastEventId != null ? lastEventId : "none");
        buf.writeLong(timestamp);
    }

    public static D20SyncPayload read(FriendlyByteBuf buf) {
        return new D20SyncPayload(
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readFloat(),
                buf.readUtf(),
                buf.readLong()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(D20SyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientD20State.update(payload);
        });
    }
}
