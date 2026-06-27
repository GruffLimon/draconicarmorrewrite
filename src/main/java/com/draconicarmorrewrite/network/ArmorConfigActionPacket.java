package com.draconicarmorrewrite.network;

import com.draconicarmorrewrite.config.OASArmorConfig;
import com.draconicarmorrewrite.config.OASArmorConfigKey;
import com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem;
import com.draconicarmorrewrite.items.OASUpgradableItem;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record ArmorConfigActionPacket(InteractionHand hand, EquipmentSlot equipmentSlot, Integer inventorySlot, ArmorConfigAction action, Double value) {
    public ArmorConfigActionPacket(InteractionHand hand, EquipmentSlot equipmentSlot, ArmorConfigAction action) {
        this(hand, equipmentSlot, null, action, null);
    }

    public ArmorConfigActionPacket(InteractionHand hand, EquipmentSlot equipmentSlot, Integer inventorySlot, ArmorConfigAction action) {
        this(hand, equipmentSlot, inventorySlot, action, null);
    }

    public static void encode(ArmorConfigActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.hand != null);
        if (packet.hand != null) {
            buffer.writeEnum(packet.hand);
        }
        buffer.writeBoolean(packet.equipmentSlot != null);
        if (packet.equipmentSlot != null) {
            buffer.writeEnum(packet.equipmentSlot);
        }
        buffer.writeBoolean(packet.inventorySlot != null);
        if (packet.inventorySlot != null) {
            buffer.writeInt(packet.inventorySlot);
        }
        buffer.writeUtf(packet.action.name());
        buffer.writeBoolean(packet.value != null);
        if (packet.value != null) {
            buffer.writeDouble(packet.value);
        }
    }

    public static ArmorConfigActionPacket decode(FriendlyByteBuf buffer) {
        InteractionHand hand = buffer.readBoolean() ? buffer.readEnum(InteractionHand.class) : null;
        EquipmentSlot equipmentSlot = buffer.readBoolean() ? buffer.readEnum(EquipmentSlot.class) : null;
        Integer inventorySlot = buffer.readBoolean() ? buffer.readInt() : null;
        ArmorConfigAction action = decodeAction(buffer.readUtf());
        Double value = buffer.readBoolean() ? buffer.readDouble() : null;
        return new ArmorConfigActionPacket(hand, equipmentSlot, inventorySlot, action, value);
    }

    private static ArmorConfigAction decodeAction(String actionName) {
        try {
            return ArmorConfigAction.valueOf(actionName);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public static void handle(ArmorConfigActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || packet.action == null) {
                return;
            }
            ItemStack stack = ItemStack.EMPTY;
            if (packet.hand != null) {
                stack = player.getItemInHand(packet.hand);
            } else if (packet.equipmentSlot != null) {
                stack = player.getItemBySlot(packet.equipmentSlot);
            } else if (packet.inventorySlot != null) {
                stack = player.getInventory().getItem(packet.inventorySlot);
            }
            Item item = stack.getItem();
            if (item instanceof OASUpgradableItem) {
                if (item instanceof ClassicUpgradeableArmorItem armorItem) {
                    if (packet.value != null) {
                        armorItem.applyConfigValue(player, stack, packet.action, packet.value);
                    } else {
                        armorItem.applyConfigAction(player, stack, packet.action);
                    }
                }
                player.containerMenu.broadcastChanges();
            }
        });
        context.setPacketHandled(true);
    }
}
