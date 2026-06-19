package com.draconicarmorrewrite;

import net.minecraft.client.gui.screens.MenuScreens;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.client.gui.modular.ModularItemGui;
import com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui;

public class ClientSetup {
    public static void init() {
        com.brandon3055.draconicevolution.client.DEShaders.TOOL_BASE_SHADER.onShaderApplied(shader -> {
            codechicken.lib.render.shader.CCUniform simpleLight = shader.getShaderInstance().getUniform("SimpleLight");
            if (simpleLight != null) {
                simpleLight.set(0);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static void registerScreenOverride(
            net.minecraft.world.inventory.MenuType<?> type,
            net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor factory) {
        try {
            java.util.Map<net.minecraft.world.inventory.MenuType<?>, net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor<?, ?>> screensMap;
            try {
                java.lang.reflect.Field field = net.minecraft.client.gui.screens.MenuScreens.class.getDeclaredField("SCREENS");
                field.setAccessible(true);
                screensMap = (java.util.Map<net.minecraft.world.inventory.MenuType<?>, net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor<?, ?>>) field.get(null);
            } catch (NoSuchFieldException e) {
                java.lang.reflect.Field field = net.minecraft.client.gui.screens.MenuScreens.class.getDeclaredField("f_96196_");
                field.setAccessible(true);
                screensMap = (java.util.Map<net.minecraft.world.inventory.MenuType<?>, net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor<?, ?>>) field.get(null);
            }
            if (screensMap != null) {
                screensMap.put(type, factory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.world.inventory.MenuType<?> registeredModularItem = net.minecraftforge.registries.ForgeRegistries.MENU_TYPES.getValue(new net.minecraft.resources.ResourceLocation("draconicevolution", "modular_item"));
            net.minecraft.world.inventory.MenuType<?> registeredConfigurableItem = net.minecraftforge.registries.ForgeRegistries.MENU_TYPES.getValue(new net.minecraft.resources.ResourceLocation("draconicevolution", "configurable_item"));

            if (registeredModularItem != null) {
                registerScreenOverride(registeredModularItem, (menu, inv, title) -> new com.brandon3055.draconicevolution.client.gui.modular.ModularItemGui.Screen((com.brandon3055.draconicevolution.inventory.ModularItemMenu) menu, inv, title));
            }
            if (registeredConfigurableItem != null) {
                registerScreenOverride(registeredConfigurableItem, (menu, inv, title) -> new com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui.Screen((com.brandon3055.draconicevolution.inventory.ConfigurableItemMenu) menu, inv, title));
            }

            try {
                registerScreenOverride(DEContent.MENU_MODULAR_ITEM.get(), (menu, inv, title) -> new com.brandon3055.draconicevolution.client.gui.modular.ModularItemGui.Screen((com.brandon3055.draconicevolution.inventory.ModularItemMenu) menu, inv, title));
                registerScreenOverride(DEContent.MENU_CONFIGURABLE_ITEM.get(), (menu, inv, title) -> new com.brandon3055.draconicevolution.client.gui.modular.itemconfig.ConfigurableItemGui.Screen((com.brandon3055.draconicevolution.inventory.ConfigurableItemMenu) menu, inv, title));
            } catch (Exception ignored) {}
        });

        if (com.draconicarmorrewrite.DraconicArmorRewriteConfig.useClassicModels()) {
            return;
        }
        event.enqueueWork(() -> {
            codechicken.lib.model.ModelRegistryHelper modelHelper = new codechicken.lib.model.ModelRegistryHelper();

            modelHelper.register(
                new net.minecraft.client.resources.model.ModelResourceLocation(
                    net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(DraconicArmorRewrite.CHESTPIECE_WYVERN.get()),
                    "inventory"
                ),
                new com.brandon3055.draconicevolution.client.render.item.RenderModularChestpiece(com.brandon3055.brandonscore.api.TechLevel.WYVERN)
            );

            modelHelper.register(
                new net.minecraft.client.resources.model.ModelResourceLocation(
                    net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(DraconicArmorRewrite.CHESTPIECE_DRACONIC.get()),
                    "inventory"
                ),
                new com.brandon3055.draconicevolution.client.render.item.RenderModularChestpiece(com.brandon3055.brandonscore.api.TechLevel.DRACONIC)
            );

            modelHelper.register(
                new net.minecraft.client.resources.model.ModelResourceLocation(
                    net.minecraftforge.registries.ForgeRegistries.ITEMS.getKey(DraconicArmorRewrite.CHESTPIECE_CHAOTIC.get()),
                    "inventory"
                ),
                new com.brandon3055.draconicevolution.client.render.item.RenderModularChestpiece(com.brandon3055.brandonscore.api.TechLevel.CHAOTIC)
            );
        });
    }
}
