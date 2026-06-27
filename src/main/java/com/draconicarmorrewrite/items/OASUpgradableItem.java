package com.draconicarmorrewrite.items;

import com.draconicarmorrewrite.upgrade.OASUpgrade;
import com.draconicarmorrewrite.upgrade.OASUpgradeHelper;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.crafting.IFusionDataTransfer;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface OASUpgradableItem extends IFusionDataTransfer {
    Set<OASUpgrade> getValidUpgrades(ItemStack stack);
    int getMaxUpgradeLevel(ItemStack stack, OASUpgrade upgrade);

    default boolean supportsUpgrade(ItemStack stack, OASUpgrade upgrade) {
        return this.getValidUpgrades(stack).contains(upgrade);
    }

    default int getUpgradeLevel(ItemStack stack, OASUpgrade upgrade) {
        return OASUpgradeHelper.getUpgradeLevel(stack, upgrade);
    }

    default boolean canApplyUpgrade(ItemStack stack, OASUpgrade upgrade, int targetLevel) {
        if (!this.supportsUpgrade(stack, upgrade)) {
            return false;
        }
        int maxLevel = Math.min(upgrade.maxRecipeLevel(), this.getMaxUpgradeLevel(stack, upgrade));
        return targetLevel >= 1 && targetLevel <= maxLevel && this.getUpgradeLevel(stack, upgrade) == targetLevel - 1;
    }

    default ItemStack applyUpgrade(ItemStack stack, OASUpgrade upgrade, int targetLevel) {
        ItemStack result = stack.copy();
        OASUpgradeHelper.setUpgradeLevel(result, upgrade, targetLevel);
        return result;
    }

    @Override
    default void transferIngredientData(ItemStack result, IFusionInventory inventory) {
        ItemStack catalyst = inventory.getCatalystStack();
        if (!catalyst.isEmpty() && catalyst.hasTag()) {
            result.setTag(catalyst.getTag().copy());
        }
    }

    default long getEnergyStored(ItemStack stack) {
        return stack.getCapability(DECapabilities.OP_STORAGE).map(com.brandon3055.brandonscore.api.power.IOPStorage::getOPStored).orElse(0L);
    }

    default long getEnergyCapacity(ItemStack stack) {
        return stack.getCapability(DECapabilities.OP_STORAGE).map(com.brandon3055.brandonscore.api.power.IOPStorage::getMaxOPStored).orElse(0L);
    }

    default void appendStandardHoverText(ItemStack stack, List<Component> tooltip) {
        if (isControlDown()) {
            tooltip.add(Component.translatable("tooltip.originalarmorstuff.upgrades").withStyle(ChatFormatting.GOLD));
            for (OASUpgrade upgrade : OASUpgrade.values()) {
                if (!this.supportsUpgrade(stack, upgrade)) continue;
                tooltip.add(this.upgradeLine(stack, upgrade));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.originalarmorstuff.press_ctrl", 
                    Component.literal("CTRL").withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC)
            ).withStyle(ChatFormatting.GRAY));
            
            tooltip.add(Component.translatable("tooltip.originalarmorstuff.charge", 
                    formatRf(this.getEnergyStored(stack)), 
                    formatRf(this.getEnergyCapacity(stack))
            ).withStyle(ChatFormatting.GRAY));
        }
    }

    private Component upgradeLine(ItemStack stack, OASUpgrade upgrade) {
        int level = OASUpgradeHelper.getUpgradeLevel(stack, upgrade);
        return Component.translatable(upgrade.translationKey()).withStyle(ChatFormatting.AQUA)
                .append(Component.literal(" "))
                .append(formatUpgradeLevel(level).withStyle(level > 0 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
    }

    private static MutableComponent formatUpgradeLevel(int level) {
        return switch (level) {
            case 1 -> Component.translatable("tier.originalarmorstuff.basic");
            case 2 -> Component.translatable("tier.originalarmorstuff.wyvern");
            case 3 -> Component.translatable("tier.originalarmorstuff.draconic");
            case 4 -> Component.translatable("tier.originalarmorstuff.chaotic");
            default -> Component.translatable("tooltip.originalarmorstuff.level.none");
        };
    }

    private static String formatRf(long value) {
        if (value >= 1000000000L) {
            return String.format(Locale.ROOT, "%.3fG", (double)value / 1.0E9);
        }
        if (value >= 1000000L) {
            return String.format(Locale.ROOT, "%.1fM", (double)value / 1000000.0);
        }
        if (value >= 1000L) {
            return String.format(Locale.ROOT, "%.1fk", (double)value / 1000.0);
        }
        return Long.toString(value);
    }

    private static boolean isControlDown() {
        return FMLEnvironment.dist == Dist.CLIENT && Screen.hasControlDown();
    }
}
