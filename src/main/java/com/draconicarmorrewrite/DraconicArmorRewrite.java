package com.draconicarmorrewrite;

import com.draconicarmorrewrite.items.equipment.*;
import net.minecraft.world.item.ArmorItem;
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
    public static final DeferredRegister<net.minecraft.sounds.SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<net.minecraft.sounds.SoundEvent> SHIELD_STRIKE = SOUND_EVENTS.register("shield_strike",
            () -> net.minecraft.sounds.SoundEvent.createVariableRangeEvent(new net.minecraft.resources.ResourceLocation(MODID, "shield_strike")));

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

    // Classic Upgradable Armor - Wyvern
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> WYVERN_HELMET_CLASSIC = ITEMS.register("wyvern_helmet_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.WYVERN_TOOLS, ArmorItem.Type.HELMET, 4000000L, 8000L));
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> WYVERN_CHESTPLATE_CLASSIC = ITEMS.register("wyvern_chestplate_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.WYVERN_TOOLS, ArmorItem.Type.CHESTPLATE, 4000000L, 8000L));
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> WYVERN_LEGGINGS_CLASSIC = ITEMS.register("wyvern_leggings_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.WYVERN_TOOLS, ArmorItem.Type.LEGGINGS, 4000000L, 8000L));
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> WYVERN_BOOTS_CLASSIC = ITEMS.register("wyvern_boots_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.WYVERN_TOOLS, ArmorItem.Type.BOOTS, 4000000L, 8000L));

    // Classic Upgradable Armor - Draconic
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> DRACONIC_HELMET_CLASSIC = ITEMS.register("draconic_helmet_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.DRACONIC_TOOLS, ArmorItem.Type.HELMET, 16000000L, 32000L));
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> DRACONIC_CHESTPLATE_CLASSIC = ITEMS.register("draconic_chestplate_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.DRACONIC_TOOLS, ArmorItem.Type.CHESTPLATE, 16000000L, 32000L));
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> DRACONIC_LEGGINGS_CLASSIC = ITEMS.register("draconic_leggings_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.DRACONIC_TOOLS, ArmorItem.Type.LEGGINGS, 16000000L, 32000L));
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> DRACONIC_BOOTS_CLASSIC = ITEMS.register("draconic_boots_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.DRACONIC_TOOLS, ArmorItem.Type.BOOTS, 16000000L, 32000L));

    // Classic Upgradable Armor - Chaotic
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> CHAOTIC_HELMET_CLASSIC = ITEMS.register("chaotic_helmet_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.CHAOTIC_TOOLS, ArmorItem.Type.HELMET, 64000000L, 128000L));
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> CHAOTIC_CHESTPLATE_CLASSIC = ITEMS.register("chaotic_chestplate_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.CHAOTIC_TOOLS, ArmorItem.Type.CHESTPLATE, 64000000L, 128000L));
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> CHAOTIC_LEGGINGS_CLASSIC = ITEMS.register("chaotic_leggings_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.CHAOTIC_TOOLS, ArmorItem.Type.LEGGINGS, 64000000L, 128000L));
    public static final RegistryObject<com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem> CHAOTIC_BOOTS_CLASSIC = ITEMS.register("chaotic_boots_classic",
            () -> new com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem(com.brandon3055.draconicevolution.init.DEContent.CHAOTIC_TOOLS, ArmorItem.Type.BOOTS, 64000000L, 128000L));

    // Upgrade Cores Map
    public static final java.util.Map<com.draconicarmorrewrite.upgrade.OASUpgrade, RegistryObject<Item>> UPGRADE_CORES = new java.util.EnumMap<>(com.draconicarmorrewrite.upgrade.OASUpgrade.class);

    static {
        for (com.draconicarmorrewrite.upgrade.OASUpgrade upgrade : com.draconicarmorrewrite.upgrade.OASUpgrade.values()) {
            UPGRADE_CORES.put(upgrade, ITEMS.register(upgrade.registryPath() + "_core",
                    () -> new com.draconicarmorrewrite.items.UpgradeCoreItem(upgrade, new Item.Properties().stacksTo(1))));
        }
    }

    // Helpers to create charged and upgraded versions of classic armor
    public static net.minecraft.world.item.ItemStack makeChargedClassic(com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem item) {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
        long baseEnergy = 0;
        if (item.getTechLevel() == com.brandon3055.brandonscore.api.TechLevel.WYVERN) {
            baseEnergy = 4000000L;
        } else if (item.getTechLevel() == com.brandon3055.brandonscore.api.TechLevel.DRACONIC) {
            baseEnergy = 16000000L;
        } else if (item.getTechLevel() == com.brandon3055.brandonscore.api.TechLevel.CHAOTIC) {
            baseEnergy = 64000000L;
        }
        stack.getOrCreateTag().putLong("Energy", baseEnergy);
        double shieldCap = item.getShieldCapacity(stack);
        item.setShieldPoints(stack, shieldCap);
        
        net.minecraft.nbt.CompoundTag nbt = stack.serializeNBT();
        return net.minecraft.world.item.ItemStack.of(nbt);
    }

    public static net.minecraft.world.item.ItemStack makeFullyUpgradedClassic(com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem item) {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item);
        int maxLvl = 0;
        if (item.getTechLevel() == com.brandon3055.brandonscore.api.TechLevel.WYVERN) {
            maxLvl = 2;
        } else if (item.getTechLevel() == com.brandon3055.brandonscore.api.TechLevel.DRACONIC) {
            maxLvl = 3;
        } else if (item.getTechLevel() == com.brandon3055.brandonscore.api.TechLevel.CHAOTIC) {
            maxLvl = 4;
        }

        for (com.draconicarmorrewrite.upgrade.OASUpgrade upgrade : item.getValidUpgrades(stack)) {
            com.draconicarmorrewrite.upgrade.OASUpgradeHelper.setUpgradeLevel(stack, upgrade, maxLvl);
        }

        long baseEnergy = 0;
        if (item.getTechLevel() == com.brandon3055.brandonscore.api.TechLevel.WYVERN) {
            baseEnergy = 4000000L;
        } else if (item.getTechLevel() == com.brandon3055.brandonscore.api.TechLevel.DRACONIC) {
            baseEnergy = 16000000L;
        } else if (item.getTechLevel() == com.brandon3055.brandonscore.api.TechLevel.CHAOTIC) {
            baseEnergy = 64000000L;
        }
        long maxEnergy = baseEnergy * com.draconicarmorrewrite.upgrade.OASUpgradeHelper.getEnergyMultiplier(maxLvl);
        stack.getOrCreateTag().putLong("Energy", maxEnergy);

        double shieldCap = item.getShieldCapacity(stack);
        item.setShieldPoints(stack, shieldCap);

        net.minecraft.nbt.CompoundTag nbt = stack.serializeNBT();
        return net.minecraft.world.item.ItemStack.of(nbt);
    }

    private static void acceptClassicVariants(net.minecraft.world.item.CreativeModeTab.Output output, com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem item) {
        output.accept(item);
        output.accept(makeChargedClassic(item));
        output.accept(makeFullyUpgradedClassic(item));
    }

    // Tab
    public static final RegistryObject<net.minecraft.world.item.CreativeModeTab> DRACONIC_ARMOR_TAB = CREATIVE_MODE_TABS.register("draconic_armor_tab",
            () -> net.minecraft.world.item.CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.translatable("itemGroup.draconic_armor_tab"))
                    .icon(() -> new net.minecraft.world.item.ItemStack(HELMET_CHAOTIC.get()))
                    .displayItems((params, output) -> {
                        // Modular Armor
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

                        // Upgrade Cores
                        UPGRADE_CORES.values().forEach(ro -> output.accept(ro.get()));

                        // Classic Upgradable Armor
                        acceptClassicVariants(output, WYVERN_HELMET_CLASSIC.get());
                        acceptClassicVariants(output, WYVERN_CHESTPLATE_CLASSIC.get());
                        acceptClassicVariants(output, WYVERN_LEGGINGS_CLASSIC.get());
                        acceptClassicVariants(output, WYVERN_BOOTS_CLASSIC.get());
                        acceptClassicVariants(output, DRACONIC_HELMET_CLASSIC.get());
                        acceptClassicVariants(output, DRACONIC_CHESTPLATE_CLASSIC.get());
                        acceptClassicVariants(output, DRACONIC_LEGGINGS_CLASSIC.get());
                        acceptClassicVariants(output, DRACONIC_BOOTS_CLASSIC.get());
                        acceptClassicVariants(output, CHAOTIC_HELMET_CLASSIC.get());
                        acceptClassicVariants(output, CHAOTIC_CHESTPLATE_CLASSIC.get());
                        acceptClassicVariants(output, CHAOTIC_LEGGINGS_CLASSIC.get());
                        acceptClassicVariants(output, CHAOTIC_BOOTS_CLASSIC.get());
                    })
                    .build());

    public DraconicArmorRewrite() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        com.draconicarmorrewrite.recipe.RecipeSerializers.register(modEventBus);

        com.draconicarmorrewrite.network.PacketHandler.init();
        com.draconicarmorrewrite.UpgradableArmorEventHandler.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DraconicArmorRewriteConfig.CLIENT_SPEC);
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT) {
            ClientSetup.init();
            modEventBus.addListener(ClientSetup::clientSetup);
        }
    }
}
