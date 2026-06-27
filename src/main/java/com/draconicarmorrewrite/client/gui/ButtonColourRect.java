package com.draconicarmorrewrite.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ButtonColourRect extends Button {
    private final int backColour;
    private final int borderColourInactive;
    private final int borderColourActive;

    public ButtonColourRect(int x, int y, int width, int height, Component message, OnPress onPress, int backColour, int borderColourInactive, int borderColourActive) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.backColour = backColour;
        this.borderColourInactive = borderColourInactive;
        this.borderColourActive = borderColourActive;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            int border = this.isHoveredOrFocused() ? this.borderColourActive : this.borderColourInactive;
            
            // Draw background rectangle
            guiGraphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, this.backColour);
            
            // Draw borders
            // top
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, border);
            // bottom
            guiGraphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, border);
            // left
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, border);
            // right
            guiGraphics.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, border);

            // Draw centered string
            Minecraft mc = Minecraft.getInstance();
            guiGraphics.drawCenteredString(mc.font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 0xFFFFFF);
        }
    }
}
