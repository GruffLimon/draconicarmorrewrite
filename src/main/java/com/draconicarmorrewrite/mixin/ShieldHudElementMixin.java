package com.draconicarmorrewrite.mixin;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.entities.ShieldControlEntity;
import com.brandon3055.draconicevolution.api.modules.entities.UndyingEntity;
import com.brandon3055.draconicevolution.client.render.hud.ShieldHudElement;
import com.brandon3055.draconicevolution.items.tools.DraconiumCapacitor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(value = ShieldHudElement.class, remap = false)
public abstract class ShieldHudElementMixin extends com.brandon3055.brandonscore.api.hud.AbstractHudElement {

    @Shadow
    private Minecraft mc;
    @Shadow
    private double shieldCharge;
    @Shadow
    private String shieldText;
    @Shadow
    private double coolDown;
    @Shadow
    private double energyBar;
    @Shadow
    private String energyText;
    @Shadow
    private double[] totemStatus;
    @Shadow
    private int lastTotemCount;
    @Shadow
    private int lastChargedTotemCount;
    @Shadow
    private boolean renderHud;
    @Shadow
    private boolean numericEnergy;
    @Shadow
    private boolean showUndying;
    @Shadow
    private int energyMode;

    public ShieldHudElementMixin() {
        super(null);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void onTick(boolean configuring, CallbackInfo ci) {
        if (this.mc.player == null || !this.enabled) {
            this.renderHud = false;
            if (configuring) {
                this.renderHud = this.enabled;
                this.shieldCharge = 1624 / 3055D;
                this.shieldText = "1624/3055";
                this.energyBar = 0.75;
                this.energyText = net.minecraft.client.resources.language.I18n.get("op.brandonscore.op") + ": 42M";
                this.totemStatus = this.showUndying ? new double[]{-1, 0.5, 0.75} : new double[0];
            }
            ci.cancel();
            return;
        }

        // 1. Gather all equipped modular items
        List<ItemStack> modularItems = com.brandon3055.draconicevolution.api.modules.ModuleHelper.getEquippedHostItems(this.mc.player);
        if (modularItems.isEmpty()) {
            this.renderHud = false;
            if (configuring) {
                this.renderHud = this.enabled;
                this.shieldCharge = 1624 / 3055D;
                this.shieldText = "1624/3055";
                this.energyBar = 0.75;
                this.energyText = net.minecraft.client.resources.language.I18n.get("op.brandonscore.op") + ": 42M";
                this.totemStatus = this.showUndying ? new double[]{-1, 0.5, 0.75} : new double[0];
            }
            ci.cancel();
            return;
        }

        // 2. Sum up Shield points, capacity, and cooldowns
        double totalCapacity = 0;
        double totalPoints = 0;
        double maxCooldown = 0;
        double currentCooldown = 0;
        boolean hasShield = false;
        boolean shieldEnabledVal = false;

        for (ItemStack stack : modularItems) {
            com.brandon3055.draconicevolution.api.capability.ModuleHost host = stack.getCapability(com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY).orElse(null);
            if (host != null) {
                ShieldControlEntity shieldControl = host.getEntitiesByType(ModuleTypes.SHIELD_CONTROLLER).map(e -> (ShieldControlEntity) e).findAny().orElse(null);
                if (shieldControl != null) {
                    hasShield = true;
                    if (shieldControl.isShieldEnabled()) {
                        shieldEnabledVal = true;
                        double capacity = shieldControl.getShieldCapacity() + shieldControl.getMaxShieldBoost();
                        if (capacity == 0 && shieldControl.getMaxShieldBoost() > 0) {
                            capacity = shieldControl.getMaxShieldBoost();
                        }
                        totalCapacity += capacity;
                        totalPoints += shieldControl.getShieldPoints();
                        currentCooldown = Math.max(currentCooldown, shieldControl.getShieldCoolDown());
                        maxCooldown = Math.max(maxCooldown, shieldControl.getMaxShieldCoolDown());
                    }
                }
            }
        }

        if (!hasShield) {
            this.shieldCharge = 0;
            this.shieldText = net.minecraft.client.resources.language.I18n.get("hud_armor.draconicevolution.no_shield");
        } else if (!shieldEnabledVal) {
            this.shieldCharge = 0;
            this.shieldText = net.minecraft.client.resources.language.I18n.get("hud_armor.draconicevolution.shield_disabled");
        } else {
            this.shieldCharge = totalCapacity > 0 ? totalPoints / totalCapacity : 0;
            this.shieldText = (int) totalPoints + "/" + (int) totalCapacity;
            this.coolDown = maxCooldown > 0 ? currentCooldown / maxCooldown : 0;
        }

        // 3. Sum up Energy
        long totalEnergy = 0;
        long totalMaxEnergy = 0;

        if (this.energyMode == 1 || this.energyMode == 2) {
            List<ItemStack> capacitors = new ArrayList<>(com.brandon3055.draconicevolution.integration.equipment.EquipmentManager.findItems(e -> e.getItem() instanceof DraconiumCapacitor, this.mc.player));
            for (ItemStack stack : this.mc.player.getInventory().items) {
                if (stack.getItem() instanceof DraconiumCapacitor) {
                    capacitors.add(stack);
                }
            }
            long capMax = 0;
            long capEnergy = 0;
            for (ItemStack stack : capacitors) {
                LazyOptional<IOPStorage> optCap = stack.getCapability(com.brandon3055.brandonscore.capability.CapabilityOP.OP);
                if (optCap.isPresent()) {
                    IOPStorage storage = optCap.orElseThrow(com.brandon3055.draconicevolution.lib.WTFException::new);
                    capMax = com.brandon3055.brandonscore.utils.Utils.safeAdd(storage.getMaxOPStored(), capMax);
                    capEnergy = com.brandon3055.brandonscore.utils.Utils.safeAdd(storage.getOPStored(), capEnergy);
                }
            }

            if (this.energyMode == 1) {
                totalEnergy = capEnergy;
                totalMaxEnergy = capMax;
            } else {
                totalEnergy = capEnergy;
                totalMaxEnergy = capMax;
                for (ItemStack stack : modularItems) {
                    IOPStorage storage = stack.getCapability(com.brandon3055.draconicevolution.api.capability.DECapabilities.OP_STORAGE).orElse(null);
                    if (storage != null) {
                        totalEnergy = com.brandon3055.brandonscore.utils.Utils.safeAdd(storage.getOPStored(), totalEnergy);
                        totalMaxEnergy = com.brandon3055.brandonscore.utils.Utils.safeAdd(storage.getMaxOPStored(), totalMaxEnergy);
                    }
                }
            }
        } else {
            for (ItemStack stack : modularItems) {
                IOPStorage storage = stack.getCapability(com.brandon3055.draconicevolution.api.capability.DECapabilities.OP_STORAGE).orElse(null);
                if (storage != null) {
                    totalEnergy = com.brandon3055.brandonscore.utils.Utils.safeAdd(storage.getOPStored(), totalEnergy);
                    totalMaxEnergy = com.brandon3055.brandonscore.utils.Utils.safeAdd(storage.getMaxOPStored(), totalMaxEnergy);
                }
            }
        }

        this.energyBar = totalMaxEnergy > 0 ? (double) totalEnergy / (double) totalMaxEnergy : 0;
        if (this.numericEnergy) {
            this.energyText = net.minecraft.client.resources.language.I18n.get("op.brandonscore.op") + ": " + com.brandon3055.brandonscore.utils.Utils.formatNumber(totalEnergy);
        } else {
            this.energyText = "";
        }

        // 4. Sum up Totems
        if (this.showUndying) {
            List<UndyingEntity> totems = new ArrayList<>();
            for (ItemStack stack : modularItems) {
                com.brandon3055.draconicevolution.api.capability.ModuleHost host = stack.getCapability(com.brandon3055.draconicevolution.api.capability.DECapabilities.MODULE_HOST_CAPABILITY).orElse(null);
                if (host != null) {
                    host.getEntitiesByType(ModuleTypes.UNDYING)
                            .map(e -> (UndyingEntity) e)
                            .forEach(totems::add);
                }
            }

            totems.sort(Comparator.comparing(e -> e.getModule().getModuleTechLevel().index));

            int chargedTotems = 0;
            this.totemStatus = new double[totems.size()];
            for (int i = 0; i < totems.size(); i++) {
                UndyingEntity entity = totems.get(i);
                if (entity.isCharged()) {
                    chargedTotems++;
                }
                this.totemStatus[i] = entity.isCharged() ? -1 : entity.getCharge();
            }

            if (this.lastTotemCount != totems.size()) {
                this.lastTotemCount = totems.size();
            } else if (chargedTotems > this.lastChargedTotemCount && this.mc.level != null) {
                this.mc.level.playLocalSound(this.mc.player.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0f, 2.0f, false);
            }
            this.lastChargedTotemCount = chargedTotems;
        } else {
            this.totemStatus = new double[0];
        }

        this.renderHud = true;
        ci.cancel();
    }
}
