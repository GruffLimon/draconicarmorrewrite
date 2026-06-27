package com.draconicarmorrewrite.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record PacketShieldHit(int entityId, float shieldPower) {
    public static void encode(PacketShieldHit msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeFloat(msg.shieldPower);
    }

    public static PacketShieldHit decode(FriendlyByteBuf buf) {
        return new PacketShieldHit(buf.readInt(), buf.readFloat());
    }

    public static void handle(PacketShieldHit msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () -> {
                com.draconicarmorrewrite.client.ShieldClientPacketHandler.handle(msg);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
