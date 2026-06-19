package com.draconicarmorrewrite.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.brandon3055.draconicevolution.client.model.ModularChestpieceModel$CoreGemModelPart", remap = false)
public class CoreGemModelPartMixin {

    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIFFFF)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRender(PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay, float r, float g, float b, float a, CallbackInfo ci) {
        if (com.draconicarmorrewrite.DraconicArmorRewriteConfig.useClassicModels()) {
            ci.cancel();
        }
    }
}
