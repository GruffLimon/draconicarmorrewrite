package com.draconicarmorrewrite.items;

import com.brandon3055.brandonscore.api.power.OPStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class UpgradableOPStorage extends OPStorage {
    private final ItemStack stack;
    private final long baseCapacity;
    private final long baseTransfer;

    public UpgradableOPStorage(ItemStack stack, long baseCapacity, long baseTransfer) {
        super(baseCapacity, baseTransfer);
        this.stack = stack;
        this.baseCapacity = baseCapacity;
        this.baseTransfer = baseTransfer;
    }

    @Override
    public long getOPStored() {
        if (stack.hasTag() && stack.getTag().contains("Energy")) {
            return stack.getTag().getLong("Energy");
        }
        return super.getOPStored();
    }

    @Override
    public long getMaxOPStored() {
        if (!stack.isEmpty()) {
            return baseCapacity * com.draconicarmorrewrite.upgrade.OASUpgradeHelper.getEnergyMultiplier(stack);
        }
        return baseCapacity;
    }

    @Override
    public long maxReceive() {
        return Long.MAX_VALUE;
    }

    @Override
    public long maxExtract() {
        if (!stack.isEmpty()) {
            return baseTransfer * com.draconicarmorrewrite.upgrade.OASUpgradeHelper.getEnergyMultiplier(stack);
        }
        return baseTransfer;
    }

    @Override
    public long receiveOP(long maxReceive, boolean simulate) {
        long stored = getOPStored();
        long max = getMaxOPStored();
        long accept = Math.min(max - stored, maxReceive);
        if (accept > 0 && !simulate) {
            modifyEnergyStored(accept);
        }
        return accept;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        long stored = getOPStored();
        long max = getMaxOPStored();
        long accept = Math.min(max - stored, (long) maxReceive);
        if (accept > 0 && !simulate) {
            modifyEnergyStored(accept);
        }
        return (int) accept;
    }

    @Override
    public long extractOP(long maxExtract, boolean simulate) {
        long stored = getOPStored();
        long extract = Math.min(stored, maxExtract);
        if (extract > 0 && !simulate) {
            modifyEnergyStored(-extract);
        }
        return extract;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        long stored = getOPStored();
        long extract = Math.min(stored, (long) maxExtract);
        if (extract > 0 && !simulate) {
            modifyEnergyStored(-extract);
        }
        return (int) extract;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public long modifyEnergyStored(long amount) {
        long stored = getOPStored();
        long max = getMaxOPStored();
        
        long newStored = stored + amount;
        if (newStored > max) {
            amount = max - stored;
        } else if (newStored < 0) {
            amount = -stored;
        }
        
        long finalStored = stored + amount;
        stack.getOrCreateTag().putLong("Energy", finalStored);
        this.energy = finalStored;
        this.capacity = max;
        
        return Math.abs(amount);
    }
}
