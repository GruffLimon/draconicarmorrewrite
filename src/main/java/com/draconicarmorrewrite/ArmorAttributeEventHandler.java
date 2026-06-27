package com.draconicarmorrewrite;

import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = DraconicArmorRewrite.MODID)
public class ArmorAttributeEventHandler {

    private static final UUID BOOTS_UUID = UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B20");
    private static final UUID LEGGINGS_UUID = UUID.fromString("D8499B22-FCE4-4B32-AF99-822E70C4C443");
    private static final UUID CHESTPLATE_UUID = UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E");
    private static final UUID HELMET_UUID = UUID.fromString("2AD3E546-0FBE-4B20-9A4F-5D500C1EA765");

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IModularArmor) {
            if (!net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem()).getNamespace().equals("draconicarmorrewrite")) {
                return;
            }
            ArmorItem self = (ArmorItem) stack.getItem();
            if (event.getSlotType() == self.getEquipmentSlot()) {
                int armorValue = switch (self.getType()) {
                    case HELMET -> 3;
                    case CHESTPLATE -> 8;
                    case LEGGINGS -> 6;
                    case BOOTS -> 3;
                };

                UUID uuid = switch (self.getType()) {
                    case BOOTS -> BOOTS_UUID;
                    case LEGGINGS -> LEGGINGS_UUID;
                    case CHESTPLATE -> CHESTPLATE_UUID;
                    case HELMET -> HELMET_UUID;
                };

                event.removeAttribute(Attributes.ARMOR);
                event.removeAttribute(Attributes.ARMOR_TOUGHNESS);

                event.addModifier(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", armorValue, AttributeModifier.Operation.ADDITION));
                event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", 3.0, AttributeModifier.Operation.ADDITION));
            }
        }
    }
}
