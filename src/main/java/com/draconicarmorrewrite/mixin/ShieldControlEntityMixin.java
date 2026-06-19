package com.draconicarmorrewrite.mixin;

import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.ShieldData;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ShieldControlEntity.class, remap = false)
public abstract class ShieldControlEntityMixin extends ModuleEntity<com.brandon3055.draconicevolution.api.modules.data.ShieldControlData> {

    @Shadow
    private ShieldData shieldCache;
    @Shadow
    private boolean conflict;

    public ShieldControlEntityMixin() {
        super(null);
    }

    @Inject(method = "getShieldData", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGetShieldData(LivingEntity entity, CallbackInfoReturnable<ShieldData> cir) {
        if (this.shieldCache == null) {
            this.conflict = false;
            if (entity == null) {
                this.shieldCache = this.host.getModuleData(ModuleTypes.SHIELD_BOOST, new ShieldData(0, 0.0));
            } else {
                ShieldData combined = com.brandon3055.draconicevolution.api.modules.ModuleHelper.getCombinedEquippedData(entity, ModuleTypes.SHIELD_BOOST, new ShieldData(0, 0.0));
                int controllers = com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedModules(entity, ModuleTypes.SHIELD_CONTROLLER).size();
                if (controllers > 1) {
                    this.shieldCache = new ShieldData(combined.shieldCapacity() / controllers, combined.shieldRecharge() / controllers);
                } else {
                    this.shieldCache = combined;
                }
            }
        }
        cir.setReturnValue(this.shieldCache);
    }

    @org.spongepowered.asm.mixin.injection.Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/brandon3055/brandonscore/api/power/IOPStorage;getOPStored()J"), remap = false)
    private long onGetOPStored(com.brandon3055.brandonscore.api.power.IOPStorage storage, com.brandon3055.draconicevolution.api.modules.lib.ModuleContext moduleContext) {
        if (moduleContext instanceof com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext context) {
            LivingEntity entity = context.getEntity();
            if (entity != null) {
                long total = 0;
                for (net.minecraft.world.item.ItemStack stack : com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedHostItems(entity)) {
                    com.brandon3055.brandonscore.api.power.IOPStorage itemStorage = stack.getCapability(com.brandon3055.draconicevolution.api.capability.DECapabilities.OP_STORAGE).orElse(null);
                    if (itemStorage != null) {
                        total += itemStorage.getOPStored();
                    }
                }
                return total;
            }
        }
        return storage.getOPStored();
    }

    @org.spongepowered.asm.mixin.injection.Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/brandon3055/brandonscore/api/power/IOPStorage;modifyEnergyStored(J)J"), remap = false)
    private long onModifyEnergyStored(com.brandon3055.brandonscore.api.power.IOPStorage storage, long amount, com.brandon3055.draconicevolution.api.modules.lib.ModuleContext moduleContext) {
        if (moduleContext instanceof com.brandon3055.draconicevolution.api.modules.lib.StackModuleContext context) {
            LivingEntity entity = context.getEntity();
            if (entity != null) {
                long extracted = 0;
                long toExtractTotal = -amount;
                if (toExtractTotal > 0) {
                    for (net.minecraft.world.item.ItemStack stack : com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedHostItems(entity)) {
                        com.brandon3055.brandonscore.api.power.IOPStorage itemStorage = stack.getCapability(com.brandon3055.draconicevolution.api.capability.DECapabilities.OP_STORAGE).orElse(null);
                        if (itemStorage != null && toExtractTotal > 0) {
                            long stored = itemStorage.getOPStored();
                            long toExtract = Math.min(stored, toExtractTotal);
                            itemStorage.modifyEnergyStored(-toExtract);
                            extracted += toExtract;
                            toExtractTotal -= toExtract;
                        }
                    }
                    return -extracted;
                } else if (toExtractTotal < 0) {
                    return storage.modifyEnergyStored(amount);
                }
            }
        }
        return storage.modifyEnergyStored(amount);
    }
}
