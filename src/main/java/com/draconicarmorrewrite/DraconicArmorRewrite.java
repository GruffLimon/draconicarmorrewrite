package com.draconicarmorrewrite;

import com.draconicarmorrewrite.items.equipment.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(DraconicArmorRewrite.MODID)
public class DraconicArmorRewrite {
    public static final String MODID = "draconicarmorrewrite";
    public static int currentBrightness;
    public static int currentOverlay;

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Helmets
    public static final RegistryObject<ModularHelmet> HELMET_WYVERN = ITEMS.register("wyvern_helmet",
            () -> new ModularHelmet(com.brandon3055.draconicevolution.init.DEContent.WYVERN_TOOLS));
    public static final RegistryObject<ModularHelmet> HELMET_DRACONIC = ITEMS.register("draconic_helmet",
            () -> new ModularHelmet(com.brandon3055.draconicevolution.init.DEContent.DRACONIC_TOOLS));
    public static final RegistryObject<ModularHelmet> HELMET_CHAOTIC = ITEMS.register("chaotic_helmet",
            () -> new ModularHelmet(com.brandon3055.draconicevolution.init.DEContent.CHAOTIC_TOOLS));

    // Chestpieces
    public static final RegistryObject<ModularChestpiece> CHESTPIECE_WYVERN = ITEMS.register("wyvern_chestplate",
            () -> new ModularChestpiece(com.brandon3055.draconicevolution.init.DEContent.WYVERN_TOOLS));
    public static final RegistryObject<ModularChestpiece> CHESTPIECE_DRACONIC = ITEMS.register("draconic_chestplate",
            () -> new ModularChestpiece(com.brandon3055.draconicevolution.init.DEContent.DRACONIC_TOOLS));
    public static final RegistryObject<ModularChestpiece> CHESTPIECE_CHAOTIC = ITEMS.register("chaotic_chestplate",
            () -> new ModularChestpiece(com.brandon3055.draconicevolution.init.DEContent.CHAOTIC_TOOLS));

    // Leggings
    public static final RegistryObject<ModularLeggings> LEGGINGS_WYVERN = ITEMS.register("wyvern_leggings",
            () -> new ModularLeggings(com.brandon3055.draconicevolution.init.DEContent.WYVERN_TOOLS));
    public static final RegistryObject<ModularLeggings> LEGGINGS_DRACONIC = ITEMS.register("draconic_leggings",
            () -> new ModularLeggings(com.brandon3055.draconicevolution.init.DEContent.DRACONIC_TOOLS));
    public static final RegistryObject<ModularLeggings> LEGGINGS_CHAOTIC = ITEMS.register("chaotic_leggings",
            () -> new ModularLeggings(com.brandon3055.draconicevolution.init.DEContent.CHAOTIC_TOOLS));

    // Boots
    public static final RegistryObject<ModularBoots> BOOTS_WYVERN = ITEMS.register("wyvern_boots",
            () -> new ModularBoots(com.brandon3055.draconicevolution.init.DEContent.WYVERN_TOOLS));
    public static final RegistryObject<ModularBoots> BOOTS_DRACONIC = ITEMS.register("draconic_boots",
            () -> new ModularBoots(com.brandon3055.draconicevolution.init.DEContent.DRACONIC_TOOLS));
    public static final RegistryObject<ModularBoots> BOOTS_CHAOTIC = ITEMS.register("chaotic_boots",
            () -> new ModularBoots(com.brandon3055.draconicevolution.init.DEContent.CHAOTIC_TOOLS));

    public DraconicArmorRewrite() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DraconicArmorRewriteConfig.CLIENT_SPEC);
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            ClientSetup.init();
            modEventBus.addListener(ClientSetup::clientSetup);
        }
    }
}
