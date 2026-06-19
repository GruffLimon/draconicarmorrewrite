package com.draconicarmorrewrite.mixin;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.client.model.ModularArmorModel;
import com.draconicarmorrewrite.ClassicModelLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModularArmorModel.class, remap = false)
public abstract class ModularArmorModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

    public ModularArmorModelMixin() { super(null); }

    @Shadow
    private EquipmentSlot slot;
    @Shadow
    private TechLevel techLevel;
    @Shadow
    private boolean isOnArmor;

    @Inject(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/item/ItemStack;IIF)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRender(LivingEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, ItemStack stack, int packedLight, int packedOverlay, float partialTicks, CallbackInfo ci) {
        if (!com.draconicarmorrewrite.DraconicArmorRewriteConfig.useClassicModels()) return;

        // Cancel original shader and VBO rendering completely!
        ci.cancel();

        if (entity instanceof net.minecraft.world.entity.decoration.ArmorStand) {
            this.head.visible = true;
            this.hat.visible = true;
            this.body.visible = true;
            this.leftLeg.visible = true;
            this.rightLeg.visible = true;
        }

        String tech = this.techLevel.name().toLowerCase();
        boolean isOnArmor = this.isOnArmor;

        // Render our classic textured OBJ parts directly to MultiBufferSource (vanilla)
        if (slot == EquipmentSlot.HEAD) {
            ClassicModelLoader.renderClassicPart(poseStack, bufferSource, tech, "helmet", isOnArmor, this.head, packedLight, packedOverlay);
        } else if (slot == EquipmentSlot.LEGS) {
            ClassicModelLoader.renderClassicPart(poseStack, bufferSource, tech, "belt", isOnArmor, this.body, packedLight, packedOverlay);
            ClassicModelLoader.renderClassicPart(poseStack, bufferSource, tech, "left_leg", isOnArmor, this.leftLeg, packedLight, packedOverlay);
            ClassicModelLoader.renderClassicPart(poseStack, bufferSource, tech, "right_leg", isOnArmor, this.rightLeg, packedLight, packedOverlay);
        } else if (slot == EquipmentSlot.FEET) {
            ClassicModelLoader.renderClassicPart(poseStack, bufferSource, tech, "left_boot", isOnArmor, this.leftLeg, packedLight, packedOverlay);
            ClassicModelLoader.renderClassicPart(poseStack, bufferSource, tech, "right_boot", isOnArmor, this.rightLeg, packedLight, packedOverlay);
        }
    }
}
