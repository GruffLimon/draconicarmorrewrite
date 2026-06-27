package com.draconicarmorrewrite.client;

import com.draconicarmorrewrite.DraconicArmorRewrite;
import com.draconicarmorrewrite.client.gui.GlobalConfigScreen;
import com.draconicarmorrewrite.network.ArmorShieldTogglePacket;
import com.draconicarmorrewrite.network.PacketHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import com.draconicarmorrewrite.client.gui.ArmorConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

@Mod.EventBusSubscriber(modid = DraconicArmorRewrite.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OASClient {

    /** Open armor config for a given stack — from equipment slot, inventory, or hand */
    public static void openArmorConfig(ItemStack stack, @org.jetbrains.annotations.Nullable EquipmentSlot equipmentSlot, @org.jetbrains.annotations.Nullable InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        Screen parent = mc.screen;
        if (equipmentSlot != null) {
            mc.setScreen(new ArmorConfigScreen(parent, stack, equipmentSlot));
        } else if (hand != null) {
            mc.setScreen(new ArmorConfigScreen(parent, stack, hand));
        } else {
            mc.setScreen(new ArmorConfigScreen(parent, stack, InteractionHand.MAIN_HAND));
        }
    }

    public static void openArmorConfig(ItemStack stack, int inventorySlot) {
        Minecraft mc = Minecraft.getInstance();
        Screen parent = mc.screen;
        mc.setScreen(new ArmorConfigScreen(parent, stack, inventorySlot));
    }


    public static int clientTicks = 0;

    public static final KeyMapping CONFIG_KEY = new KeyMapping(
            "key.draconicarmorrewrite.config",
            GLFW.GLFW_KEY_C,
            "key.categories.draconicarmorrewrite"
    );

    public static final KeyMapping SHIELD_TOGGLE_KEY = new KeyMapping(
            "key.draconicarmorrewrite.shield_toggle",
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.draconicarmorrewrite"
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(CONFIG_KEY);
        event.register(SHIELD_TOGGLE_KEY);
    }

    @SubscribeEvent
    public static void registerOverlays(net.minecraftforge.client.event.RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("classic_shield_hud", new com.draconicarmorrewrite.client.gui.ClassicShieldHud());
        event.registerAboveAll("classic_tool_hud", new com.draconicarmorrewrite.client.gui.ToolHudOverlay());
    }

    @Mod.EventBusSubscriber(modid = DraconicArmorRewrite.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
            if (event.phase == net.minecraftforge.event.TickEvent.Phase.END) {
                clientTicks++;
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    com.draconicarmorrewrite.client.gui.ClassicShieldHud.clientTick();
                    while (CONFIG_KEY.consumeClick()) {
                        if (mc.screen == null) {
                            mc.setScreen(new GlobalConfigScreen());
                        }
                    }
                    while (SHIELD_TOGGLE_KEY.consumeClick()) {
                        if (mc.screen == null) {
                            PacketHandler.CHANNEL.sendToServer(new ArmorShieldTogglePacket());
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onRenderPlayer(net.minecraftforge.client.event.RenderPlayerEvent.Post event) {
            net.minecraft.world.entity.player.Player player = event.getEntity();
            if (player == null) return;

            ShieldClientPacketHandler.ShieldHitInfo info = ShieldClientPacketHandler.playerShieldStatus.get(player.getUUID());
            if (info != null) {
                int elapsed = clientTicks - info.hitTick();
                if (elapsed > 5 || elapsed < 0) {
                    ShieldClientPacketHandler.playerShieldStatus.remove(player.getUUID());
                    return;
                }

                int ticksRemaining = 5 - elapsed;
                float p = info.shieldPower();

                renderShieldSphere(event.getPoseStack(), event.getMultiBufferSource(), player, p, ticksRemaining, event.getPackedLight());
            }
        }

        private static void renderShieldSphere(com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource buffers, net.minecraft.world.entity.player.Player player, float p, int ticksRemaining, int packedLight) {
            codechicken.lib.render.CCModel model = com.draconicarmorrewrite.ClassicModelLoader.getModel("shield", "sphere");
            if (model == null) return;

            poseStack.pushPose();
            poseStack.translate(0.0f, 0.9f, 0.0f);
            poseStack.scale(1.2f, 1.7f, 1.2f);

            float rotation = (clientTicks + Minecraft.getInstance().getFrameTime()) * 2.0f;
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(rotation * 0.5f));

            net.minecraft.resources.ResourceLocation texture = new net.minecraft.resources.ResourceLocation("draconicarmorrewrite", "textures/armor/classic/shield_sphere.png");
            net.minecraft.client.renderer.RenderType renderType = com.draconicarmorrewrite.ClassicModelLoader.getShieldRenderType(texture);
            com.mojang.blaze3d.vertex.VertexConsumer builder = buffers.getBuffer(renderType);

            org.joml.Matrix4f matrix = poseStack.last().pose();
            org.joml.Matrix3f normalMatrix = poseStack.last().normal();

            codechicken.lib.vec.Vector3[] normals = model.normals();
            codechicken.lib.vec.Vertex5[] verts = model.verts;

            int r = (int) ((1.0f - p) * 255);
            int g = 0;
            int b = (int) (p * 255);
            int a = (int) ((ticksRemaining / 5.0f) * 255);
            r = Math.max(0, Math.min(255, r));
            b = Math.max(0, Math.min(255, b));
            a = Math.max(0, Math.min(255, a));

            int packedOverlay = net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

            if (verts != null) {
                int vp = model.vp;
                for (int i = 0; i < verts.length; i += vp) {
                    for (int j = 0; j < 4; j++) {
                        int index = i + Math.min(j, vp - 1);
                        if (index < verts.length) {
                            codechicken.lib.vec.Vertex5 vert = verts[index];
                            codechicken.lib.vec.Vector3 normal = (normals != null && index < normals.length && normals[index] != null) ? normals[index] : codechicken.lib.vec.Vector3.Y_POS;

                            builder.vertex(matrix, (float) vert.vec.x, (float) vert.vec.y, (float) vert.vec.z)
                                   .color(r, g, b, a)
                                   .uv((float) vert.uv.u, (float) vert.uv.v)
                                   .overlayCoords(packedOverlay)
                                   .uv2(packedLight)
                                   .normal(normalMatrix, (float) normal.x, (float) normal.y, (float) normal.z)
                                   .endVertex();
                        }
                    }
                }
            }

            poseStack.popPose();
        }
    }
}
