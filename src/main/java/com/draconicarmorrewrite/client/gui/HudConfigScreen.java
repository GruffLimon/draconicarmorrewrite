package com.draconicarmorrewrite.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.List;

public class HudConfigScreen extends Screen {
    private static final int CYAN = 0xFF00FFFF;

    // Custom button colors matching original 1.12.2 layout
    private static final int c1 = 0x88000000;
    private static final int c2 = 0xFF440066;
    private static final int c3 = 0xFF009900;

    private final Screen parent;
    
    // HUD settings buttons
    private ButtonColourRect toolFadeButton;
    private ButtonColourRect toolHiddenButton;
    
    // Shield settings buttons
    private ButtonColourRect shieldFadeButton;
    private ButtonColourRect shieldRotateButton;
    private ButtonColourRect shieldNumericButton;
    private ButtonColourRect shieldHiddenButton;

    private boolean draggingTool = false;
    private boolean draggingShield = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public HudConfigScreen(Screen parent) {
        super(Component.translatable("gui.draconicarmorrewrite.hud.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        HudSettings.load();

        int cx = this.width / 2;
        int y = this.height / 2 - 30;

        // --- HUD Display Settings Buttons ---
        
        // HUD Scale -
        this.addRenderableWidget(new ButtonColourRect(cx - 81, y - 37, 80, 15, Component.translatable("gui.draconicarmorrewrite.hud.scale_down"), btn -> {
            HudSettings.adjustToolScale(-10);
            HudSettings.save();
        }, c1, c2, c3));

        // HUD Scale +
        this.addRenderableWidget(new ButtonColourRect(cx + 2, y - 37, 80, 15, Component.translatable("gui.draconicarmorrewrite.hud.scale_up"), btn -> {
            HudSettings.adjustToolScale(10);
            HudSettings.save();
        }, c1, c2, c3));

        // HUD Fade Mode
        toolFadeButton = this.addRenderableWidget(new ButtonColourRect(cx - 81, y - 20, 163, 15, getToolFadeText(), btn -> {
            HudSettings.toolFadeMode = (HudSettings.toolFadeMode + 1) % 5;
            toolFadeButton.setMessage(getToolFadeText());
            HudSettings.save();
        }, c1, c2, c3));

        // HUD Toggle Hidden
        toolHiddenButton = this.addRenderableWidget(new ButtonColourRect(cx - 81, y - 3, 163, 15, Component.translatable("gui.draconicarmorrewrite.hud.toggle_hidden"), btn -> {
            HudSettings.toolHidden = !HudSettings.toolHidden;
            HudSettings.save();
        }, c1, c2, c3));


        // --- Shield Display Settings Buttons ---
        
        // Shield Scale -
        this.addRenderableWidget(new ButtonColourRect(cx - 81, y + 25, 80, 15, Component.translatable("gui.draconicarmorrewrite.hud.scale_down"), btn -> {
            HudSettings.adjustShieldScale(-10);
            HudSettings.save();
        }, c1, c2, c3));

        // Shield Scale +
        this.addRenderableWidget(new ButtonColourRect(cx + 2, y + 25, 80, 15, Component.translatable("gui.draconicarmorrewrite.hud.scale_up"), btn -> {
            HudSettings.adjustShieldScale(10);
            HudSettings.save();
        }, c1, c2, c3));

        // Shield Fade Mode
        shieldFadeButton = this.addRenderableWidget(new ButtonColourRect(cx - 81, y + 42, 163, 15, getShieldFadeText(), btn -> {
            HudSettings.shieldFadeMode = (HudSettings.shieldFadeMode + 1) % 5;
            shieldFadeButton.setMessage(getShieldFadeText());
            HudSettings.save();
        }, c1, c2, c3));

        // Shield Rotate
        shieldRotateButton = this.addRenderableWidget(new ButtonColourRect(cx - 81, y + 59, 80, 15, getShieldRotateText(), btn -> {
            HudSettings.shieldRotated = !HudSettings.shieldRotated;
            shieldRotateButton.setMessage(getShieldRotateText());
            HudSettings.save();
        }, c1, c2, c3));

        // Shield Numeric
        shieldNumericButton = this.addRenderableWidget(new ButtonColourRect(cx + 2, y + 59, 80, 15, getShieldNumericText(), btn -> {
            HudSettings.shieldNumeric = !HudSettings.shieldNumeric;
            shieldNumericButton.setMessage(getShieldNumericText());
            HudSettings.save();
        }, c1, c2, c3));

        // Shield Toggle Hidden
        shieldHiddenButton = this.addRenderableWidget(new ButtonColourRect(cx - 81, y + 76, 163, 15, Component.translatable("gui.draconicarmorrewrite.hud.toggle_hidden"), btn -> {
            HudSettings.shieldHidden = !HudSettings.shieldHidden;
            HudSettings.save();
        }, c1, c2, c3));


        // --- Back Button ---
        this.addRenderableWidget(new ButtonColourRect(cx - 35, y + 95, 70, 15, Component.translatable("gui.back"), btn -> this.onClose(), c1, c2, c3));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        int cx = this.width / 2;
        int y = this.height / 2 - 30;

        // Draw Headers
        guiGraphics.drawCenteredString(this.font, Component.translatable("gui.draconicarmorrewrite.hud.title"), cx, y - 60, CYAN);
        guiGraphics.drawCenteredString(this.font, Component.translatable("gui.draconicarmorrewrite.hud.display_settings"), cx, y - 47, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.translatable("gui.draconicarmorrewrite.hud.shield_settings"), cx, y + 15, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.translatable("gui.draconicarmorrewrite.hud.drag_instructions"), cx, y + 115, 0xFFFFFF);

        // Draw scale values next to the buttons
        guiGraphics.drawCenteredString(this.font, HudSettings.toolScale + "%", cx + 97, y - 33, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, HudSettings.shieldScale + "%", cx + 97, y + 29, 0xFFFFFF);

        // Render Previews and Drag Handles
        renderToolHUD(guiGraphics);
        renderShieldHUD(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Hover tooltips for handles
        drawHandleTooltips(guiGraphics, mouseX, mouseY);
    }

    private void renderToolHUD(GuiGraphics guiGraphics) {
        int x = HudSettings.toolLeft(this.width);
        int y = HudSettings.toolTop(this.height);
        float scale = HudSettings.toolScale();

        if (!HudSettings.toolHidden) {
            ToolHudOverlay.renderPreview(guiGraphics, x, y, scale, 1.0f);
        }

        // Draw purple dragging box
        drawDragBox(guiGraphics, x, y);
    }

    private void renderShieldHUD(GuiGraphics guiGraphics) {
        int x = HudSettings.shieldLeft(this.width);
        int y = HudSettings.shieldTop(this.height);
        float scale = HudSettings.shieldScale();
        boolean rotated = HudSettings.shieldRotated;
        boolean numeric = HudSettings.shieldNumeric;

        if (!HudSettings.shieldHidden) {
            ClassicShieldHud.renderPreview(guiGraphics, x, y, scale, rotated, numeric, 1.0f);
        }

        // Draw purple dragging box
        drawDragBox(guiGraphics, x, y);
    }

    private void drawDragBox(GuiGraphics guiGraphics, int cx, int cy) {
        // Draw 39x39 transparent purple background
        guiGraphics.fill(cx - 19, cy - 19, cx + 20, cy + 20, 0x44FF00FF);
        
        // Draw white border
        drawBorder(guiGraphics, cx - 19, cy - 19, cx + 20, cy + 20, -1);
        
        // Draw white cross
        guiGraphics.fill(cx - 4, cy, cx + 5, cy + 1, -1);
        guiGraphics.fill(cx, cy - 4, cx + 1, cy + 5, -1);
    }

    private void drawHandleTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int tx = HudSettings.toolLeft(this.width);
        int ty = HudSettings.toolTop(this.height);
        int sx = HudSettings.shieldLeft(this.width);
        int sy = HudSettings.shieldTop(this.height);

        if (isInHandle(mouseX, mouseY, tx, ty) || isInHandle(mouseX, mouseY, sx, sy)) {
            guiGraphics.renderComponentTooltip(this.font, List.of(Component.translatable("info.de.hudDisplayConfigTxt2.txt")), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int tx = HudSettings.toolLeft(this.width);
            int ty = HudSettings.toolTop(this.height);
            if (isInHandle(mouseX, mouseY, tx, ty)) {
                this.draggingTool = true;
                this.dragOffsetX = tx - (int)mouseX;
                this.dragOffsetY = ty - (int)mouseY;
                return true;
            }

            int sx = HudSettings.shieldLeft(this.width);
            int sy = HudSettings.shieldTop(this.height);
            if (isInHandle(mouseX, mouseY, sx, sy)) {
                this.draggingShield = true;
                this.dragOffsetX = sx - (int)mouseX;
                this.dragOffsetY = sy - (int)mouseY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingTool) {
            float scale = HudSettings.toolScale();
            int scaledW = ToolHudOverlay.scaledWidth(scale);
            int scaledH = ToolHudOverlay.scaledHeight(scale);
            int x = clamp((int)mouseX + this.dragOffsetX, 0, Math.max(0, this.width - scaledW));
            int y = clamp((int)mouseY + this.dragOffsetY, 0, Math.max(0, this.height - scaledH));
            HudSettings.moveToolTo(x, y, this.width, this.height);
            return true;
        }

        if (this.draggingShield) {
            float scale = HudSettings.shieldScale();
            boolean rotated = HudSettings.shieldRotated;
            boolean numeric = HudSettings.shieldNumeric;
            int scaledW = ClassicShieldHud.scaledWidth(scale, rotated, numeric);
            int scaledH = ClassicShieldHud.scaledHeight(scale, rotated, numeric);
            int x = clamp((int)mouseX + this.dragOffsetX, 0, Math.max(0, this.width - scaledW));
            int y = clamp((int)mouseY + this.dragOffsetY, 0, Math.max(0, this.height - scaledH));
            HudSettings.moveShieldTo(x, y, this.width, this.height);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.draggingTool || this.draggingShield) {
            this.draggingTool = false;
            this.draggingShield = false;
            HudSettings.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        HudSettings.save();
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean isInHandle(double mx, double my, int hx, int hy) {
        return mx >= hx - 19 && mx < hx + 20 && my >= hy - 19 && my < hy + 20;
    }

    private static void drawBorder(GuiGraphics graphics, int left, int top, int right, int bottom, int color) {
        graphics.fill(left, top, right, top + 1, color);
        graphics.fill(left, bottom - 1, right, bottom, color);
        graphics.fill(left, top, left + 1, bottom, color);
        graphics.fill(right - 1, top, right, bottom, color);
    }

    private static Component getToolFadeText() {
        String modeKey = switch (HudSettings.toolFadeMode) {
            case 0 -> "gui.draconicarmorrewrite.hud.fade.always";
            case 1 -> "gui.draconicarmorrewrite.hud.fade.75";
            case 2 -> "gui.draconicarmorrewrite.hud.fade.50";
            case 3 -> "gui.draconicarmorrewrite.hud.fade.25";
            default -> "gui.draconicarmorrewrite.hud.fade.hide";
        };
        return Component.translatable("gui.draconicarmorrewrite.hud.fade_out", Component.translatable(modeKey));
    }

    private static Component getShieldFadeText() {
        String modeKey = switch (HudSettings.shieldFadeMode) {
            case 0 -> "gui.draconicarmorrewrite.hud.fade.always";
            case 1 -> "gui.draconicarmorrewrite.hud.fade.75";
            case 2 -> "gui.draconicarmorrewrite.hud.fade.50";
            case 3 -> "gui.draconicarmorrewrite.hud.fade.25";
            default -> "gui.draconicarmorrewrite.hud.fade.hide";
        };
        return Component.translatable("gui.draconicarmorrewrite.hud.fade_out", Component.translatable(modeKey));
    }

    private static Component getShieldNumericText() {
        Component state = Component.translatable(HudSettings.shieldNumeric ? "gui.draconicarmorrewrite.hud.on" : "gui.draconicarmorrewrite.hud.off");
        return Component.translatable("gui.draconicarmorrewrite.hud.numeric", state);
    }

    private static Component getShieldRotateText() {
        return Component.translatable("gui.draconicarmorrewrite.hud.rotate");
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
