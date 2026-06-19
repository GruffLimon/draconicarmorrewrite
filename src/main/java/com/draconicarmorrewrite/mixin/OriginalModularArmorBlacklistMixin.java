package com.draconicarmorrewrite.mixin;

import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
    "com.brandon3055.draconicevolution.items.equipment.ModularHelmet",
    "com.brandon3055.draconicevolution.items.equipment.ModularLeggings",
    "com.brandon3055.draconicevolution.items.equipment.ModularBoots"
}, remap = false)
public class OriginalModularArmorBlacklistMixin {

    @Inject(method = "createHost", at = @At("RETURN"), remap = false)
    private void onCreateHost(ItemStack stack, CallbackInfoReturnable<ModuleHostImpl> cir) {
        ModuleHostImpl host = cir.getReturnValue();
        if (host != null) {
            host.blackListType(ModuleTypes.SHIELD_CONTROLLER);
        }
    }
}
