package com.draconicarmorrewrite.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import com.draconicarmorrewrite.network.PacketShieldHit;

public class ShieldClientPacketHandler {
    public static final Map<UUID, ShieldHitInfo> playerShieldStatus = new HashMap<>();

    public record ShieldHitInfo(float shieldPower, int hitTick) {}

    public static void handle(PacketShieldHit msg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            net.minecraft.world.entity.Entity entity = mc.level.getEntity(msg.entityId());
            if (entity instanceof Player player) {
                int currentTicks = com.draconicarmorrewrite.client.OASClient.clientTicks;
                playerShieldStatus.put(player.getUUID(), new ShieldHitInfo(msg.shieldPower(), currentTicks));
            }
        }
    }
}
