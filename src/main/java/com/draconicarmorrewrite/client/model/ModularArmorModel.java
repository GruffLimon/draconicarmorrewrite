package com.draconicarmorrewrite.client.model;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.client.render.EquippedItemModel;
import com.draconicarmorrewrite.ClassicModelLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;

public class ModularArmorModel<T extends LivingEntity>
extends HumanoidModel<T>
implements EquippedItemModel {
    private final EquipmentSlot slot;
    private final TechLevel techLevel;
    private final boolean isOnArmor;

    public ModularArmorModel(TechLevel techLevel, boolean isOnArmor, EquipmentSlot slot) {
        super(createStandardRoot());
        this.techLevel = techLevel;
        this.isOnArmor = isOnArmor;
        this.slot = slot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        // Render nothing here, rendering is done in render(...) below
    }

    @Override
    public void render(LivingEntity entity, PoseStack poseStack, MultiBufferSource buffers, ItemStack stack, int packedLight, int packedOverlay, float partialTicks) {
        if (entity instanceof net.minecraft.world.entity.decoration.ArmorStand) {
            this.head.visible = true;
            this.hat.visible = true;
            this.body.visible = true;
            this.leftLeg.visible = true;
            this.rightLeg.visible = true;
        }

        String tech = this.techLevel.name().toLowerCase(java.util.Locale.ROOT);

        if (slot == EquipmentSlot.HEAD) {
            ClassicModelLoader.renderClassicPart(poseStack, buffers, tech, "helmet", isOnArmor, this.head, packedLight, packedOverlay);
        } else if (slot == EquipmentSlot.LEGS) {
            ClassicModelLoader.renderClassicPart(poseStack, buffers, tech, "belt", isOnArmor, this.body, packedLight, packedOverlay);
            ClassicModelLoader.renderClassicPart(poseStack, buffers, tech, "left_leg", isOnArmor, this.leftLeg, packedLight, packedOverlay);
            ClassicModelLoader.renderClassicPart(poseStack, buffers, tech, "right_leg", isOnArmor, this.rightLeg, packedLight, packedOverlay);
        } else if (slot == EquipmentSlot.FEET) {
            ClassicModelLoader.renderClassicPart(poseStack, buffers, tech, "left_boot", isOnArmor, this.leftLeg, packedLight, packedOverlay);
            ClassicModelLoader.renderClassicPart(poseStack, buffers, tech, "right_boot", isOnArmor, this.rightLeg, packedLight, packedOverlay);
        }
    }

    private static ModelPart createStandardRoot() {
        net.minecraft.client.model.geom.builders.MeshDefinition mesh = HumanoidModel.createMesh(net.minecraft.client.model.geom.builders.CubeDeformation.NONE, 0.0F);
        return net.minecraft.client.model.geom.builders.LayerDefinition.create(mesh, 64, 32).bakeRoot();
    }
}
