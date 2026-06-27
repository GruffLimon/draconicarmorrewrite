package com.draconicarmorrewrite.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem;

import java.util.function.Supplier;

public class UpgradableArmorSettingsPacket {
    private final double speedMult;
    private final double jumpMult;
    private final boolean flightEnabled;

    public UpgradableArmorSettingsPacket(double speedMult, double jumpMult, boolean flightEnabled) {
        this.speedMult = speedMult;
        this.jumpMult = jumpMult;
        this.flightEnabled = flightEnabled;
    }

    public static void encode(UpgradableArmorSettingsPacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.speedMult);
        buf.writeDouble(msg.jumpMult);
        buf.writeBoolean(msg.flightEnabled);
    }

    public static UpgradableArmorSettingsPacket decode(FriendlyByteBuf buf) {
        return new UpgradableArmorSettingsPacket(buf.readDouble(), buf.readDouble(), buf.readBoolean());
    }

    public static void handle(UpgradableArmorSettingsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Update equipped armor pieces:
                for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.HEAD, EquipmentSlot.FEET}) {
                    ItemStack stack = player.getItemBySlot(slot);
                    if (!stack.isEmpty() && stack.getItem() instanceof ClassicUpgradeableArmorItem) {
                        CompoundTag settings = stack.getOrCreateTagElement("DESettings");
                        settings.putDouble("speed_mult", msg.speedMult);
                        settings.putDouble("jump_mult", msg.jumpMult);
                        if (slot == EquipmentSlot.CHEST) {
                            settings.putBoolean("flight_enabled", msg.flightEnabled);
                        }
                    }
                }
                
                // Also update held item if applicable:
                for (net.minecraft.world.InteractionHand hand : net.minecraft.world.InteractionHand.values()) {
                    ItemStack held = player.getItemInHand(hand);
                    if (!held.isEmpty() && held.getItem() instanceof ClassicUpgradeableArmorItem) {
                        CompoundTag settings = held.getOrCreateTagElement("DESettings");
                        settings.putDouble("speed_mult", msg.speedMult);
                        settings.putDouble("jump_mult", msg.jumpMult);
                        settings.putBoolean("flight_enabled", msg.flightEnabled);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
