package com.draconicarmorrewrite.recipe;

import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

final class ClassicFusionRecipeChecks {
    private ClassicFusionRecipeChecks() {
    }

    static boolean canStart(IFusionRecipe recipe, IFusionInventory inventory, Level level, Consumer<Component> consumer) {
        ItemStack result = recipe.getResultItem(level.registryAccess());
        ItemStack outputStack = inventory.getOutputStack();
        if (!(outputStack.isEmpty() || (ItemStack.isSameItemSameTags(outputStack, result) && outputStack.getCount() + result.getCount() <= result.getMaxStackSize()))) {
            sendStatus(consumer, "fusion_status.draconicevolution.output_obstructed", ChatFormatting.RED);
            return false;
        }
        if (inventory.getMinimumTier().index < recipe.getRecipeTier().index) {
            sendStatus(consumer, "fusion_status.draconicevolution.tier_low", ChatFormatting.RED);
            return false;
        }
        sendStatus(consumer, "fusion_status.draconicevolution.ready", ChatFormatting.GREEN);
        return true;
    }

    private static void sendStatus(Consumer<Component> consumer, String translationKey, ChatFormatting color) {
        if (consumer != null) {
            consumer.accept(Component.translatable(translationKey).withStyle(color));
        }
    }
}
