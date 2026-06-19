package com.draconicarmorrewrite.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.capability.MultiCapabilityProvider;
import com.brandon3055.brandonscore.client.model.DummyHumanoidModel;
import com.brandon3055.brandonscore.client.render.EquippedItemModel;
import com.brandon3055.brandonscore.items.EquippedModelItem;
import com.brandon3055.draconicevolution.items.equipment.IModularArmor;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.draconicarmorrewrite.client.model.ModularArmorModel;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.init.TechProperties;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.integration.equipment.IDEEquipment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ModularLeggings extends ArmorItem implements IModularArmor, IDEEquipment, EquippedModelItem {
    private final TechLevel techLevel;

    public ModularLeggings(TechProperties props) {
        super(ModularArmorMaterial.INSTANCE, Type.LEGGINGS, props);
        this.techLevel = props.getTechLevel();
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        if (entity instanceof LivingEntity && !EquipmentManager.findItem(e -> e.getItem() instanceof ModularLeggings, (LivingEntity) entity).isEmpty()) {
            return false;
        }
        return Mob.getEquipmentSlotForItem(stack) == armorType;
    }

    @Override
    public boolean canEquip(ItemStack stack, LivingEntity livingEntity, String slotID) {
        if (!slotID.equals("legs") || !EquipmentManager.findItem(e -> e.getItem() instanceof ModularLeggings, livingEntity).isEmpty()) {
            return false;
        }
        return !(livingEntity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ModularLeggings);
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.chestpieceWidth(techLevel), ModuleCfg.chestpieceHeight(techLevel), "legs", ModuleCfg.removeInvalidModules);
        host.addCategories(ModuleCategory.ARMOR, ModuleCategory.ARMOR_LEGS, ModuleCategory.ALL, ModuleCategory.CHESTPIECE);
        host.blackListType(ModuleTypes.SHIELD_CONTROLLER);
        return host;
    }

    @Override
    public Component getName(ItemStack stack) {
        Component baseName = super.getName(stack);
        net.minecraft.ChatFormatting color;
        if (techLevel == TechLevel.WYVERN) {
            color = net.minecraft.ChatFormatting.AQUA;
        } else if (techLevel == TechLevel.DRACONIC) {
            color = net.minecraft.ChatFormatting.LIGHT_PURPLE;
        } else if (techLevel == TechLevel.CHAOTIC) {
            color = net.minecraft.ChatFormatting.RED;
        } else {
            color = net.minecraft.ChatFormatting.WHITE;
        }
        return baseName.copy().withStyle(color);
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseChestpieceEnergy(techLevel), EquipCfg.getBaseChestpieceTransfer(techLevel));
    }

    @Override
    public void initCapabilities(ItemStack stack, ModuleHostImpl host, MultiCapabilityProvider provider) {
        EquipmentManager.addCaps(stack, provider);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        if (techLevel == TechLevel.CHAOTIC) { consumer.accept(DummyHumanoidModel.DUMMY_ITEM_RENDER_PROPS); }
    }

    @Override
    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String level = this.techLevel.name().toLowerCase(java.util.Locale.ROOT);
        return "draconicarmorrewrite:textures/armor/classic/" + level + "_armor_layer_2.png";
    }

    @OnlyIn(Dist.CLIENT)
    private ModularArmorModel<?> model;

    @OnlyIn(Dist.CLIENT)
    private ModularArmorModel<?> model_on_armor;

    @Override
    @OnlyIn(Dist.CLIENT)
    public EquippedItemModel getExtendedModel(LivingEntity entity, ItemStack stack, @Nullable EquipmentSlot slot, HumanoidModel<?> parentModel, boolean slim) {
        ItemStack item = entity.getItemBySlot(EquipmentSlot.LEGS);
        boolean onArmor = slot == null && !item.isEmpty() && item.getItem() instanceof ArmorItem;
        if (model == null || model_on_armor == null) {
            model = new ModularArmorModel<>(techLevel, false, EquipmentSlot.LEGS);
            model_on_armor = new ModularArmorModel<>(techLevel, true, EquipmentSlot.LEGS);
        }
        ModularArmorModel<?> activeModel = onArmor ? model_on_armor : model;
        ForgeHooksClient.copyModelProperties(parentModel, activeModel);
        return activeModel;
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0 && entity.pickupDelay != 32767) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public boolean isEnchantable(ItemStack p_41456_) {
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return damageBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return damageBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return damageBarColour(stack);
    }
}
