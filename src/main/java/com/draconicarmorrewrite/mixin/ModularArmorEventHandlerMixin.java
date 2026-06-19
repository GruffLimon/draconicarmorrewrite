package com.draconicarmorrewrite.mixin;

import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.api.modules.entities.UndyingEntity;
import com.brandon3055.draconicevolution.handlers.ModularArmorEventHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ModularArmorEventHandler.class, remap = false)
public class ModularArmorEventHandlerMixin {

    @Inject(method = "onEntityAttacked", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onEntityAttacked(LivingAttackEvent event, CallbackInfo ci) {
        LivingEntity entity = event.getEntity();
        if (event.isCanceled() || event.getAmount() <= 0 || entity.level().isClientSide || event.getSource().is(com.brandon3055.draconicevolution.init.DEDamage.KILL)) {
            return;
        }

        //Allows /kill to completely bypass all protections
        if (event.getAmount() == Float.MAX_VALUE && event.getSource().is(net.minecraft.world.damagesource.DamageTypes.FELL_OUT_OF_WORLD)) {
            event.setCanceled(true);
            entity.hurt(com.brandon3055.draconicevolution.init.DEDamage.killDamage(entity.level()), Float.MAX_VALUE / 5);
            ci.cancel();
            return;
        }

        List<com.brandon3055.draconicevolution.api.capability.ModuleHost> hosts = com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedHosts(entity).toList();
        
        // Try undying modules first
        for (com.brandon3055.draconicevolution.api.capability.ModuleHost host : hosts) {
            if (host.getEntitiesByType(ModuleTypes.UNDYING).anyMatch(module -> ((UndyingEntity) module).tryBlockDamage(event))) {
                ci.cancel();
                return;
            }
        }

        // Try shield controllers
        for (com.brandon3055.draconicevolution.api.capability.ModuleHost host : hosts) {
            ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
            if (shieldControl != null) {
                shieldControl.tryBlockDamage(event);
                if (event.isCanceled()) {
                    ci.cancel();
                    return;
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "onEntityDamaged", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onEntityDamaged(LivingDamageEvent event, CallbackInfo ci) {
        LivingEntity entity = event.getEntity();
        if (event.isCanceled() || event.getAmount() <= 0 || entity.level().isClientSide || event.getSource().is(com.brandon3055.draconicevolution.init.DEDamage.KILL)) {
            return;
        }

        List<com.brandon3055.draconicevolution.api.capability.ModuleHost> hosts = com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedHosts(entity).toList();

        // Try undying modules first
        for (com.brandon3055.draconicevolution.api.capability.ModuleHost host : hosts) {
            if (host.getEntitiesByType(ModuleTypes.UNDYING).anyMatch(module -> ((UndyingEntity) module).tryBlockDamage(event))) {
                ci.cancel();
                return;
            }
        }

        // Try shield controllers
        for (com.brandon3055.draconicevolution.api.capability.ModuleHost host : hosts) {
            ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
            if (shieldControl != null) {
                shieldControl.tryBlockDamage(event);
                if (event.isCanceled() || event.getAmount() <= 0) {
                    ci.cancel();
                    return;
                }
            }
        }
        ci.cancel();
    }

    @Inject(method = "getJumpBoost", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onGetJumpBoost(LivingEntity entity, boolean max, CallbackInfoReturnable<Float> cir) {
        double combinedJump = 0;
        boolean hasJump = false;
        for (com.brandon3055.draconicevolution.api.capability.ModuleHost host : com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedHosts(entity).toList()) {
            com.brandon3055.draconicevolution.api.modules.data.JumpData jumpData = host.getModuleData(ModuleTypes.JUMP_BOOST);
            if (jumpData != null) {
                hasJump = true;
                double jump = jumpData.multiplier();
                if (!max) {
                    if (entity.isSprinting()) {
                        if (host instanceof com.brandon3055.draconicevolution.api.capability.PropertyProvider && ((com.brandon3055.draconicevolution.api.capability.PropertyProvider) host).hasDecimal("jump_boost_run")) {
                            jump = Math.min(jump, ((com.brandon3055.draconicevolution.api.capability.PropertyProvider) host).getDecimal("jump_boost_run").getValue());
                        }
                    } else {
                        if (host instanceof com.brandon3055.draconicevolution.api.capability.PropertyProvider && ((com.brandon3055.draconicevolution.api.capability.PropertyProvider) host).hasDecimal("jump_boost")) {
                            jump = Math.min(jump, ((com.brandon3055.draconicevolution.api.capability.PropertyProvider) host).getDecimal("jump_boost").getValue());
                        }
                    }
                }
                combinedJump += jump;
            }
        }
        cir.setReturnValue(hasJump ? (float) combinedJump : 0f);
    }

    @Inject(method = "getStepHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onGetStepHeight(LivingEntity entity, com.brandon3055.draconicevolution.handlers.ModularArmorEventHandler.ArmorAbilities abilities, CallbackInfoReturnable<net.minecraft.world.entity.ai.attributes.AttributeModifier> cir) {
        boolean hasHighStep = false;
        for (com.brandon3055.draconicevolution.api.capability.ModuleHost host : com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedHosts(entity).toList()) {
            if (host.getEntitiesByType(ModuleTypes.HILL_STEP).findAny().isPresent()) {
                hasHighStep = true;
                break;
            }
        }
        hasHighStep = hasHighStep && !entity.isShiftKeyDown();

        net.minecraft.world.entity.ai.attributes.AttributeInstance instance = entity.getAttribute(net.minecraftforge.common.ForgeMod.STEP_HEIGHT_ADDITION.get());

        if (hasHighStep && instance != null) {
            double modifier = instance.getValue();
            net.minecraft.world.entity.ai.attributes.AttributeModifier ourMod = instance.getModifier(ModularArmorEventHandler.STEP_HEIGHT_UUID);
            if (modifier > 1 && ourMod == null) {
                cir.setReturnValue(null);
                return;
            }
            if (ourMod != null) {
                modifier -= ourMod.getAmount();
            }
            modifier = 1.1625D - modifier;
            if (modifier > 0) {
                cir.setReturnValue(new net.minecraft.world.entity.ai.attributes.AttributeModifier(ModularArmorEventHandler.STEP_HEIGHT_UUID, net.minecraftforge.common.ForgeMod.STEP_HEIGHT_ADDITION.get().getDescriptionId(), modifier, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION));
                return;
            }
        }
        cir.setReturnValue(null);
    }

    @Inject(method = "breakSpeed", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onBreakSpeed(PlayerEvent.BreakSpeed event, CallbackInfo ci) {
        Player player = event.getEntity();
        if (player == null) return;

        float newDigSpeed = event.getOriginalSpeed();

        boolean hasAquaAdapt = false;
        boolean hasMiningStability = false;
        for (com.brandon3055.draconicevolution.api.capability.ModuleHost host : com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedHosts(player).toList()) {
            if (host.getModuleData(ModuleTypes.AQUA_ADAPT) != null) {
                hasAquaAdapt = true;
            }
            if (host.getModuleData(ModuleTypes.MINING_STABILITY) != null) {
                hasMiningStability = true;
            }
        }

        if (hasAquaAdapt) {
            if (player.isEyeInFluid(net.minecraft.tags.FluidTags.WATER) && !net.minecraft.world.item.enchantment.EnchantmentHelper.hasAquaAffinity(player)) {
                newDigSpeed *= 5f;
            }
        }

        if (!player.onGround() && hasMiningStability) {
            newDigSpeed *= 5f;
        }

        if (newDigSpeed != event.getOriginalSpeed()) {
            event.setNewSpeed(newDigSpeed);
        }
        ci.cancel();
    }

    @Inject(method = "onPlayerLogin", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        Player player = event.getEntity();
        if (player.onGround()) return;

        for (com.brandon3055.draconicevolution.api.capability.ModuleHost host : com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedHosts(player).toList()) {
            com.brandon3055.draconicevolution.api.modules.data.FlightData flightData = host.getModuleData(ModuleTypes.FLIGHT);
            if (flightData != null && flightData.creative()) {
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                break;
            }
        }
        ci.cancel();
    }
}
