package com.draconicarmorrewrite.items;

import com.draconicarmorrewrite.upgrade.OASUpgrade;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpgradeCoreItem extends Item {
    private final OASUpgrade upgrade;

    public UpgradeCoreItem(OASUpgrade upgrade, Properties properties) {
        super(properties);
        this.upgrade = upgrade;
    }

    public OASUpgrade getUpgrade() {
        return this.upgrade;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.upgrade.translationKey() + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
