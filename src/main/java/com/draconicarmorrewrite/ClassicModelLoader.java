package com.draconicarmorrewrite;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.model.OBJParser;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ClassicModelLoader {
    private static final Map<String, Optional<CCModel>> MODEL_CACHE = new ConcurrentHashMap<>();

    public static CCModel getModel(String techLevel, String partName) {
        String key = techLevel.toLowerCase() + ":" + partName;
        return MODEL_CACHE.computeIfAbsent(key, k -> {
            String modelTech = techLevel.equalsIgnoreCase("chaotic") ? "draconic" : techLevel.toLowerCase();
            CCModel model = loadModel(new ResourceLocation(DraconicArmorRewrite.MODID, "models/armor/classic/" + modelTech + "_" + partName + ".obj"));
            return Optional.ofNullable(model);
        }).orElse(null);
    }

    private static final Map<ResourceLocation, RenderType> RENDER_TYPE_CACHE = new ConcurrentHashMap<>();

    public static RenderType getClassicArmorRenderType(ResourceLocation texture) {
        return RENDER_TYPE_CACHE.computeIfAbsent(texture, tex -> RenderType.create(
            "draconicarmorrewrite:classic_armor_" + tex.getPath().replace('/', '_').replace(':', '_'),
            com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY,
            com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLES,
            256,
            true, // affectsOutline
            false, // needsSorting
            RenderType.CompositeState.builder()
                .setShaderState(new net.minecraft.client.renderer.RenderStateShard.ShaderStateShard(net.minecraft.client.renderer.GameRenderer::getRendertypeEntityCutoutNoCullShader))
                .setTextureState(new net.minecraft.client.renderer.RenderStateShard.TextureStateShard(tex, false, false))
                .setTransparencyState(net.minecraft.client.renderer.RenderStateShard.NO_TRANSPARENCY)
                .setLightmapState(net.minecraft.client.renderer.RenderStateShard.LIGHTMAP)
                .setOverlayState(net.minecraft.client.renderer.RenderStateShard.OVERLAY)
                .setCullState(net.minecraft.client.renderer.RenderStateShard.NO_CULL)
                .createCompositeState(true) // depth write
        ));
    }

    private static CCModel loadModel(ResourceLocation location) {
        try {
            Map<String, CCModel> parsed = new OBJParser(location).ignoreMtl().parse();
            if (parsed.isEmpty()) {
                return null;
            }
            CCModel combined;
            if (parsed.size() == 1) {
                combined = parsed.values().iterator().next();
            } else {
                combined = CCModel.combine(parsed.values());
            }
            reverseWinding(combined);
            flipV(combined);
            combined.computeNormals();
            return combined;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void flipV(CCModel model) {
        if (model.verts != null) {
            for (int i = 0; i < model.verts.length; i++) {
                if (model.verts[i].uv != null) {
                    model.verts[i].uv.v = 1.0 - model.verts[i].uv.v;
                }
            }
        }
    }

    public static void reverseWinding(CCModel model) {
        if (model.verts != null) {
            int vp = model.vp;
            for (int i = 0; i < model.verts.length; i += vp) {
                for (int j = 0; j < vp / 2; j++) {
                    codechicken.lib.vec.Vertex5 temp = model.verts[i + j];
                    model.verts[i + j] = model.verts[i + vp - 1 - j];
                    model.verts[i + vp - 1 - j] = temp;
                }
            }
        }
    }

    public static void renderClassicPart(PoseStack poseStack, MultiBufferSource buffers, String tech, String part, boolean isOnArmor, net.minecraft.client.model.geom.ModelPart modelPart, int packedLight, int packedOverlay) {
        if (modelPart == null || !modelPart.visible) {
            return;
        }

        poseStack.pushPose();
        modelPart.translateAndRotate(poseStack);

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

        if (isOnArmor) {
            poseStack.translate(0.0, 0.0, -0.0625);
        }

        // 4. Render using plain vanilla RenderType - NO shaders, NO CCRenderState
        CCModel ccModel = getModel(tech, part);
        if (ccModel != null) {
            ResourceLocation texture = new ResourceLocation("draconicarmorrewrite", "textures/armor/classic/" + tech + "_" + part + ".png");
            RenderType renderType = getClassicArmorRenderType(texture);
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
