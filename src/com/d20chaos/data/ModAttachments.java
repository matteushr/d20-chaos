package com.d20chaos.data;

import com.d20chaos.D20Chaos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, D20Chaos.MOD_ID);

    public static final Supplier<AttachmentType<PlayerD20Attachment>> PLAYER_D20_DATA =
            ATTACHMENT_TYPES.register("player_d20_data", () ->
                    AttachmentType.serializable(PlayerD20Attachment::new)
                            .copyOnDeath()
                            .build()
            );

    public static PlayerD20Attachment getData(Player player) {
        if (player instanceof net.neoforged.neoforge.attachment.IAttachmentHolder holder) {
            return holder.getData(PLAYER_D20_DATA);
        }
        return ((net.neoforged.neoforge.attachment.IAttachmentHolder) (Object) player).getData(PLAYER_D20_DATA);
    }
}
