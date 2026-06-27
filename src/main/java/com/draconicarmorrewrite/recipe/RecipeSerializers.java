package com.draconicarmorrewrite.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class RecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, com.draconicarmorrewrite.DraconicArmorRewrite.MODID);

    public static final RegistryObject<RecipeSerializer<ClassicFusionUpgradeRecipe>> CLASSIC_FUSION_UPGRADE = 
            RECIPE_SERIALIZERS.register("classic_fusion_upgrade", ClassicFusionUpgradeRecipe.Serializer::new);

    public static final RegistryObject<RecipeSerializer<ClassicFusionTransformRecipe>> CLASSIC_FUSION_TRANSFORM = 
            RECIPE_SERIALIZERS.register("classic_fusion_transform", ClassicFusionTransformRecipe.Serializer::new);

    private RecipeSerializers() {
    }

    public static void register(IEventBus modBus) {
        RECIPE_SERIALIZERS.register(modBus);
    }
}
