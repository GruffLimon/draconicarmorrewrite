package com.draconicarmorrewrite;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class UpgradableArmorEventHandler {
    public static final UUID UPGRADABLE_SPEED_UUID = UUID.fromString("11a7ce8e-d2e8-11e5-ab30-625662870761");
    private static final Set<UUID> SHIELD_REAPPLYING_DAMAGE = new HashSet<>();
    private static final Set<UUID> SHIELD_REGEN_PLAYERS = new HashSet<>();
    private static final Map<UUID, Integer> SHIELD_HIT_COOLDOWNS = new HashMap<>();

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new UpgradableArmorEventHandler());
    }

    private static boolean isArmorPowered(Player player, ClassicUpgradeableArmorItem armor, ItemStack stack) {
        return player.getAbilities().instabuild || getStoredEnergy(stack) > 0L;
    }

    private static long getStoredEnergy(ItemStack stack) {
        return stack.getCapability(DECapabilities.OP_STORAGE).map(com.brandon3055.brandonscore.api.power.IOPStorage::getOPStored).orElse(0L);
    }

    private static void consumeEnergy(ItemStack stack, long amount) {
        stack.getCapability(DECapabilities.OP_STORAGE).ifPresent(storage -> storage.modifyEnergyStored(-amount));
    }

    private record ShieldArmorStack(ItemStack stack, ClassicUpgradeableArmorItem armor, double shieldPoints, long energyStored) {}

    private static List<ShieldArmorStack> getClassicArmorStacks(Player player) {
        List<ShieldArmorStack> list = new ArrayList<>();
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ClassicUpgradeableArmorItem armor) {
                if (isArmorPowered(player, armor, stack)) {
                    list.add(new ShieldArmorStack(stack, armor, armor.getShieldPoints(stack), getStoredEnergy(stack)));
                }
            }
        }
        return list;
    }

    private static double getMeanShieldEntropy(List<ShieldArmorStack> armorStacks) {
        if (armorStacks.isEmpty()) return 0.0;
        double sum = 0.0;
        for (ShieldArmorStack stack : armorStacks) {
            sum += stack.armor.getShieldEntropy(stack.stack);
        }
        return sum / armorStacks.size();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player) || player.level().isClientSide) {
            return;
        }

        if (event.isCanceled() || event.getAmount() <= 0.0f || SHIELD_REAPPLYING_DAMAGE.contains(player.getUUID())) {
            return;
        }

        List<ShieldArmorStack> armorStacks = getClassicArmorStacks(player);
        if (armorStacks.isEmpty()) {
            return;
        }

        // Cancel fire/lava/magma damage if we have active shields
        if (event.getSource().is(net.minecraft.world.damagesource.DamageTypes.IN_FIRE) ||
            event.getSource().is(net.minecraft.world.damagesource.DamageTypes.ON_FIRE) ||
            event.getSource().is(net.minecraft.world.damagesource.DamageTypes.LAVA) ||
            event.getSource().is(net.minecraft.world.damagesource.DamageTypes.HOT_FLOOR)) {
            event.setCanceled(true);
            player.clearFire();
            return;
        }

        // Shield absorption logic
        double totalShield = 0.0;
        for (ShieldArmorStack stack : armorStacks) {
            totalShield += stack.shieldPoints;
        }

        if (totalShield <= 0.0) {
            return;
        }

        float remainingDamage = event.getAmount();
        double entropy = Math.min(100.0, getMeanShieldEntropy(armorStacks) + 1.0 + (double)remainingDamage / 20.0);
        double absorbedTotal = 0.0;

        for (ShieldArmorStack armorStack : armorStacks) {
            double share = armorStack.shieldPoints / totalShield;
            double requestedAbsorb = Math.min((double)remainingDamage * share, armorStack.shieldPoints);
            double absorbed = armorStack.armor.absorbShieldPoints(armorStack.stack, requestedAbsorb, false);
            if (absorbed > 0.0) {
                armorStack.armor.setShieldEntropy(armorStack.stack, entropy);
                absorbedTotal += absorbed;
            }
        }

        float remaining = Math.max(0.0f, remainingDamage - (float)absorbedTotal);

        if (absorbedTotal > 0.0) {
            // Calculate remaining shield and total capacity
            double remainingShield = 0.0;
            double maxShield = 0.0;
            for (ShieldArmorStack armorStack : armorStacks) {
                remainingShield += armorStack.armor.getShieldPoints(armorStack.stack);
                maxShield += armorStack.armor.getShieldCapacity(armorStack.stack);
            }
            float shieldPower = maxShield > 0.0 ? (float) (remainingShield / maxShield) : 1.0f;

            // Play shield hit effects
            float pitch = player.level().random.nextFloat() * 0.1f + 0.5f + 0.3f * shieldPower;
            net.minecraft.sounds.SoundEvent shieldSound = com.draconicarmorrewrite.DraconicArmorRewrite.SHIELD_STRIKE.get();
            player.level().playSound(null, player.getX(), player.getY() + 1.0, player.getZ(),
                    shieldSound, SoundSource.PLAYERS, 0.9f, pitch);

            // Add particles
            if (player instanceof ServerPlayer sp) {
                sp.serverLevel().sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 5, 0.2, 0.2, 0.2, 0.0);
                
                // Broadcast packet to tracking players and self
                com.draconicarmorrewrite.network.PacketHandler.CHANNEL.send(
                    net.minecraftforge.network.PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sp),
                    new com.draconicarmorrewrite.network.PacketShieldHit(sp.getId(), shieldPower)
                );
            }
        }

        if (remaining <= 0.0f) {
            event.setCanceled(true);
        } else {
            event.setCanceled(true);
            SHIELD_REAPPLYING_DAMAGE.add(player.getUUID());
            try {
                player.hurt(event.getSource(), remaining);
            } finally {
                SHIELD_REAPPLYING_DAMAGE.remove(player.getUUID());
            }
        }
    }

    @SubscribeEvent
    public void onLivingKnockBack(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            List<ShieldArmorStack> stacks = getClassicArmorStacks(player);
            double totalShield = 0.0;
            for (ShieldArmorStack stack : stacks) {
                totalShield += stack.shieldPoints;
            }
            if (totalShield > 0.0) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        List<ShieldArmorStack> armorStacks = getClassicArmorStacks(player);
        if (armorStacks.isEmpty()) {
            return;
        }

        double totalShield = 0.0;
        long totalEnergy = 0L;
        boolean hasDraconic = false;

        for (ShieldArmorStack armorStack : armorStacks) {
            totalShield += armorStack.shieldPoints;
            totalEnergy += armorStack.energyStored;
            if (armorStack.armor.getTechLevel() == TechLevel.DRACONIC || armorStack.armor.getTechLevel() == TechLevel.CHAOTIC) {
                hasDraconic = true;
            }
        }

        if (totalShield > 0.0) {
            event.setCanceled(true);
            player.setHealth(10.0f);
            return;
        }

        if (!hasDraconic || totalEnergy < 10000000L) {
            return;
        }

        // Consume 10,000,000 RF/FE to save the player
        long remainingCost = 10000000L;
        long remainingEnergy = totalEnergy;
        for (ShieldArmorStack armorStack : armorStacks) {
            if (remainingCost <= 0L || remainingEnergy <= 0L) {
                break;
            }
            if (armorStack.energyStored <= 0L) continue;
            long energyCost = remainingEnergy == armorStack.energyStored ? remainingCost : Math.round((double)remainingCost * ((double)armorStack.energyStored / (double)remainingEnergy));
            energyCost = Math.min(energyCost, armorStack.energyStored);
            if (energyCost > 0L) {
                consumeEnergy(armorStack.stack, energyCost);
                remainingCost -= energyCost;
            }
            remainingEnergy -= armorStack.energyStored;
        }

        event.setCanceled(true);
        player.sendSystemMessage(Component.translatable("msg.originalarmorstuff.shield_depleted").withStyle(ChatFormatting.RED));
        player.setHealth(1.0f);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player) || player.level().isClientSide) {
            return;
        }

        MobEffectInstance effect = event.getEffectInstance();
        if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            // Check if full active Draconic set is worn
            boolean fullSet = true;
            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                ItemStack stack = player.getItemBySlot(slot);
                if (stack.isEmpty() || !(stack.getItem() instanceof ClassicUpgradeableArmorItem armor) ||
                    (armor.getTechLevel() != TechLevel.DRACONIC && armor.getTechLevel() != TechLevel.CHAOTIC) ||
                    !isArmorPowered(player, armor, stack)) {
                    fullSet = false;
                    break;
                }
            }
            if (fullSet) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        // Night vision, water breathing, auto-feed, speed, jump, flight
        ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legsStack = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack bootsStack = player.getItemBySlot(EquipmentSlot.FEET);

        // --- Server Only Logic ---
        if (player instanceof ServerPlayer player2) {
            // Ticking shield recovery
            tickShieldRecovery(player2);

            // 1. Head effects
            if (!headStack.isEmpty() && headStack.getItem() instanceof ClassicUpgradeableArmorItem helmet) {
                boolean powered = isArmorPowered(player2, helmet, headStack);
                
                if (powered && helmet.hasNightVision(headStack)) {
                    player2.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, true, false, false));
                }

                if (powered) {
                    if (player2.getAirSupply() < player2.getMaxAirSupply()) {
                        player2.setAirSupply(player2.getMaxAirSupply());
                    }

                    // Auto-feed
                    if (helmet.hasAutoFeed(headStack) && player2.getFoodData().needsFood() && player2.tickCount % 20 == 0) {
                        for (int i = 0; i < player2.getInventory().items.size(); i++) {
                            ItemStack foodStack = player2.getInventory().items.get(i);
                            if (!foodStack.isEmpty() && foodStack.getItem().isEdible()) {
                                FoodProperties food = foodStack.getItem().getFoodProperties(foodStack, player2);
                                if (food != null) {
                                    player2.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());
                                    if (!player2.getAbilities().instabuild) {
                                        foodStack.shrink(1);
                                        consumeEnergy(headStack, 500L);
                                    }
                                    player2.level().playSound(null, player2.getX(), player2.getY(), player2.getZ(),
                                            SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5f, 1.0f);
                                    player2.getInventory().setChanged();
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // 2. Leg effects (Speed)
            AttributeInstance speedAttr = player2.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttr != null) {
                speedAttr.removeModifier(UPGRADABLE_SPEED_UUID);
                if (!legsStack.isEmpty() && legsStack.getItem() instanceof ClassicUpgradeableArmorItem legs) {
                    if (isArmorPowered(player2, legs, legsStack)) {
                        int speedPercent = legs.getConfiguredSpeedPercent(legsStack);
                        
                        if (speedPercent > 0) {
                            double boost = speedPercent / 100.0D;
                            speedAttr.addTransientModifier(new AttributeModifier(UPGRADABLE_SPEED_UUID, "Classic Speed Upgrade", boost, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        }
                    }
                }
            }

            // 3. Boot effects (Jump and Step Assist)
            if (!bootsStack.isEmpty() && bootsStack.getItem() instanceof ClassicUpgradeableArmorItem boots) {
                boolean powered = isArmorPowered(player2, boots, bootsStack);
                int jumpPercent = boots.getConfiguredJumpPercent(bootsStack);

                if (powered && jumpPercent > 0) {
                    int amplifier = Math.max(0, jumpPercent / 50);
                    player2.addEffect(new MobEffectInstance(MobEffects.JUMP, 10, amplifier, true, false, false));
                }

                // Step assist
                if (powered && boots.hasHillStep(bootsStack)) {
                    player2.setMaxUpStep(1.0f);
                } else {
                    player2.setMaxUpStep(0.6f);
                }
            } else {
                player2.setMaxUpStep(0.6f);
            }
        }

        // --- Client + Server Logic (Flight, speed lock, inertia cancellation) ---
        boolean hasFlight = false;
        ClassicUpgradeableArmorItem chest = null;
        if (!chestStack.isEmpty() && chestStack.getItem() instanceof ClassicUpgradeableArmorItem c) {
            chest = c;
            
            if (isArmorPowered(player, chest, chestStack)) {
                hasFlight = true;
            }
        }

        if (hasFlight) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
            if (player.getAbilities().flying) {
                if (!player.level().isClientSide && !player.getAbilities().instabuild) {
                    // Drain 10 RF per tick
                    consumeEnergy(chestStack, 10L);
                }

                // Flight Speed & Sprint Boost
                float speedFactor = 1.0f + (chest.getConfiguredFlightSpeed(chestStack) / 100.0f);
                if (chest.hasSprintBoost(chestStack) && player.isSprinting()) {
                    speedFactor *= 2.0f;
                }
                float targetSpeed = 0.05f * speedFactor;
                if (Math.abs(player.getAbilities().getFlyingSpeed() - targetSpeed) > 0.0001f) {
                    player.getAbilities().setFlyingSpeed(targetSpeed);
                    player.onUpdateAbilities();
                }

                // Inertia Cancellation
                if (chest.hasInertiaCancel(chestStack)) {
                    boolean hasNoInput = player.xxa == 0.0f && player.zza == 0.0f && !((com.draconicarmorrewrite.mixin.LivingEntityAccessor) player).isJumping() && !player.isCrouching();
                    if (hasNoInput) {
                        player.setDeltaMovement(Vec3.ZERO);
                    }
                }
            } else if (chest.hasFlightLock(chestStack) && !player.isCrouching() && player.onGround() && !player.getAbilities().flying) {
                // Flight Lock: force flight to true if touching ground
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            }
        } else {
            if (!player.getAbilities().instabuild && player.getAbilities().mayfly) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
            if (player.getAbilities().getFlyingSpeed() != 0.05f) {
                player.getAbilities().setFlyingSpeed(0.05f);
                player.onUpdateAbilities();
            }
        }
    }

    private static void tickShieldRecovery(Player player) {
        if (!SHIELD_REGEN_PLAYERS.add(player.getUUID())) {
            return;
        }
        try {
            boolean creative = player.getAbilities().instabuild;
            List<ShieldArmorStack> armorStacks = getClassicArmorStacks(player);
            double totalCapacity = 0.0;
            double totalPoints = 0.0;
            double totalEnergy = 0.0;
            double meanRecovery = 0.0;
            double meanEntropy = 0.0;

            for (ShieldArmorStack stack : armorStacks) {
                double capacity = stack.armor.getShieldCapacity(stack.stack);
                double points = stack.armor.getShieldPoints(stack.stack);
                if (capacity > 0.0) {
                    totalCapacity += capacity;
                    totalPoints += points;
                    totalEnergy += stack.energyStored;
                    meanRecovery += stack.armor.getShieldRecovery(stack.stack);
                    meanEntropy += stack.armor.getShieldEntropy(stack.stack);
                }
            }

            if (armorStacks.isEmpty()) {
                return;
            }

            meanRecovery /= armorStacks.size();
            meanEntropy /= armorStacks.size();

            double missingShield = totalCapacity - totalPoints;
            if (missingShield <= 0.01 && meanEntropy <= 0.0) {
                return;
            }

            double toAdd = Math.min(missingShield, totalCapacity / 60.0);
            toAdd *= Math.max(0.0, 1.0 - meanEntropy / 100.0);
            double recoveredEntropy = Math.max(0.0, meanEntropy - meanRecovery / 100.0);

            if (!creative) {
                toAdd = Math.min(toAdd, totalEnergy / 1000.0);
            }

            if (toAdd < 0.0) {
                toAdd = 0.0;
            }

            double pointsDownTotal = Math.max(1.0, missingShield);
            long totalEnergyCost = Math.round(toAdd * 1000.0);

            if (!creative && totalEnergy > 0.0 && totalEnergyCost > 0L) {
                for (ShieldArmorStack armorStack : armorStacks) {
                    long energyCost = Math.round((double)totalEnergyCost * ((double)armorStack.energyStored / totalEnergy));
                    if (energyCost > 0L) {
                        consumeEnergy(armorStack.stack, energyCost);
                    }
                }
            }

            for (ShieldArmorStack armorStack : armorStacks) {
                double points = armorStack.armor.getShieldPoints(armorStack.stack);
                double capacity = armorStack.armor.getShieldCapacity(armorStack.stack);
                double missing = capacity - points;
                if (missing > 0.0 && toAdd > 0.0) {
                    double slotAdd = Math.min(missing, toAdd * (missing / pointsDownTotal));
                    armorStack.armor.setShieldPoints(armorStack.stack, points + slotAdd);
                }
                if (player.invulnerableTime <= 0) {
                    armorStack.armor.setShieldEntropy(armorStack.stack, recoveredEntropy);
                }
            }
        } finally {
            SHIELD_REGEN_PLAYERS.remove(player.getUUID());
        }
    }
}
