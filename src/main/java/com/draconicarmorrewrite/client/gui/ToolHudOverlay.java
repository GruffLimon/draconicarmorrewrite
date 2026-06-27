package com.draconicarmorrewrite.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ToolHudOverlay implements IGuiOverlay {
    private static final int BG = 0xFF200036;
    private static final int BORDER = 0xFF4B0082;
    private static final int CYAN = 0xFF00E6F7;

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        // We don't render anything in-game for tool HUD since this rewrite is armor-only,
        // but the config editor calls renderPreview.
    }

    public static void renderPreview(GuiGraphics graphics, int x, int y, float scale, float opacity) {
        Minecraft mc = Minecraft.getInstance();
        
        // Russian translation key OR English based on locale
        String title = Component.translatable("gui.draconicarmorrewrite.hud.display_settings").getString();
        // Format to look like #### HUD Display ####
        String headerText = "#### " + title + " ####";
        String footerText = "######################";
        for (int k = 0; k < headerText.length(); k++) {
            footerText = "######################";
        }

        int width = getWidth(headerText, mc.font);
        int height = getHeight();

        int alpha = Math.max(0, Math.min(255, Math.round(opacity * 255.0f)));
        int bgColor = (BG & 0xFFFFFF) | (alpha << 24);
        int borderColor = (BORDER & 0xFFFFFF) | (alpha << 24);
        int textColor = (CYAN & 0xFFFFFF) | (alpha << 24);

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.pose().translate(-x, -y, 0);

        // Draw background
        graphics.fill(x, y, x + width, y + height, bgColor);
        
        // Draw border
        drawBorder(graphics, x, y, x + width, y + height, borderColor);

        // Draw text
        graphics.pose().pushPose();
        graphics.pose().translate(x + 7.0f, y + 7.0f, 0.0f);
        graphics.pose().scale(0.7777778f, 0.7777778f, 1.0f);

        mc.font.drawInBatch(headerText, 0, 0, textColor, false, graphics.pose().last().pose(), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
        mc.font.drawInBatch(footerText, 0, Math.round(11.571428f * 4), textColor, false, graphics.pose().last().pose(), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);

        graphics.pose().popPose();
        graphics.pose().popPose();
    }

    public static int scaledWidth(float scale) {
        Minecraft mc = Minecraft.getInstance();
        String title = Component.translatable("gui.draconicarmorrewrite.hud.display_settings").getString();
        String headerText = "#### " + title + " ####";
        return Math.round(getWidth(headerText, mc.font) * scale);
    }

    public static int scaledHeight(float scale) {
        return Math.round(getHeight() * scale);
    }

    private static int getWidth(String text, Font font) {
        int textWidth = font.width(text);
        return Math.max(188, 14 + Math.round((float)textWidth * 0.7777778f));
    }

    private static int getHeight() {
        // 5 lines of text height in preview
        return 14 + 5 * 9;
    }

    private static void drawBorder(GuiGraphics graphics, int left, int top, int right, int bottom, int color) {
        graphics.fill(left, top, right, top + 1, color);
        graphics.fill(left, bottom - 1, right, bottom, color);
        graphics.fill(left, top, left + 1, bottom, color);
        graphics.fill(right - 1, top, right, bottom, color);
    }
}
