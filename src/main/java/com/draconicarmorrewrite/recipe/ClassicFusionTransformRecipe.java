package com.draconicarmorrewrite.recipe;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class ClassicFusionTransformRecipe extends FusionRecipe {
    public ClassicFusionTransformRecipe(ResourceLocation id, ItemStack result, Ingredient catalyst, long totalEnergy, TechLevel tier, Collection<FusionRecipe.FusionIngredient> ingredients) {
        super(id, result, catalyst, totalEnergy, tier, ingredients);
    }

    @Override
    public ItemStack assemble(IFusionInventory inventory, RegistryAccess registryAccess) {
        ItemStack catalystStack = inventory.getCatalystStack();
        ItemStack result = this.getResultItem(registryAccess).copy();
        if (catalystStack.hasTag()) {
            result.setTag(catalystStack.getTag().copy());
        }
        return result;
    }

    @Override
    public boolean canStartCraft(IFusionInventory inventory, Level level, Consumer<Component> consumer) {
        ItemStack catalystStack = inventory.getCatalystStack();
        if (!this.getCatalyst().test(catalystStack)) {
            if (consumer != null) {
                consumer.accept(Component.translatable("fusion_status.draconicarmorrewrite.transform_catalyst_invalid").withStyle(ChatFormatting.RED));
            }
            return false;
        }
        
        // Standard check
        return ClassicFusionRecipeChecks.canStart(this, inventory, level, consumer);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return com.draconicarmorrewrite.recipe.RecipeSerializers.CLASSIC_FUSION_TRANSFORM.get();
    }

    @Override
    public RecipeType<?> getType() {
        return DraconicAPI.FUSION_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ClassicFusionTransformRecipe> {
        @Override
        public ClassicFusionTransformRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient catalyst = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "catalyst"));
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            ArrayList<FusionRecipe.FusionIngredient> ingredients = new ArrayList<>();
            for (int i = 0; i < ingredientsJson.size(); ++i) {
                ingredients.add(new FusionRecipe.FusionIngredient(Ingredient.fromJson(ingredientsJson.get(i)), true));
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            TechLevel tier = TechLevel.valueOf(GsonHelper.getAsString(json, "tier").toUpperCase());
            long totalEnergy = GsonHelper.getAsLong(json, "total_energy");
            return new ClassicFusionTransformRecipe(recipeId, result, catalyst, totalEnergy, tier, ingredients);
        }

        @Override
        public ClassicFusionTransformRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack result = buffer.readItem();
            Ingredient catalyst = Ingredient.fromNetwork(buffer);
            long totalEnergy = buffer.readVarLong();
            TechLevel tier = buffer.readEnum(TechLevel.class);
            int ingredientCount = buffer.readVarInt();
            ArrayList<FusionRecipe.FusionIngredient> ingredients = new ArrayList<>();
            for (int i = 0; i < ingredientCount; ++i) {
                ingredients.add(new FusionRecipe.FusionIngredient(Ingredient.fromNetwork(buffer), buffer.readBoolean()));
            }
            return new ClassicFusionTransformRecipe(recipeId, result, catalyst, totalEnergy, tier, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ClassicFusionTransformRecipe recipe) {
            buffer.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
            recipe.getCatalyst().toNetwork(buffer);
            buffer.writeVarLong(recipe.getEnergyCost());
            buffer.writeEnum(recipe.getRecipeTier());
            buffer.writeVarInt(recipe.fusionIngredients().size());
            for (IFusionRecipe.IFusionIngredient ingredient : recipe.fusionIngredients()) {
                ingredient.get().toNetwork(buffer);
                buffer.writeBoolean(ingredient.consume());
            }
        }
    }
}
