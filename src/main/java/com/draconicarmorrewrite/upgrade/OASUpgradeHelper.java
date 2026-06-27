package com.draconicarmorrewrite.upgrade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class OASUpgradeHelper {
    public static final String UPGRADE_TAG = "DEUpgrades";

    private OASUpgradeHelper() {
    }

    public static int getUpgradeLevel(ItemStack stack, OASUpgrade upgrade) {
        return stack.getOrCreateTagElement(UPGRADE_TAG).getInt(upgrade.id());
    }

    public static void setUpgradeLevel(ItemStack stack, OASUpgrade upgrade, int level) {
        CompoundTag tag = stack.getOrCreateTagElement(UPGRADE_TAG);
        tag.putInt(upgrade.id(), Math.max(0, level));
    }

    public static long getEnergyMultiplier(int lvl) {
        if (lvl <= 2) {
            return 1L << lvl;
        } else if (lvl == 3) {
            return 16L;
        } else if (lvl == 4) {
            return 64L;
        }
        return 1L << lvl;
    }

    public static long getEnergyMultiplier(ItemStack stack) {
        if (stack.isEmpty()) return 1L;
        int lvl = getUpgradeLevel(stack, OASUpgrade.RF_CAP);
        return getEnergyMultiplier(lvl);
    }
}
