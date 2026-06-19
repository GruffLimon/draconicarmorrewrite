package com.draconicarmorrewrite.mixin;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.buffer.VBORenderType;
import codechicken.lib.vec.Matrix4;
import com.brandon3055.brandonscore.client.shader.BCShader;
import com.brandon3055.draconicevolution.client.model.ModularChestpieceModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.brandon3055.draconicevolution.client.model.ModularChestpieceModel$ChestpieceModelPart", remap = false)
public class ChestpieceModelPartMixin implements com.draconicarmorrewrite.IClassicArmorPart {

    @Shadow
    protected CCModel model;

    @Shadow
    protected RenderType baseType;

    @Shadow
    protected BCShader<?> shader;

    @Shadow
    protected VBORenderType renderType;

    @Unique
    private String draconicarmorrewrite$techLevel;
    @Unique
    private String draconicarmorrewrite$partName;
    @Unique
    private boolean draconicarmorrewrite$isOnArmor;

    @Override
    @Unique
    public void draconicarmorrewrite$setMetadata(String techLevel, String partName, boolean isOnArmor) {
        this.draconicarmorrewrite$techLevel = techLevel;
        this.draconicarmorrewrite$partName = partName;
        this.draconicarmorrewrite$isOnArmor = isOnArmor;
    }

    @Override
    @Unique
    public String draconicarmorrewrite$getTechLevel() {
        return this.draconicarmorrewrite$techLevel;
    }

    @Override
    @Unique
    public String draconicarmorrewrite$getPartName() {
        return this.draconicarmorrewrite$partName;
    }

    @Override
    @Unique
    public boolean draconicarmorrewrite$isOnArmor() {
        return this.draconicarmorrewrite$isOnArmor;
    }

    private static java.lang.reflect.Field visibleField;
    private static java.lang.reflect.Method translateAndRotateMethod;

    static {
        try {
            visibleField = net.minecraft.client.model.geom.ModelPart.class.getField("visible");
        } catch (NoSuchFieldException e) {
            try {
                visibleField = net.minecraft.client.model.geom.ModelPart.class.getField("f_104207_");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (visibleField != null) {
            visibleField.setAccessible(true);
        }

        try {
            translateAndRotateMethod = net.minecraft.client.model.geom.ModelPart.class.getMethod("translateAndRotate", PoseStack.class);
        } catch (NoSuchMethodException e) {
            try {
                translateAndRotateMethod = net.minecraft.client.model.geom.ModelPart.class.getMethod("m_104299_", PoseStack.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (translateAndRotateMethod != null) {
            translateAndRotateMethod.setAccessible(true);
        }
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIFFFF)V", at = @At("HEAD"), cancellable = true)
    private void onRender(PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay, float r, float g, float b, float a, CallbackInfo ci) {
        // Always cancel the original shader-based render when classic models are enabled
        if (!com.draconicarmorrewrite.DraconicArmorRewriteConfig.useClassicModels()) {
            return; // let original render happen
        }

        // ALWAYS cancel the original render - no shader rendering at all
        ci.cancel();

        // If this part is hidden, just cancel and return
        if ("hidden".equals(this.draconicarmorrewrite$partName)) {
            return;
        }

        // If we have no tech level set, this part wasn't tagged - just hide it
        if (this.draconicarmorrewrite$techLevel == null) {
            return;
        }

        // Check visibility
        boolean isVisible = true;
        if (visibleField != null) {
            try {
                isVisible = visibleField.getBoolean(this);
            } catch (Exception e) {
                // ignore
            }
        }
        if (!isVisible) {
            return;
        }

        // Render the classic textured 3D model - NO shaders
        poseStack.pushPose();
        if (translateAndRotateMethod != null) {
            try {
                translateAndRotateMethod.invoke(this, poseStack);
            } catch (Exception e) {
                // ignore
            }
        }

        String tech = this.draconicarmorrewrite$techLevel.toLowerCase();
        String part = this.draconicarmorrewrite$partName;
        boolean isDraconic = tech.equals("draconic") || tech.equals("chaotic");
        boolean isWyvern = tech.equals("wyvern");

        // 1. applyLegacyOffset
        if (isDraconic) {
            switch (part) {
                case "helmet": poseStack.translate(-0.033, -0.1, 0.1); break;
                case "body": poseStack.translate(0.0, 0.755, -0.03); break;
                case "right_arm": poseStack.translate(-0.205, 0.72, -0.05); break;
                case "left_arm": poseStack.translate(0.21, 0.72, -0.06); break;
                case "belt": poseStack.translate(0.0, 0.756, -0.04); break;
                case "right_leg": poseStack.translate(-0.085, 0.6, 0.0); break;
                case "left_leg": poseStack.translate(0.085, 0.6, 0.0); break;
                case "right_boot": poseStack.translate(-0.03, 0.76, 0.0); break;
                case "left_boot": poseStack.translate(0.03, 0.76, 0.0); break;
            }
        } else {
            switch (part) {
                case "helmet": poseStack.translate(0.0, -0.07, 0.0); break;
                case "body": poseStack.translate(0.0, 0.755, -0.03); break;
                case "right_arm": poseStack.translate(-0.21, 0.72, 0.0); break;
                case "left_arm": poseStack.translate(0.21, 0.72, 0.0); break;
                case "belt": poseStack.translate(0.0, 0.756, -0.04); break;
                case "right_leg": poseStack.translate(-0.085, 0.6, 0.0); break;
                case "left_leg": poseStack.translate(0.085, 0.6, 0.0); break;
                case "right_boot": poseStack.translate(-0.03, 0.76, 0.0); break;
                case "left_boot": poseStack.translate(0.03, 0.76, 0.0); break;
            }
        }

        // 2. legacyScale
        double scale = 0.0625;
        if (isDraconic) {
            switch (part) {
                case "helmet": scale = 0.07692308; break;
                case "right_arm": case "left_arm": scale = 0.07194245; break;
                case "right_leg": case "left_leg": case "right_boot": case "left_boot": scale = 0.071428575; break;
            }
        } else {
            switch (part) {
                case "right_arm": case "left_arm": scale = 0.072992705; break;
                case "right_leg": case "left_leg": case "right_boot": case "left_boot": scale = 0.071428575; break;
            }
        }

        double scaleX = 1.0;
        double scaleY = 1.0;
        double scaleZ = part.equals("helmet") ? 1.08 : 1.0;

        if (isWyvern) {
            switch (part) {
                case "helmet":
                    scaleX = 1.08; scaleY = 1.03; scaleZ = 1.08 * 1.06; break;
                case "body":
                    scaleX = 1.07; scaleY = 1.02; scaleZ = 1.07; break;
                case "belt":
                    scaleX = 1.07; scaleZ = 1.07; break;
                case "right_leg": case "left_leg": case "right_boot": case "left_boot":
                    scaleX = 1.04; scaleZ = 1.04; break;
            }
        }

        poseStack.scale((float) (scale * scaleX), (float) (scale * scaleY), (float) (scale * scaleZ));

        // 3. Rotation
        org.joml.Quaternionf rot = new org.joml.Quaternionf().fromAxisAngleRad(new org.joml.Vector3f(-1.0f, 0.0f, 1.0f).normalize(), (float) Math.PI);
        poseStack.mulPose(rot);

        if (this.draconicarmorrewrite$isOnArmor) {
            poseStack.translate(0.0, 0.0, -0.0625);
        }

        // 4. Render using plain vanilla RenderType - NO shaders, NO CCRenderState
        codechicken.lib.render.CCModel ccModel = com.draconicarmorrewrite.ClassicModelLoader.getModel(tech, part);
        if (ccModel != null) {
            net.minecraft.resources.ResourceLocation texture = new net.minecraft.resources.ResourceLocation("draconicarmorrewrite", "textures/armor/classic/" + tech + "_" + part + ".png");
            RenderType renderType = RenderType.entityCutoutNoCull(texture);
            com.mojang.blaze3d.vertex.VertexConsumer builder = buffers.getBuffer(renderType);

            org.joml.Matrix4f matrix = poseStack.last().pose();
            org.joml.Matrix3f normalMatrix = poseStack.last().normal();

            codechicken.lib.vec.Vector3[] normals = ccModel.normals();
            codechicken.lib.vec.Vertex5[] verts = ccModel.verts;

            if (verts != null) {
                for (int i = 0; i < verts.length; i++) {
                    codechicken.lib.vec.Vertex5 vert = verts[i];
                    codechicken.lib.vec.Vector3 normal = (normals != null && i < normals.length && normals[i] != null) ? normals[i] : codechicken.lib.vec.Vector3.Y_POS;

                    builder.vertex(matrix, (float) vert.vec.x, (float) vert.vec.y, (float) vert.vec.z)
                           .color(255, 255, 255, 255)
                           .uv((float) vert.uv.u, (float) vert.uv.v)
                           .overlayCoords(packedOverlay)
                           .uv2(packedLight)
                           .normal(normalMatrix, (float) normal.x, (float) normal.y, (float) normal.z)
                           .endVertex();
                }
            }
        }

        poseStack.popPose();
    }
}
