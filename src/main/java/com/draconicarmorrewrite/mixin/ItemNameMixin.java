package com.draconicarmorrewrite.mixin;

import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemNameMixin {

    @Inject(
        method = "getName",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onGetName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        if (this instanceof IModularArmor) {
            Component baseName = Component.translatable(((Item) (Object) this).getDescriptionId(stack));
            com.brandon3055.brandonscore.api.TechLevel tech = ((IModularArmor) this).getTechLevel();
            
            net.minecraft.ChatFormatting color;
            if (tech == com.brandon3055.brandonscore.api.TechLevel.WYVERN) {
                color = net.minecraft.ChatFormatting.AQUA;
            } else if (tech == com.brandon3055.brandonscore.api.TechLevel.DRACONIC) {
                color = net.minecraft.ChatFormatting.LIGHT_PURPLE;
            } else if (tech == com.brandon3055.brandonscore.api.TechLevel.CHAOTIC) {
                color = net.minecraft.ChatFormatting.RED;
            } else {
                color = net.minecraft.ChatFormatting.WHITE;
            }
            
            cir.setReturnValue(baseName.copy().withStyle(color));
        }
    }
}
