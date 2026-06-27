package com.draconicarmorrewrite.network;

import com.draconicarmorrewrite.config.OASArmorConfig;
import com.draconicarmorrewrite.config.OASArmorConfigKey;
import com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record ArmorShieldTogglePacket() {
    public static void encode(ArmorShieldTogglePacket packet, FriendlyByteBuf buffer) {
    }

    public static ArmorShieldTogglePacket decode(FriendlyByteBuf buffer) {
        return new ArmorShieldTogglePacket();
    }

    public static void handle(ArmorShieldTogglePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            boolean foundArmor = false;
            boolean anyDisabled = false;
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack stack = player.getItemBySlot(slot);
                Item item = stack.getItem();
                if (!(item instanceof ClassicUpgradeableArmorItem armor)) continue;
                foundArmor = true;
                if (!armor.isShieldEnabled(stack)) {
                    anyDisabled = true;
                }
            }
            if (!foundArmor) {
                player.sendSystemMessage(Component.translatable("msg.originalarmorstuff.no_classic_armor").withStyle(ChatFormatting.WHITE));
                return;
            }
            boolean next = anyDisabled;
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack stack = player.getItemBySlot(slot);
                if (!(stack.getItem() instanceof ClassicUpgradeableArmorItem)) continue;
                OASArmorConfig.setBoolean(stack, OASArmorConfigKey.ARMOR_SHIELD_ENABLED, next);
            }
            player.sendSystemMessage(Component.translatable(next ? "msg.originalarmorstuff.armor_shield_enabled" : "msg.originalarmorstuff.armor_shield_disabled").withStyle(ChatFormatting.WHITE));
            player.level().playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 0.4f, 1.1f);
            player.containerMenu.broadcastChanges();
        });
        context.setPacketHandled(true);
    }
}
