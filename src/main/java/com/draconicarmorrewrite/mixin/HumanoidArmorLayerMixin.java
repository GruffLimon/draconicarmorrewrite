package com.draconicarmorrewrite.mixin;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import com.draconicarmorrewrite.DraconicArmorRewriteConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels the vanilla HumanoidArmorLayer box rendering for DE modular armor
 * when classic models are enabled. The 3D custom model is rendered separately
 * by EquippedItemModelLayer, so we only need to suppress the vanilla flat boxes
 * to prevent z-fighting.
 */
@Mixin(value = HumanoidArmorLayer.class, remap = true)
public class HumanoidArmorLayerMixin {

    /**
     * Cancel the renderArmorPiece call for DE modular armor (wyvern/draconic)
     * when classic models are enabled. This prevents the vanilla flat armor box
     * from z-fighting with the 3D OBJ model rendered by EquippedItemModelLayer.
     */
    @Inject(
        method = "renderArmorPiece",
        at = @At("HEAD"),
        cancellable = true,
        remap = true
    )
    private void onRenderArmorPiece(PoseStack poseStack, MultiBufferSource buffers,
                                     LivingEntity entity, EquipmentSlot slot,
                                     int packedLight, HumanoidModel<?> armorModel,
                                     CallbackInfo ci) {
        if (!com.draconicarmorrewrite.DraconicArmorRewriteConfig.useClassicModels()) return;
        ItemStack stack = entity.getItemBySlot(slot);
        if (stack.isEmpty()) return;
        if (stack.getItem() instanceof IModularArmor) {
            net.minecraft.resources.ResourceLocation rl = net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(stack.getItem());
            if (rl != null && rl.getNamespace().equals("draconicarmorrewrite")) {
                // Cancel vanilla flat box rendering — the 3D model handles it
                ci.cancel();
            }
        }
    }
}
