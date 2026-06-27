/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.item.ItemStack
 */
package com.draconicarmorrewrite.config;

import com.draconicarmorrewrite.config.OASArmorConfigKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class OASArmorConfig {
    public static final String CONFIG_TAG = "oas_armor_config";

    private OASArmorConfig() {
    }

    public static int getInt(ItemStack stack, OASArmorConfigKey key, int defaultValue) {
        CompoundTag tag = stack.getTagElement(CONFIG_TAG);
        return tag != null && tag.contains(key.key()) ? tag.getInt(key.key()) : defaultValue;
    }

    public static boolean getBoolean(ItemStack stack, OASArmorConfigKey key, boolean defaultValue) {
        CompoundTag tag = stack.getTagElement(CONFIG_TAG);
        return tag != null && tag.contains(key.key()) ? tag.getBoolean(key.key()) : defaultValue;
    }

    public static double getDouble(ItemStack stack, OASArmorConfigKey key, double defaultValue) {
        CompoundTag tag = stack.getTagElement(CONFIG_TAG);
        return tag != null && tag.contains(key.key()) ? tag.getDouble(key.key()) : defaultValue;
    }

    public static void setInt(ItemStack stack, OASArmorConfigKey key, int value) {
        stack.getOrCreateTagElement(CONFIG_TAG).putInt(key.key(), value);
    }

    public static void setBoolean(ItemStack stack, OASArmorConfigKey key, boolean value) {
        stack.getOrCreateTagElement(CONFIG_TAG).putBoolean(key.key(), value);
    }

    public static void setDouble(ItemStack stack, OASArmorConfigKey key, double value) {
        stack.getOrCreateTagElement(CONFIG_TAG).putDouble(key.key(), value);
    }
}
