package com.draconicarmorrewrite.mixin;

import com.brandon3055.draconicevolution.items.equipment.ModularHelmet;
import com.brandon3055.draconicevolution.items.equipment.ModularLeggings;
import com.brandon3055.draconicevolution.items.equipment.ModularBoots;
import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin({
    ModularHelmet.class,
    ModularLeggings.class,
    ModularBoots.class
})
public class ModularArmorNameFixMixin {

    @Inject(
        method = "getName",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onGetName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        Component baseName = Component.translatable(((net.minecraft.world.item.Item) (Object) this).getDescriptionId(stack));
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

    @Inject(
        method = "appendHoverText",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onAppendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        ((IModularArmor) this).addModularItemInformation(stack, world, tooltip, flag);
        ci.cancel();
    }
}
