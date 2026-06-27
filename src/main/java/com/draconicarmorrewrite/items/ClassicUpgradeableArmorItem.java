package com.draconicarmorrewrite.items;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.capability.MultiCapabilityProvider;
import com.brandon3055.brandonscore.client.model.DummyHumanoidModel;
import com.brandon3055.brandonscore.client.render.EquippedItemModel;
import com.brandon3055.brandonscore.items.EquippedModelItem;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.crafting.IFusionInventory;
import com.brandon3055.draconicevolution.api.crafting.IFusionInjector;
import com.brandon3055.draconicevolution.init.TechProperties;
import com.brandon3055.draconicevolution.integration.equipment.IDEEquipment;
import com.draconicarmorrewrite.client.model.ModularArmorModel;
import com.draconicarmorrewrite.items.equipment.ModularArmorMaterial;
import com.draconicarmorrewrite.ClientSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import com.draconicarmorrewrite.config.OASArmorConfig;
import com.draconicarmorrewrite.config.OASArmorConfigKey;
import com.draconicarmorrewrite.network.ArmorConfigAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import com.draconicarmorrewrite.upgrade.OASUpgrade;
import com.draconicarmorrewrite.upgrade.OASUpgradeHelper;
import java.util.Set;
import java.util.List;
import java.util.function.Consumer;

public class ClassicUpgradeableArmorItem extends ArmorItem implements IDEEquipment, EquippedModelItem, OASUpgradableItem {
    private final TechLevel techLevel;
    private final long baseEnergy;
    private final long baseTransfer;

    public double getShieldCapacity(ItemStack stack) {
        int upgradeLevel = OASUpgradeHelper.getUpgradeLevel(stack, OASUpgrade.SHIELD_CAP);
        double baseCap = this.techLevel == TechLevel.WYVERN ? 256.0 : (this.techLevel == TechLevel.DRACONIC ? 512.0 : 1024.0);
        double share = switch (getEquipmentSlot()) {
            case HEAD, FEET -> 0.15;
            case LEGS -> 0.30;
            case CHEST -> 0.40;
            default -> 0.0;
        };
        return baseCap * share * (upgradeLevel + 1);
    }

    public double getShieldRecovery(ItemStack stack) {
        int upgradeLevel = OASUpgradeHelper.getUpgradeLevel(stack, OASUpgrade.SHIELD_REC);
        double baseRec = this.techLevel == TechLevel.WYVERN ? 2.0 : (this.techLevel == TechLevel.DRACONIC ? 4.0 : 8.0);
        return baseRec * (upgradeLevel + 1);
    }

    public double getShieldPoints(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains("ShieldPoints")) {
            return 0.0;
        }
        return Math.min(stack.getTag().getDouble("ShieldPoints"), getShieldCapacity(stack));
    }

    public void setShieldPoints(ItemStack stack, double value) {
        double next = Math.max(0.0, Math.min(value, getShieldCapacity(stack)));
        stack.getOrCreateTag().putDouble("ShieldPoints", next);
    }

    public double getShieldEntropy(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains("ShieldEntropy")) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(100.0, stack.getTag().getDouble("ShieldEntropy")));
    }

    public void setShieldEntropy(ItemStack stack, double value) {
        double next = Math.max(0.0, Math.min(100.0, value));
        stack.getOrCreateTag().putDouble("ShieldEntropy", next);
    }

    public double absorbShieldPoints(ItemStack stack, double incomingDamage, boolean simulate) {
        double availableShield = getShieldPoints(stack);
        if (availableShield <= 0.0 || incomingDamage <= 0.0) {
            return 0.0;
        }
        double protectedPoints = Math.min(availableShield, incomingDamage);
        if (!simulate) {
            setShieldPoints(stack, availableShield - protectedPoints);
        }
        return protectedPoints;
    }

    public ClassicUpgradeableArmorItem(TechProperties props, Type type, long baseEnergy, long baseTransfer) {
        super(ModularArmorMaterial.INSTANCE, type, props);
        this.techLevel = props.getTechLevel();
        this.baseEnergy = baseEnergy;
        this.baseTransfer = baseTransfer;
    }

    public TechLevel getTechLevel() {
        return this.techLevel;
    }

    /** Returns true if this armor is Draconic tier (for GUI config screen compatibility) */
    public boolean isDraconic() {
        return this.techLevel == com.brandon3055.brandonscore.api.TechLevel.DRACONIC
                || this.techLevel == com.brandon3055.brandonscore.api.TechLevel.CHAOTIC;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return getEquipmentSlot() == armorType;
    }

    @Override
    public boolean canEquip(ItemStack stack, LivingEntity livingEntity, String slotID) {
        if (slotID.equals("head") && getEquipmentSlot() == EquipmentSlot.HEAD) return true;
        if (slotID.equals("body") && getEquipmentSlot() == EquipmentSlot.CHEST) return true;
        if (slotID.equals("legs") && getEquipmentSlot() == EquipmentSlot.LEGS) return true;
        if (slotID.equals("feet") && getEquipmentSlot() == EquipmentSlot.FEET) return true;
        return false;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
        MultiCapabilityProvider provider = new MultiCapabilityProvider();
        UpgradableOPStorage opStorage = new UpgradableOPStorage(stack, baseEnergy, baseTransfer);
        provider.addCapability(opStorage, "energy", new Capability[]{
                DECapabilities.OP_STORAGE,
                ForgeCapabilities.ENERGY
        });
        return provider;
    }

    @Override
    public Set<OASUpgrade> getValidUpgrades(ItemStack stack) {
        return switch (getEquipmentSlot()) {
            case HEAD -> Set.of(OASUpgrade.RF_CAP, OASUpgrade.SHIELD_CAP, OASUpgrade.SHIELD_REC);
            case CHEST -> Set.of(OASUpgrade.RF_CAP, OASUpgrade.SHIELD_CAP, OASUpgrade.SHIELD_REC);
            case LEGS -> Set.of(OASUpgrade.RF_CAP, OASUpgrade.SHIELD_CAP, OASUpgrade.SHIELD_REC, OASUpgrade.MOVE_SPEED);
            case FEET -> Set.of(OASUpgrade.RF_CAP, OASUpgrade.SHIELD_CAP, OASUpgrade.SHIELD_REC, OASUpgrade.JUMP_BOOST);
            default -> Set.of();
        };
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, OASUpgrade upgrade) {
        if (this.techLevel == TechLevel.WYVERN) {
            return 2;
        } else if (this.techLevel == TechLevel.DRACONIC) {
            return 3;
        } else if (this.techLevel == TechLevel.CHAOTIC) {
            return 4;
        }
        return 0;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            ClientSetup.openUpgradableArmorConfig(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        Component baseName = super.getName(stack);
        ChatFormatting color;
        if (techLevel == TechLevel.WYVERN) {
            color = ChatFormatting.AQUA;
        } else if (techLevel == TechLevel.DRACONIC) {
            color = ChatFormatting.LIGHT_PURPLE;
        } else if (techLevel == TechLevel.CHAOTIC) {
            color = ChatFormatting.RED;
        } else {
            color = ChatFormatting.WHITE;
        }
        return baseName.copy().withStyle(color);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        this.appendStandardHoverText(stack, tooltip);
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(DummyHumanoidModel.DUMMY_ITEM_RENDER_PROPS);
    }

    @Override
    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String level = this.techLevel.name().toLowerCase(java.util.Locale.ROOT);
        return "draconicarmorrewrite:textures/armor/classic/" + level + "_armor_layer_1.png";
    }

    @OnlyIn(Dist.CLIENT)
    private ModularArmorModel<?> model;

    @OnlyIn(Dist.CLIENT)
    private ModularArmorModel<?> model_on_armor;

    @Override
    @OnlyIn(Dist.CLIENT)
    public EquippedItemModel getExtendedModel(LivingEntity entity, ItemStack stack, @Nullable EquipmentSlot slot, HumanoidModel<?> parentModel, boolean slim) {
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        boolean onArmor = slot == null && !chest.isEmpty() && chest.getItem() instanceof ArmorItem;
        if (model == null || model_on_armor == null) {
            model = new ModularArmorModel<>(techLevel, false, getEquipmentSlot());
            model_on_armor = new ModularArmorModel<>(techLevel, true, getEquipmentSlot());
        }
        ModularArmorModel<?> activeModel = onArmor ? model_on_armor : model;
        ForgeHooksClient.copyModelProperties(parentModel, activeModel);
        return activeModel;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        long max = baseEnergy * com.draconicarmorrewrite.upgrade.OASUpgradeHelper.getEnergyMultiplier(stack);
        long energy = 0;
        if (stack.hasTag() && stack.getTag().contains("Energy")) {
            energy = stack.getTag().getLong("Energy");
        }
        return max > 0L && energy < max;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        long max = baseEnergy * com.draconicarmorrewrite.upgrade.OASUpgradeHelper.getEnergyMultiplier(stack);
        long energy = 0;
        if (stack.hasTag() && stack.getTag().contains("Energy")) {
            energy = stack.getTag().getLong("Energy");
        }
        float charge = (float) energy / (float) max;
        return Math.round(13.0f * charge);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        long max = baseEnergy * com.draconicarmorrewrite.upgrade.OASUpgradeHelper.getEnergyMultiplier(stack);
        long energy = 0;
        if (stack.hasTag() && stack.getTag().contains("Energy")) {
            energy = stack.getTag().getLong("Energy");
        }
        float f = Math.max(0.0f, (float) energy / (float) max);
        return net.minecraft.util.Mth.hsvToRgb((float) (f / 3.0f), (float) 1.0f, (float) 1.0f);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return 0;
    }

    public boolean isShieldEnabled(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_SHIELD_ENABLED, true);
    }

    public int getConfiguredSpeedPercent(ItemStack stack) {
        return net.minecraft.util.Mth.clamp(OASArmorConfig.getInt(stack, OASArmorConfigKey.ARMOR_SPEED_MODIFIER, 0), 0, this.getMaxSpeedModifierPercent(stack));
    }

    public int getConfiguredJumpPercent(ItemStack stack) {
        return net.minecraft.util.Mth.clamp(OASArmorConfig.getInt(stack, OASArmorConfigKey.ARMOR_JUMP_MODIFIER, 0), 0, this.getMaxJumpModifierPercent(stack));
    }

    public boolean hasSprintBoost(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.SPRINT_BOOST, false);
    }

    public boolean hasHillStep(ItemStack stack) {
        return this.getEquipmentSlot() == EquipmentSlot.FEET && OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_HILL_STEP, false);
    }

    public boolean hasAutoFeed(ItemStack stack) {
        return this.getEquipmentSlot() == EquipmentSlot.HEAD && OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_AUTO_FEED, false);
    }

    public boolean hasNightVision(ItemStack stack) {
        return this.getEquipmentSlot() == EquipmentSlot.HEAD && OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_NIGHT_VISION, false);
    }

    public boolean hasNightVisionLock(ItemStack stack) {
        return this.getEquipmentSlot() == EquipmentSlot.HEAD && OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_NIGHT_VISION_LOCK, false);
    }

    public boolean hasFlightLock(ItemStack stack) {
        return this.getEquipmentSlot() == EquipmentSlot.CHEST && OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_FLIGHT_LOCK, false);
    }

    public boolean hasInertiaCancel(ItemStack stack) {
        return this.getEquipmentSlot() == EquipmentSlot.CHEST && OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_INERTIA_CANCEL, false);
    }

    public boolean isArmorHidden(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.HIDE_ARMOR, false);
    }

    public boolean hasFovWarp(ItemStack stack) {
        return OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_SPEED_FOV_WARP, false);
    }

    public int getConfiguredFlightSpeed(ItemStack stack) {
        return net.minecraft.util.Mth.clamp(OASArmorConfig.getInt(stack, OASArmorConfigKey.ARMOR_FLIGHT_SPEED, 0), 0, this.getMaxFlightSpeedModifierPercent(stack));
    }

    public int getConfiguredVerticalFlightSpeed(ItemStack stack) {
        return net.minecraft.util.Mth.clamp(OASArmorConfig.getInt(stack, OASArmorConfigKey.ARMOR_VERTICAL_FLIGHT_SPEED, 100), 0, 200);
    }

    public int getMaxSpeedModifierPercent(ItemStack stack) {
        if (this.getEquipmentSlot() != EquipmentSlot.LEGS) {
            return 0;
        }
        return this.getMaxMoveOrJumpModifierPercent(stack, OASUpgrade.MOVE_SPEED);
    }

    public int getMaxJumpModifierPercent(ItemStack stack) {
        if (this.getEquipmentSlot() != EquipmentSlot.FEET) {
            return 0;
        }
        return this.getMaxMoveOrJumpModifierPercent(stack, OASUpgrade.JUMP_BOOST);
    }

    public int getMaxFlightSpeedModifierPercent(ItemStack stack) {
        return this.getEquipmentSlot() == EquipmentSlot.CHEST ? 600 : 0;
    }

    private int getMaxMoveOrJumpModifierPercent(ItemStack stack, OASUpgrade upgrade) {
        int level = OASUpgradeHelper.getUpgradeLevel(stack, upgrade);
        if (this.isDraconic()) {
            return 200 + 100 * level + Math.max(level - 1, 0) * 100 + Math.max(level - 2, 0) * 100;
        }
        return 100 + 100 * level + Math.max(level - 1, 0) * 50;
    }

    private static void sendConfigMessage(ServerPlayer player, String label, String value) {
        // Notifications disabled
    }

    private static int cycleBoundedPercent(int current, int maxPercent) {
        if (maxPercent <= 0) {
            return 0;
        }
        int next = current + 25;
        return next > maxPercent ? 0 : next;
    }

    private static int cyclePercent(int current, int ... values) {
        for (int i = 0; i < values.length; ++i) {
            if (values[i] != current) continue;
            return values[(i + 1) % values.length];
        }
        return values[0];
    }

    public boolean applyConfigValue(ServerPlayer player, ItemStack stack, ArmorConfigAction action, double value) {
        OASArmorConfigKey key = null;
        switch (action) {
            case CYCLE_MOVE_SPEED: {
                if (this.getEquipmentSlot() == EquipmentSlot.LEGS) {
                    key = OASArmorConfigKey.ARMOR_SPEED_MODIFIER;
                }
                break;
            }
            case CYCLE_JUMP_BOOST: {
                if (this.getEquipmentSlot() == EquipmentSlot.FEET) {
                    key = OASArmorConfigKey.ARMOR_JUMP_MODIFIER;
                }
                break;
            }
            case CYCLE_FLIGHT_SPEED: {
                if (this.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    key = OASArmorConfigKey.ARMOR_FLIGHT_SPEED;
                }
                break;
            }
            case CYCLE_VERTICAL_FLIGHT_SPEED: {
                if (this.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    key = OASArmorConfigKey.ARMOR_VERTICAL_FLIGHT_SPEED;
                }
                break;
            }
            default:
                break;
        }
        if (key != null) {
            int max = switch (action) {
                case CYCLE_MOVE_SPEED -> this.getMaxSpeedModifierPercent(stack);
                case CYCLE_JUMP_BOOST -> this.getMaxJumpModifierPercent(stack);
                case CYCLE_FLIGHT_SPEED -> this.getMaxFlightSpeedModifierPercent(stack);
                case CYCLE_VERTICAL_FLIGHT_SPEED -> 0;
                default -> 100;
            };
            int min = 0;
            if (max >= min) {
                int intValue = (int)Math.round((double)min + value * (double)(max - min));
                OASArmorConfig.setInt(stack, key, intValue);
                return true;
            }
        }
        return false;
    }

    public boolean applyConfigAction(ServerPlayer player, ItemStack stack, ArmorConfigAction action) {
        boolean changed = false;
        switch (action) {
            case TOGGLE_NIGHT_VISION: {
                if (this.getEquipmentSlot() == EquipmentSlot.HEAD) {
                    changed = this.toggleBoolean(player, stack, OASArmorConfigKey.ARMOR_NIGHT_VISION, this.hasNightVision(stack), "Night Vision");
                }
                break;
            }
            case TOGGLE_NIGHT_VISION_LOCK: {
                if (this.getEquipmentSlot() == EquipmentSlot.HEAD) {
                    changed = this.toggleBoolean(player, stack, OASArmorConfigKey.ARMOR_NIGHT_VISION_LOCK, this.hasNightVisionLock(stack), "Night Vision Lock");
                }
                break;
            }
            case TOGGLE_AUTO_FEED: {
                if (this.getEquipmentSlot() == EquipmentSlot.HEAD) {
                    changed = this.toggleBoolean(player, stack, OASArmorConfigKey.ARMOR_AUTO_FEED, this.hasAutoFeed(stack), "Auto Feed");
                }
                break;
            }
            case TOGGLE_HIDE_ARMOR: {
                changed = this.toggleBoolean(player, stack, OASArmorConfigKey.HIDE_ARMOR, this.isArmorHidden(stack), "Hide Armor");
                break;
            }
            case TOGGLE_INERTIA_CANCEL: {
                if (this.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    changed = this.toggleBoolean(player, stack, OASArmorConfigKey.ARMOR_INERTIA_CANCEL, this.hasInertiaCancel(stack), "Inertia Cancel");
                }
                break;
            }
            case TOGGLE_FOV_WARP: {
                if (this.getEquipmentSlot() == EquipmentSlot.LEGS) {
                    changed = this.toggleBoolean(player, stack, OASArmorConfigKey.ARMOR_SPEED_FOV_WARP, this.hasFovWarp(stack), "FOV Warp");
                }
                break;
            }
            case TOGGLE_HILL_STEP: {
                if (this.getEquipmentSlot() == EquipmentSlot.FEET) {
                    changed = this.toggleBoolean(player, stack, OASArmorConfigKey.ARMOR_HILL_STEP, this.hasHillStep(stack), "Auto Step");
                }
                break;
            }
            case CYCLE_MOVE_SPEED: {
                int maxPercent = this.getMaxSpeedModifierPercent(stack);
                changed = this.cycleInt(player, stack, OASArmorConfigKey.ARMOR_SPEED_MODIFIER, this.getConfiguredSpeedPercent(stack), maxPercent, "Move Speed");
                break;
            }
            case CYCLE_JUMP_BOOST: {
                int maxPercent = this.getMaxJumpModifierPercent(stack);
                changed = this.cycleInt(player, stack, OASArmorConfigKey.ARMOR_JUMP_MODIFIER, this.getConfiguredJumpPercent(stack), maxPercent, "Jump Boost");
                break;
            }
            case TOGGLE_SPRINT_BOOST: {
                changed = this.toggleBoolean(player, stack, OASArmorConfigKey.SPRINT_BOOST, this.hasSprintBoost(stack), "Sprint Boost");
                break;
            }
            case TOGGLE_FLIGHT_LOCK: {
                if (this.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    changed = this.toggleBoolean(player, stack, OASArmorConfigKey.ARMOR_FLIGHT_LOCK, this.hasFlightLock(stack), "Flight Lock");
                }
                break;
            }
            case CYCLE_FLIGHT_SPEED: {
                if (this.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    changed = this.cycleExplicit(player, stack, OASArmorConfigKey.ARMOR_FLIGHT_SPEED, this.getConfiguredFlightSpeed(stack), "Flight Speed", 0, 100, 200, 300, 400, 500, 600);
                }
                break;
            }
            case CYCLE_VERTICAL_FLIGHT_SPEED: {
                if (this.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    changed = this.cycleExplicit(player, stack, OASArmorConfigKey.ARMOR_VERTICAL_FLIGHT_SPEED, this.getConfiguredVerticalFlightSpeed(stack), "Vertical Flight Speed", 50, 100, 150, 200);
                }
                break;
            }
            default:
                break;
        }
        if (changed) {
            player.level().playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 0.4f, 1.1f);
        }
        return changed;
    }

    private boolean toggleBoolean(ServerPlayer player, ItemStack stack, OASArmorConfigKey key, boolean current, String label) {
        boolean next = !current;
        OASArmorConfig.setBoolean(stack, key, next);
        ClassicUpgradeableArmorItem.sendConfigMessage(player, label, next ? "On" : "Off");
        return true;
    }

    private boolean cycleInt(ServerPlayer player, ItemStack stack, OASArmorConfigKey key, int current, int maxPercent, String label) {
        if (maxPercent <= 0) {
            return false;
        }
        int next = ClassicUpgradeableArmorItem.cycleBoundedPercent(current, maxPercent);
        OASArmorConfig.setInt(stack, key, next);
        ClassicUpgradeableArmorItem.sendConfigMessage(player, label, next + "%");
        return true;
    }

    private boolean cycleExplicit(ServerPlayer player, ItemStack stack, OASArmorConfigKey key, int current, String label, int ... values) {
        int next = ClassicUpgradeableArmorItem.cyclePercent(current, values);
        OASArmorConfig.setInt(stack, key, next);
        ClassicUpgradeableArmorItem.sendConfigMessage(player, label, next + "%");
        return true;
    }
}
