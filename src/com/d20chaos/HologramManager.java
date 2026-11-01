package com.d20chaos;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HologramManager {

    private static class FloatingHologram {
        final Display.TextDisplay entity;
        int ticksLeft;

        FloatingHologram(Display.TextDisplay entity, int ticksLeft) {
            this.entity = entity;
            this.ticksLeft = ticksLeft;
        }
    }

    private static final List<FloatingHologram> ACTIVE_HOLOGRAMS = new ArrayList<>();

    public static void spawnHologram(ServerLevel level, double x, double y, double z, Component text, int durationTicks) {
        Display.TextDisplay td = EntityType.TEXT_DISPLAY.create(level);
        if (td != null) {
            td.setPos(x, y, z);

            CompoundTag tag = new CompoundTag();
            tag.putString("text", Component.Serializer.toJson(text, level.registryAccess()));
            tag.putString("billboard", "center");
            tag.putInt("line_width", 200);
            tag.putString("alignment", "center");
            tag.putBoolean("see_through", false);
            tag.putBoolean("shadow", true);

            td.load(tag);
            level.addFreshEntity(td);

            ACTIVE_HOLOGRAMS.add(new FloatingHologram(td, durationTicks));
        }
    }

    public static void onServerTick() {
        if (ACTIVE_HOLOGRAMS.isEmpty()) return;

        Iterator<FloatingHologram> it = ACTIVE_HOLOGRAMS.iterator();
        while (it.hasNext()) {
            FloatingHologram holo = it.next();
            holo.ticksLeft--;

            if (holo.entity.isAlive()) {
                // Float gently upwards (+0.03 blocks per tick)
                holo.entity.setPos(holo.entity.getX(), holo.entity.getY() + 0.03, holo.entity.getZ());
            }

            if (holo.ticksLeft <= 0 || !holo.entity.isAlive()) {
                holo.entity.discard();
                it.remove();
            }
        }
    }
}
