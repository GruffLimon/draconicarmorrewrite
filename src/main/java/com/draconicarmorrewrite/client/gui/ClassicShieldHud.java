package com.draconicarmorrewrite.client.gui;

import com.draconicarmorrewrite.DraconicArmorRewrite;
import com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.brandonscore.api.power.IOPStorage;

public class ClassicShieldHud implements IGuiOverlay {

    // Bind texture from the original mod (draconicevolution namespace)
    public static final ResourceLocation HUD_TEXTURE = new ResourceLocation("draconicevolution", "textures/gui/hud.png");
    
    public static float armorStatsFadeOut = 0.0f;
    private static double lastMaxShield = 0;
    private static double lastShield = 0;
    private static double lastEntropy = 0;
    private static long lastEnergy = 0;

    public static class ShieldStats {
        public double shieldCap = 0;
        public double shieldPoints = 0;
        public double entropy = 0;
        public long energy = 0;
        public long maxEnergy = 0;
        public boolean hasShield = false;
    }

    public static ShieldStats getPlayerStats(Player player) {
        ShieldStats stats = new ShieldStats();
        if (player == null) return stats;

        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (EquipmentSlot slot : slots) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ClassicUpgradeableArmorItem armor) {
                stats.hasShield = true;
                if (armor.isShieldEnabled(stack)) {
                    stats.shieldCap += armor.getShieldCapacity(stack);
                    stats.shieldPoints += armor.getShieldPoints(stack);
                    stats.entropy = Math.max(stats.entropy, armor.getShieldEntropy(stack));
                }
                IOPStorage storage = stack.getCapability(DECapabilities.OP_STORAGE).orElse(null);
                if (storage != null) {
                    stats.energy += storage.getOPStored();
                    stats.maxEnergy += storage.getMaxOPStored();
                }
            }
        }
        return stats;
    }

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ShieldStats stats = getPlayerStats(mc.player);
        if (!stats.hasShield) {
            armorStatsFadeOut = 0.0f;
            return;
        }

        if (stats.shieldCap != lastMaxShield || stats.shieldPoints != lastShield || stats.entropy != lastEntropy || stats.energy != lastEnergy) {
            armorStatsFadeOut = 5.0f;
            lastMaxShield = stats.shieldCap;
            lastShield = stats.shieldPoints;
            lastEntropy = stats.entropy;
            lastEnergy = stats.energy;
        }

        HudSettings.load();
        int fadeMode = HudSettings.shieldFadeMode;
        if (fadeMode > 0) {
            float minOpacity = 1.0f - (float) fadeMode * 0.25f;
            if (armorStatsFadeOut > minOpacity) {
                armorStatsFadeOut -= 0.1f;
                if (armorStatsFadeOut < minOpacity) {
                    armorStatsFadeOut = minOpacity;
                }
            }
        } else {
            armorStatsFadeOut = 5.0f;
        }
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || player.isSpectator() || mc.options.hideGui) return;

        HudSettings.load();
        if (HudSettings.shieldHidden) return;

        ShieldStats stats = getPlayerStats(player);
        if (!stats.hasShield) return;

        // Apply fading opacity
        float opacity = Math.min(armorStatsFadeOut, 1.0f);
        if (opacity <= 0.0f) return;

        int shieldPercentCharge = stats.shieldCap > 0 ? (int) ((stats.shieldPoints / stats.shieldCap) * 100D) : 0;
        int entropyPercent = (int) stats.entropy;
        int energyPercent = stats.maxEnergy > 0 ? (int) ((double) stats.energy / stats.maxEnergy * 100D) : 0;

        int x = HudSettings.shieldLeft(screenWidth);
        int y = HudSettings.shieldTop(screenHeight);
        float scale = HudSettings.shieldScale();
        boolean rotated = HudSettings.shieldRotated;
        boolean numeric = HudSettings.shieldNumeric;

        String shieldText = (int) stats.shieldPoints + "/" + (int) stats.shieldCap;
        String entropyText = "EN: " + entropyPercent + "%";
        String energyText = "FE: " + com.brandon3055.brandonscore.utils.Utils.formatNumber(stats.energy);

        renderHUDAt(graphics, mc, x, y, scale, rotated, numeric, opacity, shieldPercentCharge, entropyPercent, energyPercent, shieldText, entropyText, energyText);
    }

    public static void renderPreview(GuiGraphics graphics, int x, int y, float scale, boolean rotated, boolean numeric, float opacity) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ShieldStats stats = getPlayerStats(player);
        
        double shieldPoints = stats.hasShield ? stats.shieldPoints : 2048;
        double shieldCap = stats.hasShield ? stats.shieldCap : 2048;
        int shieldPercent = shieldCap > 0 ? (int) ((shieldPoints / shieldCap) * 100D) : 100;
        int entropyPercent = (int) stats.entropy;
        long totalEnergy = stats.hasShield ? stats.energy : 10000000L;
        long maxEnergy = stats.hasShield ? stats.maxEnergy : 10000000L;
        int energyPercent = maxEnergy > 0 ? (int) ((double) totalEnergy / maxEnergy * 100D) : 100;

        String shieldText = (int) shieldPoints + "/" + (int) shieldCap;
        String entropyText = "EN: " + entropyPercent + "%";
        String energyText = "FE: " + com.brandon3055.brandonscore.utils.Utils.formatNumber(totalEnergy);
        
        renderHUDAt(graphics, mc, x, y, scale, rotated, numeric, opacity, shieldPercent, entropyPercent, energyPercent, shieldText, entropyText, energyText);
    }

    private static void renderHUDAt(GuiGraphics graphics, Minecraft mc, int x, int y, float scale, boolean rotated, boolean numeric, float opacity,
                                    int shieldPercent, int entropyPercent, int energyPercent, String shieldText, String entropyText, String energyText) {
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.pose().translate(-x, -y, 0);

        int alpha = Math.max(0, Math.min(255, Math.round(opacity * 255.0f)));
        int color = 0xFFFFFF | (alpha << 24);

        if (rotated) {
            // Draw shield icon normally at x - 15, y + 1 (no rotation for icon)
            graphics.blit(HUD_TEXTURE, x - 15, y + 1, 14, 16, 2.0f, 0.0f, 13, 15, 128, 128);
            
            graphics.pose().pushPose();
            int shiftX = x + 104;
            graphics.pose().translate(shiftX, y, 0);
            graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90));
            graphics.pose().translate(-shiftX, -y, 0);
            
            // Draw background
            graphics.blit(HUD_TEXTURE, shiftX, y, 0, 15, 17, 104, 128, 128);
            
            // Draw shield charge (7 pixels wide)
            int shieldH = Math.min(100, shieldPercent);
            graphics.blit(HUD_TEXTURE, shiftX + 2, y + 2 + (100 - shieldH), 17, 100 - shieldH, 7, shieldH, 128, 128);
            
            // Draw entropy (2 pixels wide)
            int entH = Math.min(100, entropyPercent);
            graphics.blit(HUD_TEXTURE, shiftX + 10, y + 2 + (100 - entH), 25, 100 - entH, 2, entH, 128, 128);
            
            // Draw energy (2 pixels wide)
            int engH = Math.min(100, energyPercent);
            graphics.blit(HUD_TEXTURE, shiftX + 13, y + 2 + (100 - engH), 28, 100 - engH, 2, engH, 128, 128);
            
            graphics.pose().popPose();

            if (numeric) {
                int textShieldX = x + 52 - mc.font.width(shieldText) / 2;
                int textShieldY = y + 2;
                int textEntropyX = x + 104 - mc.font.width(entropyText);
                int textEntropyY = y + 18;
                int textEnergyX = x + 2;
                int textEnergyY = y + 18;

                graphics.drawString(mc.font, shieldText, textShieldX, textShieldY, 0xFF0000FF | (alpha << 24), false);
                graphics.drawString(mc.font, energyText, textEnergyX, textEnergyY, color, true);
                graphics.drawString(mc.font, entropyText, textEntropyX, textEntropyY, color, true);
            }
        } else {
            // Draw icon
            graphics.blit(HUD_TEXTURE, x + 1, y + 105, 15, 17, 2.0f, 0.0f, 13, 15, 128, 128);
            
            // Draw background
            graphics.blit(HUD_TEXTURE, x, y, 0, 15, 17, 104, 128, 128);
            
            // Draw shield charge (7 pixels wide)
            int shieldH = Math.min(100, shieldPercent);
            graphics.blit(HUD_TEXTURE, x + 2, y + 2 + (100 - shieldH), 17, 100 - shieldH, 7, shieldH, 128, 128);
            
            // Draw entropy (2 pixels wide)
            int entH = Math.min(100, entropyPercent);
            graphics.blit(HUD_TEXTURE, x + 10, y + 2 + (100 - entH), 25, 100 - entH, 2, entH, 128, 128);
            
            // Draw energy (2 pixels wide)
            int engH = Math.min(100, energyPercent);
            graphics.blit(HUD_TEXTURE, x + 13, y + 2 + (100 - engH), 28, 100 - engH, 2, engH, 128, 128);

            if (numeric) {
                graphics.drawString(mc.font, shieldText, x + 18, y + 74, color, true);
                graphics.drawString(mc.font, energyText, x + 18, y + 84, color, true);
                graphics.drawString(mc.font, entropyText, x + 18, y + 94, color, true);
            }
        }
        
        graphics.pose().popPose();
    }

    public static int scaledWidth(float scale, boolean rotated, boolean numeric) {
        if (rotated) {
            return Math.round(120.0f * scale);
        } else {
            return Math.round((numeric ? 100.0f : 18.0f) * scale);
        }
    }

    public static int scaledHeight(float scale, boolean rotated, boolean numeric) {
        if (rotated) {
            return Math.round((numeric ? 30.0f : 17.0f) * scale);
        } else {
            return Math.round(122.0f * scale);
        }
    }
}
