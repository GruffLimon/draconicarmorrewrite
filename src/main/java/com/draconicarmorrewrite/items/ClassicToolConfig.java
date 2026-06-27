package com.draconicarmorrewrite.items;

import com.draconicarmorrewrite.config.OASArmorConfig;
import com.draconicarmorrewrite.config.OASArmorConfigKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * Stub implementation of ClassicToolConfig for use with ArmorConfigScreen.
 * Since DraconicArmorRewrite only has armor (no tools), tool-related methods return zero/false.
 */
public final class ClassicToolConfig {

    private ClassicToolConfig() {
    }

    public static int getDigAoe(ItemStack stack) { return 0; }
    public static int getDigDepth(ItemStack stack) { return 0; }
    public static int getAttackAoe(ItemStack stack) { return 0; }
    public static int getDigSpeedPercent(ItemStack stack) { return 100; }

    public static boolean supportsDigDepth(ItemStack stack) { return false; }
    public static boolean supportsJunkFilter(ItemStack stack) { return false; }

    public static boolean isAoeSafeMode(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.AOE_SAFE_MODE, false);
    }

    public static boolean isAoeHeightNormalized(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.AOE_HEIGHT_NORMALIZATION, true);
    }

    public static boolean shouldShowDigAoe(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.SHOW_DIG_AOE, false);
    }

    public static boolean isJunkFilterEnabled(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ENABLE_JUNK_FILTER, true);
    }

    public static boolean isJunkNbtSensitive(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.JUNK_NBT_SENS, true);
    }

    public static boolean shouldShowHarvestIndicator(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.SHOW_HARVEST_INDICATOR, true);
    }

    public static boolean isHoeLandFillEnabled(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.HOE_LAND_FILL, false);
    }

    public static int getMaxDigAoe(ItemStack stack) { return 0; }
    public static int getMaxDigDepth(ItemStack stack) { return 0; }
    public static int getMaxAttackAoe(ItemStack stack) { return 0; }

    public static int[] getDigAoeValues(ItemStack stack) { return new int[]{0}; }
    public static int[] getDigDepthValues(ItemStack stack) { return new int[]{0}; }
    public static int[] getAttackAoeValues(ItemStack stack) { return new int[]{0}; }

    public static int valueFromProgress(ItemStack stack, ArmorConfigActionLike action, double progress) {
        int[] values = switch (action) {
            case DIG_AOE -> getDigAoeValues(stack);
            case DIG_DEPTH -> getDigDepthValues(stack);
            case ATTACK_AOE -> getAttackAoeValues(stack);
        };
        int index = Mth.clamp((int) Math.round(Mth.clamp((double) progress, 0.0, 1.0) * (double) (values.length - 1)), 0, values.length - 1);
        return values[index];
    }

    public static float progressForValue(ItemStack stack, ArmorConfigActionLike action, int value) {
        int[] values = switch (action) {
            case DIG_AOE -> getDigAoeValues(stack);
            case DIG_DEPTH -> getDigDepthValues(stack);
            case ATTACK_AOE -> getAttackAoeValues(stack);
        };
        if (values.length <= 1) return 0.0f;
        for (int i = 0; i < values.length; ++i) {
            if (values[i] >= value) return (float) i / (float) (values.length - 1);
        }
        return 1.0f;
    }

    public enum ArmorConfigActionLike {
        DIG_AOE,
        DIG_DEPTH,
        ATTACK_AOE;
    }
}
