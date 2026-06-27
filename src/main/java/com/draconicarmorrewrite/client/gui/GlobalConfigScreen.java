/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package com.draconicarmorrewrite.client.gui;

import com.draconicarmorrewrite.client.OASClient;
import com.draconicarmorrewrite.items.OASUpgradableItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GlobalConfigScreen
extends Screen {
    private static final int CELL = 18;
    private static final int GAP = 0;
    private static final int PAD = 9;
    private static final int INNER_GAP = 0;
    private static final int RED_BOX = 18;
    private static final int GREEN_BOX = 18;
    private static final int PANEL_W = 180;
    private static final int PANEL_H = 173;
    private static final int CYAN = -16711681;
    private static final int GREEN = -16730112;
    private static final int RED = -6094848;
    private static final int PANEL_BG = -1358954496;
    private ItemStack hoveredTooltipStack = ItemStack.EMPTY;

    public GlobalConfigScreen() {
        super((Component)Component.translatable("gui.originalarmorstuff.global_config"));
    }

    @Override protected void init() {
        super.init();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Layout layout = this.layout();
        EquipmentSlot[] armorSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < armorSlots.length; ++i) {
            EquipmentSlot slot = armorSlots[i];
            ItemStack stack = player.getItemBySlot(slot);
            int x = layout.armorX;
            int y = layout.armorY + i * layout.pitch;
            this.addRenderableWidget(new ItemSlotButton(x, y, CELL, stack, btn -> OASClient.openArmorConfig(stack, slot, null)));
        }
        ItemStack offHand = player.getOffhandItem();
        this.addRenderableWidget(new ItemSlotButton(layout.offhandX, layout.offhandY, CELL, offHand, btn -> OASClient.openArmorConfig(offHand, null, net.minecraft.world.InteractionHand.OFF_HAND)));
        Inventory inventory = player.getInventory();
        int i = 0;
        while (i < inventory.items.size()) {
            ItemStack stack = inventory.getItem(i);
            int x = layout.invX + i % 9 * layout.pitch;
            int y = i < 9 ? layout.hotbarY : layout.mainInvY + (i / 9 - 1) * layout.pitch;
            int slot = i++;
            this.addRenderableWidget(new ItemSlotButton(x, y, CELL, stack, btn -> OASClient.openArmorConfig(stack, slot)));
        }
        this.addRenderableWidget(new ButtonColourRect(layout.hudX, layout.hudY, 59, 16, Component.literal("HUD"), btn -> this.minecraft.setScreen(new HudConfigScreen(this)), 0x88000000, 0xFF440066, 0xFF009900));
    }

    @Override public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        Layout layout = this.layout();
        guiGraphics.fill(layout.left, layout.top, layout.right, layout.bottom, PANEL_BG);
        GlobalConfigScreen.drawBorder(guiGraphics, layout.left, layout.top, layout.right, layout.bottom, CYAN);
        GlobalConfigScreen.drawSlotGroup(guiGraphics, layout.armorGroupX, layout.armorGroupY, 1, 4, CYAN);
        GlobalConfigScreen.drawRectFrame(guiGraphics, layout.previewX, layout.previewY, layout.previewX + layout.previewW, layout.previewY + layout.previewH, CYAN);
        this.renderPlayerPreview(guiGraphics, layout.previewX + layout.previewW / 2, layout.previewY + layout.previewH - 9, 30, mouseX, mouseY);
        GlobalConfigScreen.drawSlotGroup(guiGraphics, layout.offhandGroupX, layout.offhandGroupY, 1, 1, CYAN);
        GlobalConfigScreen.drawInventoryGroup(guiGraphics, layout.invGroupX, layout.invGroupY);
        this.hoveredTooltipStack = ItemStack.EMPTY;
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (!this.hoveredTooltipStack.isEmpty()) {
            guiGraphics.renderTooltip(this.font, this.hoveredTooltipStack, mouseX, mouseY);
        }
    }

    @Override public boolean isPauseScreen() {
        return false;
    }

    private static void drawSlotGroup(GuiGraphics guiGraphics, int x, int y, int cols, int rows, int color) {
        GlobalConfigScreen.drawRectFrame(guiGraphics, x, y, x + cols * CELL + 2, y + rows * CELL + 2, color);
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                GlobalConfigScreen.drawCell(guiGraphics, x + 1 + col * CELL, y + 1 + row * CELL);
            }
        }
    }

    private static void drawInventoryGroup(GuiGraphics guiGraphics, int x, int y) {
        int groupWidth = 164;
        int groupHeight = 74;
        int separatorY = y + 54;
        GlobalConfigScreen.drawRectFrame(guiGraphics, x, y, x + groupWidth, y + groupHeight, CYAN);
        guiGraphics.fill(x, separatorY, x + groupWidth, separatorY + 1, CYAN);
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 9; ++col) {
                GlobalConfigScreen.drawCell(guiGraphics, x + 1 + col * CELL, y + 1 + row * CELL);
            }
        }
    }

    private static void drawCell(GuiGraphics guiGraphics, int x, int y) {
        int inset = 0;
        GlobalConfigScreen.drawRectFrame(guiGraphics, x + inset, y + inset, x + inset + CELL, y + inset + CELL, RED);
    }

    private Layout layout() {
        int cx = this.width / 2;
        int cy = this.height / 2 + 40;
        int left = cx - 90;
        int top = cy - 115;
        int right = cx + 90;
        int bottom = cy + 58;
        int pitch = CELL;
        
        int armorGroupX = cx - 82;
        int armorGroupY = cy - 107;
        int armorX = cx - 81;
        int armorY = cy - 106;
        
        int invGroupX = cx - 82;
        int invGroupY = cy - 25;
        int invX = cx - 81;
        int mainInvY = cy - 24;
        int hotbarY = cy + 31;
        
        int previewX = cx - 21;
        int previewY = cy - 107;
        int previewW = 42;
        int previewH = 74;
        
        int offhandGroupX = cx + 23;
        int offhandGroupY = cy - 53;
        int offhandX = cx + 24;
        int offhandY = cy - 52;
        
        int hudX = cx + 23;
        int hudY = cy - 107;
        
        return new Layout(left, top, right, bottom, pitch, 
                          armorGroupX, armorGroupY, armorX, armorY, 
                          invGroupX, invGroupY, invX, mainInvY, hotbarY, 
                          previewX, previewY, previewW, previewH, 
                          offhandGroupX, offhandGroupY, offhandX, offhandY, 
                          hudX, hudY);
    }

    private static void drawBorder(GuiGraphics guiGraphics, int left, int top, int right, int bottom, int color) {
        guiGraphics.fill(left, top, right, top + 1, color);
        guiGraphics.fill(left, bottom - 1, right, bottom, color);
        guiGraphics.fill(left, top, left + 1, bottom, color);
        guiGraphics.fill(right - 1, top, right, bottom, color);
    }

    private static void drawRectFrame(GuiGraphics guiGraphics, int left, int top, int right, int bottom, int color) {
        GlobalConfigScreen.drawBorder(guiGraphics, left, top, right, bottom, color);
        guiGraphics.fill(left + 1, top + 1, right - 1, bottom - 1, -16777216);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void renderPlayerPreview(GuiGraphics guiGraphics, int x, int y, int scale, int mouseX, int mouseY) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, x, y, scale, (float)(x - mouseX), (float)(y - 50 - mouseY), player);
    }

    private static boolean isConfigurable(ItemStack stack) {
        return stack.getItem() instanceof OASUpgradableItem;
    }

    private record Layout(int left, int top, int right, int bottom, int pitch, int armorGroupX, int armorGroupY, int armorX, int armorY, int invGroupX, int invGroupY, int invX, int mainInvY, int hotbarY, int previewX, int previewY, int previewW, int previewH, int offhandGroupX, int offhandGroupY, int offhandX, int offhandY, int hudX, int hudY) {
    }

    private final class ItemSlotButton
    extends AbstractWidget {
        private final ItemStack stack;
        private final OnPress onPress;

        private ItemSlotButton(int x, int y, int size, ItemStack stack, OnPress onPress) {
            super(x, y, size, size, (Component)(stack.isEmpty() ? Component.empty() : stack.getHoverName()));
            this.stack = stack.copy();
            this.onPress = onPress;
            this.active = !stack.isEmpty();
        }

        @Override public void onClick(double mouseX, double mouseY) {
            if (GlobalConfigScreen.isConfigurable(this.stack)) {
                this.onPress.onPress(this);
            }
        }

        @Override protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (!this.stack.isEmpty()) {
                if (GlobalConfigScreen.isConfigurable(this.stack)) {
                    int inset = -1;
                    GlobalConfigScreen.drawRectFrame(guiGraphics, this.getX() + inset, this.getY() + inset, this.getX() + inset + this.width + 2, this.getY() + inset + this.height + 2, GREEN);
                }
                int iconX = this.getX() + (this.width - 16) / 2;
                int iconY = this.getY() + (this.height - 16) / 2;
                guiGraphics.renderItem(this.stack, iconX, iconY);
                guiGraphics.renderItemDecorations(GlobalConfigScreen.this.font, this.stack, iconX, iconY);
            }
            if (this.isHoveredOrFocused() && !this.stack.isEmpty()) {
                GlobalConfigScreen.this.hoveredTooltipStack = this.stack;
            }
        }

        @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }

    @FunctionalInterface
    private static interface OnPress {
        public void onPress(AbstractWidget var1);
    }
}

