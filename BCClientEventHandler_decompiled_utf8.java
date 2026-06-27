/*਍ഀ
 * Decompiled with CFR 0.152.਍ഀ
 * ਍ഀ
 * Could not load the following classes:਍ഀ
 *  codechicken.lib.colour.EnumColour਍ഀ
 *  codechicken.lib.render.RenderUtils਍ഀ
 *  codechicken.lib.render.buffer.TransformingVertexConsumer਍ഀ
 *  codechicken.lib.vec.Cuboid6਍ഀ
 *  codechicken.lib.vec.Vector3਍ഀ
 *  com.brandon3055.brandonscore.api.IFOVModifierItem਍ഀ
 *  com.brandon3055.brandonscore.blocks.BlockBCore਍ഀ
 *  com.brandon3055.brandonscore.client.render.BlockEntityRendererTransparent਍ഀ
 *  com.brandon3055.brandonscore.handlers.BCEventHandler਍ഀ
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat਍ഀ
 *  com.mojang.blaze3d.vertex.PoseStack਍ഀ
 *  com.mojang.blaze3d.vertex.VertexConsumer਍ഀ
 *  com.mojang.blaze3d.vertex.VertexFormat਍ഀ
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode਍ഀ
 *  net.covers1624.quack.util.CrashLock਍ഀ
 *  net.minecraft.client.Camera਍ഀ
 *  net.minecraft.client.Minecraft਍ഀ
 *  net.minecraft.client.multiplayer.ClientLevel਍ഀ
 *  net.minecraft.client.renderer.LevelRenderer਍ഀ
 *  net.minecraft.client.renderer.LevelRenderer$RenderChunkInfo਍ഀ
 *  net.minecraft.client.renderer.MultiBufferSource਍ഀ
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource਍ഀ
 *  net.minecraft.client.renderer.RenderStateShard਍ഀ
 *  net.minecraft.client.renderer.RenderStateShard$DepthTestStateShard਍ഀ
 *  net.minecraft.client.renderer.RenderType਍ഀ
 *  net.minecraft.client.renderer.RenderType$CompositeState਍ഀ
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher਍ഀ
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer਍ഀ
 *  net.minecraft.client.renderer.texture.OverlayTexture਍ഀ
 *  net.minecraft.core.BlockPos਍ഀ
 *  net.minecraft.resources.ResourceKey਍ഀ
 *  net.minecraft.world.entity.Entity਍ഀ
 *  net.minecraft.world.entity.EquipmentSlot਍ഀ
 *  net.minecraft.world.entity.player.Player਍ഀ
 *  net.minecraft.world.item.ItemStack਍ഀ
 *  net.minecraft.world.level.BlockAndTintGetter਍ഀ
 *  net.minecraft.world.level.Level਍ഀ
 *  net.minecraft.world.level.block.Block਍ഀ
 *  net.minecraft.world.level.block.entity.BlockEntity਍ഀ
 *  net.minecraft.world.level.block.state.BlockState਍ഀ
 *  net.minecraft.world.phys.HitResult$Type਍ഀ
 *  net.minecraft.world.phys.Vec3਍ഀ
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut਍ഀ
 *  net.minecraftforge.client.event.ComputeFovModifierEvent਍ഀ
 *  net.minecraftforge.client.event.RenderHighlightEvent$Block਍ഀ
 *  net.minecraftforge.client.event.RenderLevelStageEvent਍ഀ
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage਍ഀ
 *  net.minecraftforge.common.MinecraftForge਍ഀ
 *  net.minecraftforge.event.TickEvent$ClientTickEvent਍ഀ
 *  net.minecraftforge.event.TickEvent$Phase਍ഀ
 *  net.minecraftforge.eventbus.api.EventPriority਍ഀ
 *  net.minecraftforge.eventbus.api.SubscribeEvent਍ഀ
 */਍ഀ
package com.brandon3055.brandonscore.client;਍ഀ
਍ഀ
import codechicken.lib.colour.EnumColour;਍ഀ
import codechicken.lib.render.RenderUtils;਍ഀ
import codechicken.lib.render.buffer.TransformingVertexConsumer;਍ഀ
import codechicken.lib.vec.Cuboid6;਍ഀ
import codechicken.lib.vec.Vector3;਍ഀ
import com.brandon3055.brandonscore.api.IFOVModifierItem;਍ഀ
import com.brandon3055.brandonscore.blocks.BlockBCore;਍ഀ
import com.brandon3055.brandonscore.client.render.BlockEntityRendererTransparent;਍ഀ
import com.brandon3055.brandonscore.handlers.BCEventHandler;਍ഀ
import com.mojang.blaze3d.vertex.DefaultVertexFormat;਍ഀ
import com.mojang.blaze3d.vertex.PoseStack;਍ഀ
import com.mojang.blaze3d.vertex.VertexConsumer;਍ഀ
import com.mojang.blaze3d.vertex.VertexFormat;਍ഀ
import java.util.Comparator;਍ഀ
import java.util.HashMap;਍ഀ
import java.util.LinkedList;਍ഀ
import java.util.List;਍ഀ
import java.util.Map;਍ഀ
import net.covers1624.quack.util.CrashLock;਍ഀ
import net.minecraft.client.Camera;਍ഀ
import net.minecraft.client.Minecraft;਍ഀ
import net.minecraft.client.multiplayer.ClientLevel;਍ഀ
import net.minecraft.client.renderer.LevelRenderer;਍ഀ
import net.minecraft.client.renderer.MultiBufferSource;਍ഀ
import net.minecraft.client.renderer.RenderStateShard;਍ഀ
import net.minecraft.client.renderer.RenderType;਍ഀ
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;਍ഀ
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;਍ഀ
import net.minecraft.client.renderer.texture.OverlayTexture;਍ഀ
import net.minecraft.core.BlockPos;਍ഀ
import net.minecraft.resources.ResourceKey;਍ഀ
import net.minecraft.world.entity.Entity;਍ഀ
import net.minecraft.world.entity.EquipmentSlot;਍ഀ
import net.minecraft.world.entity.player.Player;਍ഀ
import net.minecraft.world.item.ItemStack;਍ഀ
import net.minecraft.world.level.BlockAndTintGetter;਍ഀ
import net.minecraft.world.level.Level;਍ഀ
import net.minecraft.world.level.block.Block;਍ഀ
import net.minecraft.world.level.block.entity.BlockEntity;਍ഀ
import net.minecraft.world.level.block.state.BlockState;਍ഀ
import net.minecraft.world.phys.HitResult;਍ഀ
import net.minecraft.world.phys.Vec3;਍ഀ
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;਍ഀ
import net.minecraftforge.client.event.ComputeFovModifierEvent;਍ഀ
import net.minecraftforge.client.event.RenderHighlightEvent;਍ഀ
import net.minecraftforge.client.event.RenderLevelStageEvent;਍ഀ
import net.minecraftforge.common.MinecraftForge;਍ഀ
import net.minecraftforge.event.TickEvent;਍ഀ
import net.minecraftforge.eventbus.api.EventPriority;਍ഀ
import net.minecraftforge.eventbus.api.SubscribeEvent;਍ഀ
਍ഀ
public class BCClientEventHandler {਍ഀ
    private static final CrashLock LOCK = new CrashLock("Already Initialized");਍ഀ
    private static int remountTicksRemaining = 0;਍ഀ
    private static int remountEntityID = 0;਍ഀ
    private static int debugTimeout = 0;਍ഀ
    private static Map<ResourceKey<Level>, Integer[]> dimTickTimes = new HashMap<ResourceKey<Level>, Integer[]>();਍ഀ
    private static Integer[] overallTickTime = new Integer[200];਍ഀ
    private static int renderIndex = 0;਍ഀ
    private static LinkedList<ResourceKey<Level>> sortingOrder = new LinkedList();਍ഀ
    public static int elapsedTicks = 0;਍ഀ
    private static Comparator<ResourceKey<Level>> sorter = (value, compare) -> {਍ഀ
        long totalValue = 0L;਍ഀ
        for (Integer time : dimTickTimes.get(value)) {਍ഀ
            totalValue += (long)time.intValue();਍ഀ
        }਍ഀ
        totalValue /= 200L;਍ഀ
        long totalCompare = 0L;਍ഀ
        for (Integer time : dimTickTimes.get(compare)) {਍ഀ
            totalCompare += (long)time.intValue();਍ഀ
        }਍ഀ
        return Long.compare(totalCompare /= 200L, totalValue);਍ഀ
    };਍ഀ
    private static final RenderStateShard.DepthTestStateShard DISABLE_DEPTH = new /* Unavailable Anonymous Inner Class!! */;਍ഀ
    private static final RenderType boxNoDepth = RenderType.m_173215_((String)"ccl:box_no_depth", (VertexFormat)DefaultVertexFormat.f_85815_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.m_110628_().m_173292_(RenderStateShard.f_173104_).m_110685_(RenderStateShard.f_110139_).m_110687_(RenderStateShard.f_110115_).m_110663_(DISABLE_DEPTH).m_110691_(false));਍ഀ
    private static final Cuboid6 BOX = Cuboid6.full.copy().expand(0.02);਍ഀ
    private static final float[] RED = EnumColour.RED.getColour(128).packArray();਍ഀ
    public static List<Vector3> debugBlockList = null;਍ഀ
    int i = 0;਍ഀ
਍ഀ
    public static void init() {਍ഀ
        LOCK.lock();਍ഀ
        MinecraftForge.EVENT_BUS.register((Object)new BCClientEventHandler());਍ഀ
    }਍ഀ
਍ഀ
    @SubscribeEvent਍ഀ
    public static void disconnectEvent(ClientPlayerNetworkEvent.LoggingOut event) {਍ഀ
        Minecraft mc = Minecraft.m_91087_();਍ഀ
        if (mc.f_91074_ != null) {਍ഀ
            BCEventHandler.noClipPlayers.remove(mc.f_91074_.m_20148_());਍ഀ
        }਍ഀ
    }਍ഀ
਍ഀ
    @SubscribeEvent਍ഀ
    public void tickEnd(TickEvent.ClientTickEvent event) {਍ഀ
        if (event.phase != TickEvent.Phase.END) {਍ഀ
            return;਍ഀ
        }਍ഀ
        ++elapsedTicks;਍ഀ
        if (Minecraft.m_91087_().m_91104_()) {਍ഀ
            return;਍ഀ
        }਍ഀ
        if (debugTimeout > 0) {਍ഀ
            --debugTimeout;਍ഀ
        }਍ഀ
        if (elapsedTicks % 100 == 0 && debugTimeout > 0) {਍ഀ
            sortingOrder.clear();਍ഀ
            sortingOrder.addAll(dimTickTimes.keySet());਍ഀ
            sortingOrder.sort(sorter);਍ഀ
        }਍ഀ
    }਍ഀ
਍ഀ
    @SubscribeEvent਍ഀ
    public void drawSelectionEvent(RenderHighlightEvent.Block event) {਍ഀ
        BlockBCore block;਍ഀ
        ClientLevel level = Minecraft.m_91087_().f_91073_;਍ഀ
        if (event.getTarget().m_6662_() == HitResult.Type.MISS || level == null) {਍ഀ
            return;਍ഀ
        }਍ഀ
        BlockState state = level.m_8055_(event.getTarget().m_82425_());਍ഀ
        Block block2 = state.m_60734_();਍ഀ
        if (block2 instanceof BlockBCore && !(block = (BlockBCore)block2).renderSelectionBox(event, (Level)level)) {਍ഀ
            event.setCanceled(true);਍ഀ
        }਍ഀ
    }਍ഀ
਍ഀ
    @SubscribeEvent(priority=EventPriority.LOW)਍ഀ
    public void fovUpdate(ComputeFovModifierEvent event) {਍ഀ
        float originalFOV;਍ഀ
        Player player = event.getPlayer();਍ഀ
        float newFOV = originalFOV = event.getFovModifier();਍ഀ
        int slotIndex = 2;਍ഀ
        for (ItemStack stack : player.m_150109_().f_35975_) {਍ഀ
            if (!stack.m_41619_() && stack.m_41720_() instanceof IFOVModifierItem) {਍ഀ
                newFOV = ((IFOVModifierItem)stack.m_41720_()).getNewFOV(player, stack, newFOV, originalFOV, EquipmentSlot.values()[slotIndex]);਍ഀ
            }਍ഀ
            ++slotIndex;਍ഀ
        }਍ഀ
        ItemStack stack = player.m_21206_();਍ഀ
        if (!stack.m_41619_() && stack.m_41720_() instanceof IFOVModifierItem) {਍ഀ
            newFOV = ((IFOVModifierItem)stack.m_41720_()).getNewFOV(player, stack, newFOV, originalFOV, EquipmentSlot.OFFHAND);਍ഀ
        }਍ഀ
        if (!(stack = player.m_21205_()).m_41619_() && stack.m_41720_() instanceof IFOVModifierItem) {਍ഀ
            newFOV = ((IFOVModifierItem)stack.m_41720_()).getNewFOV(player, stack, newFOV, originalFOV, EquipmentSlot.MAINHAND);਍ഀ
        }਍ഀ
        if (newFOV != originalFOV) {਍ഀ
            event.setNewFovModifier(newFOV);਍ഀ
        }਍ഀ
    }਍ഀ
਍ഀ
    /*਍ഀ
     * WARNING - Removed try catching itself - possible behaviour change.਍ഀ
     */਍ഀ
    @SubscribeEvent(priority=EventPriority.LOW)਍ഀ
    public void renderLevelStage(RenderLevelStageEvent event) {਍ഀ
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {਍ഀ
            this.doDebugRendering(event);਍ഀ
        }਍ഀ
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {਍ഀ
            return;਍ഀ
        }਍ഀ
        BlockEntityRenderDispatcher tileRenderDispatcher = Minecraft.m_91087_().m_167982_();਍ഀ
        MultiBufferSource.BufferSource buffers = Minecraft.m_91087_().m_91269_().m_110104_();਍ഀ
        LevelRenderer levelRenderer = event.getLevelRenderer();਍ഀ
        PoseStack poseStack = event.getPoseStack();਍ഀ
        Camera camera = event.getCamera();਍ഀ
        Vec3 vec3 = event.getCamera().m_90583_();਍ഀ
        double camX = vec3.m_7096_();਍ഀ
        double camY = vec3.m_7098_();਍ഀ
        double camZ = vec3.m_7094_();਍ഀ
        for (LevelRenderer.RenderChunkInfo renderChunkInfo : levelRenderer.f_194297_) {਍ഀ
            List list = renderChunkInfo.f_109839_.m_112835_().m_112773_();਍ഀ
            for (BlockEntity tile : list) {਍ഀ
                BlockEntityRenderer renderer;਍ഀ
                if (!event.getFrustum().m_113029_(tile.getRenderBoundingBox()) || !((renderer = tileRenderDispatcher.m_112265_(tile)) instanceof BlockEntityRendererTransparent)) continue;਍ഀ
                BlockEntityRendererTransparent rendererTransparent = (BlockEntityRendererTransparent)renderer;਍ഀ
                BlockPos pos = tile.m_58899_();਍ഀ
                poseStack.m_85836_();਍ഀ
                poseStack.m_85837_((double)pos.m_123341_() - camX, (double)pos.m_123342_() - camY, (double)pos.m_123343_() - camZ);਍ഀ
                this.renderTransparent(camera, rendererTransparent, tile, event.getPartialTick(), poseStack, (MultiBufferSource)buffers);਍ഀ
                poseStack.m_85849_();਍ഀ
            }਍ഀ
        }਍ഀ
        Object object = levelRenderer.f_109468_;਍ഀ
        synchronized (object) {਍ഀ
            for (BlockEntity tile : levelRenderer.f_109468_) {਍ഀ
                BlockEntityRenderer renderer;਍ഀ
                if (!event.getFrustum().m_113029_(tile.getRenderBoundingBox()) || !((renderer = tileRenderDispatcher.m_112265_(tile)) instanceof BlockEntityRendererTransparent)) continue;਍ഀ
                BlockEntityRendererTransparent rendererTransparent = (BlockEntityRendererTransparent)renderer;਍ഀ
                BlockPos blockpos3 = tile.m_58899_();਍ഀ
                poseStack.m_85836_();਍ഀ
                poseStack.m_85837_((double)blockpos3.m_123341_() - camX, (double)blockpos3.m_123342_() - camY, (double)blockpos3.m_123343_() - camZ);਍ഀ
                this.renderTransparent(camera, rendererTransparent, tile, event.getPartialTick(), poseStack, (MultiBufferSource)buffers);਍ഀ
                poseStack.m_85849_();਍ഀ
            }਍ഀ
        }਍ഀ
    }਍ഀ
਍ഀ
    public <E extends BlockEntity> void renderTransparent(Camera camera, BlockEntityRendererTransparent<E> renderer, E tile, float partialTicks, PoseStack poseStack, MultiBufferSource buffers) {਍ഀ
        if (!tile.m_58898_() || !tile.m_58903_().m_155262_(tile.m_58900_())) {਍ഀ
            return;਍ഀ
        }਍ഀ
        if (!renderer.m_142756_(tile, camera.m_90583_())) {਍ഀ
            return;਍ഀ
        }਍ഀ
        int packedLight = LevelRenderer.m_109541_((BlockAndTintGetter)tile.m_58904_(), (BlockPos)tile.m_58899_());਍ഀ
        try {਍ഀ
            renderer.renderTransparent(tile, partialTicks, poseStack, buffers, packedLight, OverlayTexture.f_118083_);਍ഀ
        }਍ഀ
        catch (Throwable e) {਍ഀ
            e.printStackTrace();਍ഀ
        }਍ഀ
    }਍ഀ
਍ഀ
    public void doDebugRendering(RenderLevelStageEvent event) {਍ഀ
        if (debugBlockList == null) {਍ഀ
            return;਍ഀ
        }਍ഀ
        MultiBufferSource.BufferSource source = Minecraft.m_91087_().m_91269_().m_110104_();਍ഀ
        Camera camera = Minecraft.m_91087_().f_91063_.m_109153_();਍ഀ
        Vec3 cameraPos = camera.m_90583_();਍ഀ
        PoseStack pStack = event.getPoseStack();਍ഀ
        pStack.m_85836_();਍ഀ
        if (this.i++ % 100 == 0) {਍ഀ
            debugBlockList.sort(Comparator.comparingDouble(value -> value.distanceSquared(Vector3.fromEntity((Entity)Minecraft.m_91087_().f_91074_))));਍ഀ
        }਍ഀ
        pStack.m_85837_(-cameraPos.f_82479_, -cameraPos.f_82480_, -cameraPos.f_82481_);਍ഀ
        TransformingVertexConsumer consumer = new TransformingVertexConsumer(source.m_6299_(boxNoDepth), pStack);਍ഀ
        int i = 0;਍ഀ
        for (Vector3 pos : debugBlockList) {਍ഀ
            RenderUtils.bufferCuboidSolid((VertexConsumer)consumer, (Cuboid6)BOX.copy().add(pos), (float)RED[0], (float)RED[1], (float)RED[2], (float)RED[3]);਍ഀ
            if (++i <= 1000) continue;਍ഀ
            break;਍ഀ
        }਍ഀ
        source.m_109911_();਍ഀ
        pStack.m_85849_();਍ഀ
    }਍ഀ
}਍ഀ
