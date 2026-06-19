package com.draconicarmorrewrite.mixin;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.client.model.ModularChestpieceModel;
import com.draconicarmorrewrite.ClassicModelLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModularChestpieceModel.class, remap = false)
public abstract class ModularChestpieceModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

    public ModularChestpieceModelMixin() { super(null); }

    @Shadow
    private TechLevel techLevel;

    @Unique
    private boolean draconicarmorrewrite$isOnArmor;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(TechLevel techLevel, boolean isOnArmor, CallbackInfo ci) {
        this.draconicarmorrewrite$isOnArmor = isOnArmor;
    }

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
            this.leftArm.visible = true;
            this.rightArm.visible = true;
            this.body.visible = true;
        }

        String tech = this.techLevel.name().toLowerCase();
        boolean isOnArmor = this.draconicarmorrewrite$isOnArmor;

        // Render our classic textured OBJ parts directly to MultiBufferSource (vanilla)
        ClassicModelLoader.renderClassicPart(poseStack, bufferSource, tech, "body", isOnArmor, this.body, packedLight, packedOverlay);
        ClassicModelLoader.renderClassicPart(poseStack, bufferSource, tech, "left_arm", isOnArmor, this.leftArm, packedLight, packedOverlay);
        ClassicModelLoader.renderClassicPart(poseStack, bufferSource, tech, "right_arm", isOnArmor, this.rightArm, packedLight, packedOverlay);
    }
}
