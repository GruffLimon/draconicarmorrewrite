package com.draconicarmorrewrite.recipe;

import com.draconicarmorrewrite.items.OASUpgradableItem;
import com.draconicarmorrewrite.upgrade.OASUpgrade;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.DraconicAPI;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import com.brandon3055.draconicevolution.api.crafting.IFusionRecipe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class ClassicFusionUpgradeRecipe extends FusionRecipe {
    private final OASUpgrade upgrade;
    private final int upgradeLevel;

    public ClassicFusionUpgradeRecipe(ResourceLocation id, ItemStack result, Ingredient catalyst, long totalEnergy, TechLevel tier, Collection<FusionRecipe.FusionIngredient> ingredients, OASUpgrade upgrade, int upgradeLevel) {
        super(id, result, catalyst, totalEnergy, tier, ingredients);
        this.upgrade = upgrade;
        this.upgradeLevel = upgradeLevel;
    }

    public OASUpgrade getUpgrade() {
        return this.upgrade;
    }

    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        ItemStack result = super.getResultItem(registryAccess).copy();
        Item item = result.getItem();
        if (item instanceof OASUpgradableItem upgradableItem) {
            if (upgradableItem.supportsUpgrade(result, this.upgrade) && this.upgradeLevel >= 1 && this.upgradeLevel <= Math.min(this.upgrade.maxRecipeLevel(), upgradableItem.getMaxUpgradeLevel(result, this.upgrade))) {
                return upgradableItem.applyUpgrade(result, this.upgrade, this.upgradeLevel);
            }
        }
        return result;
    }

    @Override
    public ItemStack assemble(IFusionInventory inventory, RegistryAccess registryAccess) {
        ItemStack catalystStack = inventory.getCatalystStack();
        Item item = catalystStack.getItem();
        if (item instanceof OASUpgradableItem upgradableItem) {
            return upgradableItem.applyUpgrade(catalystStack, this.upgrade, this.upgradeLevel);
        }
        return super.assemble(inventory, registryAccess);
    }

    @Override
    public boolean canStartCraft(IFusionInventory inventory, Level level, Consumer<Component> consumer) {
        ItemStack catalystStack = inventory.getCatalystStack();
        Item item = catalystStack.getItem();
        if (!(item instanceof OASUpgradableItem upgradableItem)) {
            if (consumer != null) {
                consumer.accept(Component.translatable("fusion_status.draconicarmorrewrite.catalyst_not_upgradable").withStyle(ChatFormatting.RED));
            }
            return false;
        }
        if (!upgradableItem.canApplyUpgrade(catalystStack, this.upgrade, this.upgradeLevel)) {
            if (consumer != null) {
                consumer.accept(Component.translatable("fusion_status.draconicarmorrewrite.upgrade_order_invalid").withStyle(ChatFormatting.RED));
            }
            return false;
        }
        
        // Standard check
        return ClassicFusionRecipeChecks.canStart(this, inventory, level, consumer);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return com.draconicarmorrewrite.recipe.RecipeSerializers.CLASSIC_FUSION_UPGRADE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return DraconicAPI.FUSION_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ClassicFusionUpgradeRecipe> {
        @Override
        public ClassicFusionUpgradeRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient catalyst = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "catalyst"));
            OASUpgrade upgrade = OASUpgrade.byId(GsonHelper.getAsString(json, "upgrade"));
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            ArrayList<FusionRecipe.FusionIngredient> ingredients = new ArrayList<>();
            for (int i = 0; i < ingredientsJson.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientsJson.get(i));
                ingredients.add(new FusionRecipe.FusionIngredient(ingredient, !isUpgradeCoreIngredient(ingredient, upgrade)));
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            TechLevel tier = TechLevel.valueOf(GsonHelper.getAsString(json, "tier").toUpperCase());
            long totalEnergy = GsonHelper.getAsLong(json, "total_energy");
            int upgradeLevel = GsonHelper.getAsInt(json, "upgrade_level");
            return new ClassicFusionUpgradeRecipe(recipeId, result, catalyst, totalEnergy, tier, ingredients, upgrade, upgradeLevel);
        }

        private static boolean isUpgradeCoreIngredient(Ingredient ingredient, OASUpgrade upgrade) {
            var registryObject = com.draconicarmorrewrite.DraconicArmorRewrite.UPGRADE_CORES.get(upgrade);
            if (registryObject == null) return false;
            return ingredient.test(new ItemStack(registryObject.get()));
        }

        @Override
        public ClassicFusionUpgradeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack result = buffer.readItem();
            Ingredient catalyst = Ingredient.fromNetwork(buffer);
            long totalEnergy = buffer.readVarLong();
            TechLevel tier = buffer.readEnum(TechLevel.class);
            int ingredientCount = buffer.readVarInt();
            ArrayList<FusionRecipe.FusionIngredient> ingredients = new ArrayList<>();
            for (int i = 0; i < ingredientCount; ++i) {
                ingredients.add(new FusionRecipe.FusionIngredient(Ingredient.fromNetwork(buffer), buffer.readBoolean()));
            }
            OASUpgrade upgrade = buffer.readEnum(OASUpgrade.class);
            int upgradeLevel = buffer.readVarInt();
            return new ClassicFusionUpgradeRecipe(recipeId, result, catalyst, totalEnergy, tier, ingredients, upgrade, upgradeLevel);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ClassicFusionUpgradeRecipe recipe) {
            buffer.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
            recipe.getCatalyst().toNetwork(buffer);
            buffer.writeVarLong(recipe.getEnergyCost());
            buffer.writeEnum(recipe.getRecipeTier());
            buffer.writeVarInt(recipe.fusionIngredients().size());
            for (IFusionRecipe.IFusionIngredient ingredient : recipe.fusionIngredients()) {
                ingredient.get().toNetwork(buffer);
                buffer.writeBoolean(ingredient.consume());
            }
            buffer.writeEnum(recipe.upgrade);
            buffer.writeVarInt(recipe.upgradeLevel);
        }
    }
}
