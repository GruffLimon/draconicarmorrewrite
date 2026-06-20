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
    public static final DeferredRegister<net.minecraft.world.item.CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, MODID);

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

    // Tab
    public static final RegistryObject<net.minecraft.world.item.CreativeModeTab> DRACONIC_ARMOR_TAB = CREATIVE_MODE_TABS.register("draconic_armor_tab",
            () -> net.minecraft.world.item.CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.draconic_armor_tab"))
                    .icon(() -> new net.minecraft.world.item.ItemStack(HELMET_CHAOTIC.get()))
                    .displayItems((params, output) -> {
                        output.accept(HELMET_WYVERN.get());
                        output.accept(CHESTPIECE_WYVERN.get());
                        output.accept(LEGGINGS_WYVERN.get());
                        output.accept(BOOTS_WYVERN.get());
                        output.accept(HELMET_DRACONIC.get());
                        output.accept(CHESTPIECE_DRACONIC.get());
                        output.accept(LEGGINGS_DRACONIC.get());
                        output.accept(BOOTS_DRACONIC.get());
                        output.accept(HELMET_CHAOTIC.get());
                        output.accept(CHESTPIECE_CHAOTIC.get());
                        output.accept(LEGGINGS_CHAOTIC.get());
                        output.accept(BOOTS_CHAOTIC.get());
                    })
                    .build());

    public DraconicArmorRewrite() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DraconicArmorRewriteConfig.CLIENT_SPEC);
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            ClientSetup.init();
            modEventBus.addListener(ClientSetup::clientSetup);
        }
    }
}
