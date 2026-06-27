/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.narration.NarrationElementOutput
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package com.draconicarmorrewrite.client.gui;

import com.draconicarmorrewrite.config.OASArmorConfig;
import com.draconicarmorrewrite.config.OASArmorConfigKey;
import com.draconicarmorrewrite.items.ClassicUpgradeableArmorItem;
import com.draconicarmorrewrite.items.OASUpgradableItem;
import com.draconicarmorrewrite.items.ClassicToolConfig;
import com.draconicarmorrewrite.items.EquipmentTechLevel;
import com.draconicarmorrewrite.network.ArmorConfigAction;
import com.draconicarmorrewrite.network.ArmorConfigActionPacket;
import com.draconicarmorrewrite.network.PacketHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArmorConfigScreen
extends Screen {
    private final Screen parent;
    private final ItemStack stack;
    private final InteractionHand hand;
    private final EquipmentSlot equipmentSlot;
    private final Integer inventorySlot;
    private final Component title;
    private static final int PANEL_W = 320;
    private static final int PANEL_H = 235;
    private static final int CYAN = -16711681;
    private static final int FRAME = -16711681;
    private static final int SELECTED = -16737895;
    private static final int PURPLE = -14548941;
    private static final int RED = -65536;
    private static final int GREEN = -16711936;
    private static final int PANEL_BG = -1358954496;
    private ConfigEntry selectedEntry;

    public ArmorConfigScreen(Screen parent, ItemStack stack, InteractionHand hand) {
        super((Component)Component.translatable((String)"screen.originalarmorstuff.configure_item"));
        this.parent = parent;
        this.stack = stack;
        this.hand = hand;
        this.equipmentSlot = null;
        this.inventorySlot = null;
        this.title = stack.getHoverName();
    }

    public ArmorConfigScreen(Screen parent, ItemStack stack, EquipmentSlot equipmentSlot) {
        super((Component)Component.translatable((String)"screen.originalarmorstuff.configure_item"));
        this.parent = parent;
        this.stack = stack;
        this.hand = null;
        this.equipmentSlot = equipmentSlot;
        this.inventorySlot = null;
        this.title = stack.getHoverName();
    }

    public ArmorConfigScreen(Screen parent, ItemStack stack, int inventorySlot) {
        super((Component)Component.translatable((String)"screen.originalarmorstuff.configure_item"));
        this.parent = parent;
        this.stack = stack;
        this.hand = null;
        this.equipmentSlot = null;
        this.inventorySlot = inventorySlot;
        this.title = stack.getHoverName();
    }

    protected void init() {
        super.init();
        Item item = this.stack.getItem();
        if (!(item instanceof OASUpgradableItem)) {
            return;
        }
        OASUpgradableItem upgradableItem = (OASUpgradableItem)item;
        int centerX = this.width / 2;
        int top = Math.max(4, this.height / 2 - 120);
        int centerY = top + 120;
        int buttonWidth = 138;
        int leftX = centerX - 159;
        int rightX = centerX + 21;
        int topY = centerY - 88;
        List<ConfigEntry> entries = this.buildClassicEntries(upgradableItem);
        this.selectedEntry = null;
        int rowCount = Math.max(1, (entries.size() + 1) / 2);
        int rowHeight = Math.min(20, (centerY - 12 - topY) / rowCount);
        for (int i = 0; i < entries.size(); ++i) {
            ConfigEntry entry = entries.get(i);
            int x = i % 2 == 0 ? leftX : rightX;
            int y = topY + i / 2 * rowHeight;
            this.addRenderableWidget(new ConfigButton(x, y, buttonWidth, rowHeight - 1, entry));
        }
        this.addRenderableWidget(Button.builder((Component)Component.literal((String)"<"), btn -> this.onClose()).bounds(centerX - 154, centerY - 108, 20, 16).build());
        this.addRenderableWidget(new ControlWidget(centerX - 159, centerY - 6, 318, 18));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        int centerX = this.width / 2;
        int top = Math.max(4, this.height / 2 - 120);
        int centerY = top + 120;
        int left = centerX - 160;
        int right = left + 320;
        int bottom = top + 235;
        guiGraphics.fill(left, top, right, bottom, -1358954496);
        ArmorConfigScreen.drawBorder(guiGraphics, left, top, right, bottom, -16711681);
        ArmorConfigScreen.drawSeparator(guiGraphics, left, right, centerY - 8, -16711681);
        ArmorConfigScreen.drawSeparator(guiGraphics, left, right, centerY + 12, -16711681);
        ArmorConfigScreen.drawRectFrame(guiGraphics, centerX - 20, centerY - 103, centerX + 20, centerY - 11, -65536);
        guiGraphics.renderItem(this.stack, centerX - 8, centerY - 63);
        guiGraphics.drawString(this.font, this.title, centerX, centerY - 116, -16711681);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.selectedEntry != null) {
            int y = centerY + 16;
            for (FormattedCharSequence line : this.font.split((FormattedText)Component.literal((String)this.selectedEntry.description()), 302)) {
                if (y > bottom - 10) break;
                guiGraphics.drawString(this.font, line, left + 8, y, 0xFFFFFF, false);
                y += 12;
            }
        }
    }

    public void onClose() {
        if (this.parent != null) {
            this.minecraft.setScreen(this.parent);
        } else {
            super.onClose();
        }
    }

    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (net.minecraft.client.Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.minecraft.setScreen(new GlobalConfigScreen());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void send(ArmorConfigAction action) {
        PacketHandler.CHANNEL.sendToServer((Object)new ArmorConfigActionPacket(this.hand, this.equipmentSlot, this.inventorySlot, action));
        this.applyLocalAction(action);
    }

    private void send(ArmorConfigAction action, double value) {
        PacketHandler.CHANNEL.sendToServer((Object)new ArmorConfigActionPacket(this.hand, this.equipmentSlot, this.inventorySlot, action, value));
        this.applyLocalValue(action, value);
    }

    private void applyLocalValue(ArmorConfigAction action, double value) {
        OASArmorConfigKey key;
        Item item = this.stack.getItem();
        switch (action) {
            case CYCLE_MOVE_SPEED: {
                key = OASArmorConfigKey.ARMOR_SPEED_MODIFIER;
                break;
            }
            case CYCLE_JUMP_BOOST: {
                key = OASArmorConfigKey.ARMOR_JUMP_MODIFIER;
                break;
            }
            case CYCLE_FLIGHT_SPEED: {
                key = OASArmorConfigKey.ARMOR_FLIGHT_SPEED;
                break;
            }
            case CYCLE_VERTICAL_FLIGHT_SPEED: {
                key = OASArmorConfigKey.ARMOR_VERTICAL_FLIGHT_SPEED;
                break;
            }
            case SET_DIG_SPEED: {
                key = OASArmorConfigKey.DIG_SPEED;
                break;
            }
            case CYCLE_DIG_AOE: {
                key = OASArmorConfigKey.DIG_AOE;
                break;
            }
            case CYCLE_DIG_DEPTH: {
                key = OASArmorConfigKey.DIG_DEPTH;
                break;
            }
            case CYCLE_ATTACK_AOE: {
                key = OASArmorConfigKey.ATTACK_AOE;
                break;
            }
            default: {
                key = null;
            }
        }
        if (key != null) {
            int min = ArmorConfigScreen.getMinValue(action);
            int max = ArmorConfigScreen.getMaxValue(this.stack, action);
            if (max >= min) {
                if (ArmorConfigScreen.isToolDropdown(action)) {
                    OASArmorConfig.setInt(this.stack, key, ClassicToolConfig.valueFromProgress(this.stack, ArmorConfigScreen.toolActionLike(action), value));
                } else {
                    OASArmorConfig.setInt(this.stack, key, (int)Math.round((double)min + value * (double)(max - min)));
                }
            }
        }
    }

    private void applyLocalBowValue(Object bow, ArmorConfigAction action, double value) {
        // Bow items not implemented in DraconicArmorRewrite
    }

    private static int getMinValue(ArmorConfigAction action) {
        return action == ArmorConfigAction.SET_DIG_SPEED ? 1 : 0;
    }

    private static int getMaxValue(ItemStack stack, ArmorConfigAction action) {
        return switch (action) {
            case CYCLE_MOVE_SPEED -> {
                Item var3_2 = stack.getItem();
                if (var3_2 instanceof ClassicUpgradeableArmorItem) {
                    ClassicUpgradeableArmorItem armor = (ClassicUpgradeableArmorItem)var3_2;
                    yield armor.getMaxSpeedModifierPercent(stack);
                }
                yield 0;
            }
            case CYCLE_JUMP_BOOST -> {
                Item var3_3 = stack.getItem();
                if (var3_3 instanceof ClassicUpgradeableArmorItem) {
                    ClassicUpgradeableArmorItem armor = (ClassicUpgradeableArmorItem)var3_3;
                    yield armor.getMaxJumpModifierPercent(stack);
                }
                yield 0;
            }
            case CYCLE_FLIGHT_SPEED -> {
                Item var3_4 = stack.getItem();
                if (var3_4 instanceof ClassicUpgradeableArmorItem) {
                    ClassicUpgradeableArmorItem armor = (ClassicUpgradeableArmorItem)var3_4;
                    yield armor.getMaxFlightSpeedModifierPercent(stack);
                }
                yield 0;
            }
            case CYCLE_VERTICAL_FLIGHT_SPEED -> 0;
            case SET_DIG_SPEED -> 100;
            case CYCLE_DIG_AOE -> ClassicToolConfig.getMaxDigAoe(stack);
            case CYCLE_DIG_DEPTH -> ClassicToolConfig.getMaxDigDepth(stack);
            case CYCLE_ATTACK_AOE -> ClassicToolConfig.getMaxAttackAoe(stack);
            case SET_BOW_ARROW_DAMAGE -> {
                Item var3_5 = stack.getItem();
                // bow not applicable
                yield 0;
            }
            case SET_BOW_ARROW_SPEED -> {
                Item var3_6 = stack.getItem();
                // bow not applicable
                yield 0;
            }
            case SET_BOW_EXPLOSION_POWER -> {
                Item var3_7 = stack.getItem();
                // bow not applicable
                yield 0;
            }
            case SET_BOW_ZOOM -> {
                Item var3_8 = stack.getItem();
                // bow not applicable
                yield 0;
            }
            case SET_BOW_SHOCK_POWER -> {
                Item var3_9 = stack.getItem();
                // bow not applicable
                yield 0;
            }
            default -> 100;
        };
    }

    private static boolean isToolDropdown(ArmorConfigAction action) {
        return action == ArmorConfigAction.CYCLE_DIG_AOE || action == ArmorConfigAction.CYCLE_DIG_DEPTH || action == ArmorConfigAction.CYCLE_ATTACK_AOE;
    }

    private static ClassicToolConfig.ArmorConfigActionLike toolActionLike(ArmorConfigAction action) {
        return switch (action) {
            case CYCLE_DIG_AOE -> ClassicToolConfig.ArmorConfigActionLike.DIG_AOE;
            case CYCLE_DIG_DEPTH -> ClassicToolConfig.ArmorConfigActionLike.DIG_DEPTH;
            case CYCLE_ATTACK_AOE -> ClassicToolConfig.ArmorConfigActionLike.ATTACK_AOE;
            default -> throw new IllegalArgumentException("Unsupported tool config action: " + action);
        };
    }

    private static String[] dropdownOptions(ItemStack stack, ArmorConfigAction action) {
        int[] nArray;
        switch (action) {
            case CYCLE_DIG_AOE: {
                nArray = ClassicToolConfig.getDigAoeValues(stack);
                break;
            }
            case CYCLE_DIG_DEPTH: {
                nArray = ClassicToolConfig.getDigDepthValues(stack);
                break;
            }
            case CYCLE_ATTACK_AOE: {
                nArray = ClassicToolConfig.getAttackAoeValues(stack);
                break;
            }
            default: {
                int[] nArray2 = new int[1];
                nArray = nArray2;
                nArray2[0] = 0;
            }
        }
        int[] values = nArray;
        String[] options = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            int value = values[values.length - 1 - i];
            options[i] = ArmorConfigScreen.dropdownLabel(action, value);
        }
        return options;
    }

    private static int steppedValueCount(ItemStack stack, ArmorConfigAction action) {
        return switch (action) {
            case CYCLE_DIG_AOE -> ClassicToolConfig.getDigAoeValues(stack).length;
            case CYCLE_DIG_DEPTH -> ClassicToolConfig.getDigDepthValues(stack).length;
            case CYCLE_ATTACK_AOE -> ClassicToolConfig.getAttackAoeValues(stack).length;
            default -> 0;
        };
    }

    private static String dropdownLabel(ArmorConfigAction action, int value) {
        if (value <= 0) {
            return ArmorConfigScreen.tr("config.originalarmorstuff.off");
        }
        if (action == ArmorConfigAction.CYCLE_DIG_DEPTH) {
            return Integer.toString(value);
        }
        int size = 1 + value * 2;
        return size + "x" + size;
    }

    private List<ConfigEntry> buildClassicEntries(OASUpgradableItem upgradableItem) {
        ArrayList<ConfigEntry> entries = new ArrayList<ConfigEntry>();
        if (upgradableItem instanceof ClassicUpgradeableArmorItem) {
            ClassicUpgradeableArmorItem armorItem = (ClassicUpgradeableArmorItem)upgradableItem;
            this.addArmorEntries(entries, armorItem);
        }
        return entries;
    }

    private void addArmorEntries(List<ConfigEntry> entries, ClassicUpgradeableArmorItem armorItem) {
        switch (armorItem.getType()) {
            case HELMET: {
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.night_vision"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.night_vision.desc"), ArmorConfigAction.TOGGLE_NIGHT_VISION));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.night_vision_lock"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.night_vision_lock.desc"), ArmorConfigAction.TOGGLE_NIGHT_VISION_LOCK));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.auto_feed"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.auto_feed.desc"), ArmorConfigAction.TOGGLE_AUTO_FEED));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.hide_armor"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.hide_armor.desc"), ArmorConfigAction.TOGGLE_HIDE_ARMOR));
                break;
            }
            case CHESTPLATE: {
                entries.add(ConfigEntry.slider(ArmorConfigScreen.tr("config.originalarmorstuff.armor.flight_speed"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.flight_speed.desc"), ArmorConfigAction.CYCLE_FLIGHT_SPEED));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.inertia_cancel"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.inertia_cancel.desc"), ArmorConfigAction.TOGGLE_INERTIA_CANCEL));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.flight_lock"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.flight_lock.desc"), ArmorConfigAction.TOGGLE_FLIGHT_LOCK));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.sprint_boost"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.sprint_boost.desc"), ArmorConfigAction.TOGGLE_SPRINT_BOOST));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.hide_armor"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.hide_armor.desc"), ArmorConfigAction.TOGGLE_HIDE_ARMOR));
                break;
            }
            case LEGGINGS: {
                entries.add(ConfigEntry.slider(ArmorConfigScreen.tr("config.originalarmorstuff.armor.move_speed"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.move_speed.desc"), ArmorConfigAction.CYCLE_MOVE_SPEED));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.fov_warp"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.fov_warp.desc"), ArmorConfigAction.TOGGLE_FOV_WARP));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.sprint_boost"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.sprint_boost.desc"), ArmorConfigAction.TOGGLE_SPRINT_BOOST));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.hide_armor"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.hide_armor.desc"), ArmorConfigAction.TOGGLE_HIDE_ARMOR));
                break;
            }
            case BOOTS: {
                entries.add(ConfigEntry.slider(ArmorConfigScreen.tr("config.originalarmorstuff.armor.jump_boost"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.jump_boost.desc"), ArmorConfigAction.CYCLE_JUMP_BOOST));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.hill_step"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.hill_step.desc"), ArmorConfigAction.TOGGLE_HILL_STEP));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.sprint_boost"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.sprint_boost.desc"), ArmorConfigAction.TOGGLE_SPRINT_BOOST));
                entries.add(ConfigEntry.toggle(ArmorConfigScreen.tr("config.originalarmorstuff.armor.hide_armor"), ArmorConfigScreen.tr("config.originalarmorstuff.armor.hide_armor.desc"), ArmorConfigAction.TOGGLE_HIDE_ARMOR));
                break;
            }
        }
    }


    private ClassicUpgradeableArmorItem getArmorItem() {
        return (ClassicUpgradeableArmorItem) this.stack.getItem();
    }

    private void applyLocalAction(ArmorConfigAction action) {
        switch (action) {
            case TOGGLE_NIGHT_VISION: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.ARMOR_NIGHT_VISION, !this.getArmorItem().hasNightVision(this.stack));
                break;
            }
            case TOGGLE_NIGHT_VISION_LOCK: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.ARMOR_NIGHT_VISION_LOCK, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.ARMOR_NIGHT_VISION_LOCK, false));
                break;
            }
            case TOGGLE_AUTO_FEED: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.ARMOR_AUTO_FEED, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.ARMOR_AUTO_FEED, false));
                break;
            }
            case TOGGLE_HIDE_ARMOR: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.HIDE_ARMOR, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.HIDE_ARMOR, false));
                break;
            }
            case TOGGLE_INERTIA_CANCEL: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.ARMOR_INERTIA_CANCEL, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.ARMOR_INERTIA_CANCEL, false));
                break;
            }
            case TOGGLE_FOV_WARP: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.ARMOR_SPEED_FOV_WARP, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.ARMOR_SPEED_FOV_WARP, true));
                break;
            }
            case TOGGLE_HILL_STEP: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.ARMOR_HILL_STEP, !this.getArmorItem().hasHillStep(this.stack));
                break;
            }
            case TOGGLE_SPRINT_BOOST: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.SPRINT_BOOST, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.SPRINT_BOOST, false));
                break;
            }
            case TOGGLE_FLIGHT_LOCK: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.ARMOR_FLIGHT_LOCK, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.ARMOR_FLIGHT_LOCK, false));
                break;
            }
            case CYCLE_MOVE_SPEED: {
                OASArmorConfig.setInt(this.stack, OASArmorConfigKey.ARMOR_SPEED_MODIFIER, ArmorConfigScreen.cycleValue(OASArmorConfig.getInt(this.stack, OASArmorConfigKey.ARMOR_SPEED_MODIFIER, 0), 0, 25, 50));
                break;
            }
            case CYCLE_JUMP_BOOST: {
                OASArmorConfig.setInt(this.stack, OASArmorConfigKey.ARMOR_JUMP_MODIFIER, ArmorConfigScreen.cycleValue(OASArmorConfig.getInt(this.stack, OASArmorConfigKey.ARMOR_JUMP_MODIFIER, 0), 0, 25, 50));
                break;
            }
            case CYCLE_FLIGHT_SPEED: {
                OASArmorConfig.setInt(this.stack, OASArmorConfigKey.ARMOR_FLIGHT_SPEED, ArmorConfigScreen.cycleValue(OASArmorConfig.getInt(this.stack, OASArmorConfigKey.ARMOR_FLIGHT_SPEED, 0), 0, 100, 200, 300, 400, 500, 600));
                break;
            }
            case CYCLE_VERTICAL_FLIGHT_SPEED: {
                OASArmorConfig.setInt(this.stack, OASArmorConfigKey.ARMOR_VERTICAL_FLIGHT_SPEED, ArmorConfigScreen.cycleValue(OASArmorConfig.getInt(this.stack, OASArmorConfigKey.ARMOR_VERTICAL_FLIGHT_SPEED, 100), 50, 100, 150, 200));
                break;
            }
            case TOGGLE_AOE_SAFE_MODE: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.AOE_SAFE_MODE, !ClassicToolConfig.isAoeSafeMode(this.stack));
                break;
            }
            case TOGGLE_AOE_HEIGHT_NORMALIZATION: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.AOE_HEIGHT_NORMALIZATION, !ClassicToolConfig.isAoeHeightNormalized(this.stack));
                break;
            }
            case TOGGLE_SHOW_DIG_AOE: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.SHOW_DIG_AOE, !ClassicToolConfig.shouldShowDigAoe(this.stack));
                break;
            }
            case CONFIGURE_JUNK_FILTER: {
                break;
            }
            case TOGGLE_JUNK_FILTER: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.ENABLE_JUNK_FILTER, !ClassicToolConfig.isJunkFilterEnabled(this.stack));
                break;
            }
            case TOGGLE_JUNK_NBT_SENS: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.JUNK_NBT_SENS, !ClassicToolConfig.isJunkNbtSensitive(this.stack));
                break;
            }
            case TOGGLE_HARVEST_INDICATOR: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.SHOW_HARVEST_INDICATOR, !ClassicToolConfig.shouldShowHarvestIndicator(this.stack));
                break;
            }
            case TOGGLE_HOE_LAND_FILL: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.HOE_LAND_FILL, !ClassicToolConfig.isHoeLandFillEnabled(this.stack));
                break;
            }
            case CYCLE_DIG_AOE: {
                int[] vals = ClassicToolConfig.getDigAoeValues(this.stack);
                OASArmorConfig.setInt(this.stack, OASArmorConfigKey.DIG_AOE, ArmorConfigScreen.cycleValue(OASArmorConfig.getInt(this.stack, OASArmorConfigKey.DIG_AOE, 0), vals));
                break;
            }
            case CYCLE_DIG_DEPTH: {
                int[] vals = ClassicToolConfig.getDigDepthValues(this.stack);
                OASArmorConfig.setInt(this.stack, OASArmorConfigKey.DIG_DEPTH, ArmorConfigScreen.cycleValue(OASArmorConfig.getInt(this.stack, OASArmorConfigKey.DIG_DEPTH, 0), vals));
                break;
            }
            case CYCLE_ATTACK_AOE: {
                OASArmorConfig.setInt(this.stack, OASArmorConfigKey.ATTACK_AOE, ArmorConfigScreen.cycleValue(OASArmorConfig.getInt(this.stack, OASArmorConfigKey.ATTACK_AOE, 0), ClassicToolConfig.getAttackAoeValues(this.stack)));
                break;
            }
            case TOGGLE_ATTACK_DAMAGE: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.ATTACK_DAMAGE_BOOST, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.ATTACK_DAMAGE_BOOST, false));
                break;
            }
            case TOGGLE_AUTO_FIRE: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.AUTO_FIRE, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.AUTO_FIRE, false));
                break;
            }
            case TOGGLE_BOW_FIRE_ARROW: {
                OASArmorConfig.setBoolean(this.stack, OASArmorConfigKey.BOW_FIRE_ARROW, !OASArmorConfig.getBoolean(this.stack, OASArmorConfigKey.BOW_FIRE_ARROW, false));
            }
        }
    }

    private static int cycleValue(int current, int ... values) {
        for (int i = 0; i < values.length; ++i) {
            if (values[i] != current) continue;
            return values[(i + 1) % values.length];
        }
        return values[0];
    }

    private static String tr(String key) {
        return I18n.get((String)key, (Object[])new Object[0]);
    }

    private static void drawBorder(GuiGraphics guiGraphics, int left, int top, int right, int bottom, int color) {
        guiGraphics.fill(left, top, right, top + 1, color);
        guiGraphics.fill(left, bottom - 1, right, bottom, color);
        guiGraphics.fill(left, top, left + 1, bottom, color);
        guiGraphics.fill(right - 1, top, right, bottom, color);
    }

    private static void drawSeparator(GuiGraphics guiGraphics, int left, int right, int y, int color) {
        guiGraphics.fill(left, y, right, y + 1, color);
    }

    private static void drawRectFrame(GuiGraphics guiGraphics, int left, int top, int right, int bottom, int color) {
        guiGraphics.fill(left, top, right, top + 1, color);
        guiGraphics.fill(left, bottom - 1, right, bottom, color);
        guiGraphics.fill(left, top, left + 1, bottom, color);
        guiGraphics.fill(right - 1, top, right, bottom, color);
        guiGraphics.fill(left + 1, top + 1, right - 1, bottom - 1, -16777216);
    }

    private record ConfigEntry(String label, String description, ArmorConfigAction action, EntryType type) {
        static ConfigEntry toggle(String label, String description, ArmorConfigAction action) {
            return new ConfigEntry(label, description, action, EntryType.TOGGLE);
        }

        static ConfigEntry slider(String label, String description, ArmorConfigAction action) {
            return new ConfigEntry(label, description, action, EntryType.SLIDER);
        }

        static ConfigEntry steppedSlider(String label, String description, ArmorConfigAction action) {
            return new ConfigEntry(label, description, action, EntryType.STEPPED_SLIDER);
        }

        static ConfigEntry dropdown(String label, String description, ArmorConfigAction action) {
            return new ConfigEntry(label, description, action, EntryType.DROPDOWN);
        }

        static ConfigEntry action(String label, String description, ArmorConfigAction action) {
            return new ConfigEntry(label, description, action, EntryType.ACTION);
        }

        boolean isSlider() {
            return this.type == EntryType.SLIDER;
        }

        boolean isSteppedSlider() {
            return this.type == EntryType.STEPPED_SLIDER;
        }

        boolean usesSlider() {
            return this.isSlider() || this.isSteppedSlider();
        }

        boolean isDropdown() {
            return this.type == EntryType.DROPDOWN;
        }

        boolean isToggle() {
            return this.type == EntryType.TOGGLE;
        }

        String valueText(ItemStack stack) {
            return switch (this.action) {
                default -> throw new IncompatibleClassChangeError();
                case TOGGLE_NIGHT_VISION -> {
                    boolean v0;
                    Item var3_2 = stack.getItem();
                    if (var3_2 instanceof ClassicUpgradeableArmorItem) {
                        ClassicUpgradeableArmorItem armor = (ClassicUpgradeableArmorItem)var3_2;
                        v0 = armor.hasNightVision(stack);
                    } else {
                        v0 = OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_NIGHT_VISION, false);
                    }
                    yield ConfigEntry.boolText(v0);
                }
                case TOGGLE_NIGHT_VISION_LOCK -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_NIGHT_VISION_LOCK, false));
                case TOGGLE_AUTO_FEED -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_AUTO_FEED, false));
                case TOGGLE_HIDE_ARMOR -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.HIDE_ARMOR, false));
                case TOGGLE_INERTIA_CANCEL -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_INERTIA_CANCEL, false));
                case TOGGLE_FOV_WARP -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_SPEED_FOV_WARP, true));
                case TOGGLE_HILL_STEP -> {
                    boolean v2;
                    Item var3_3 = stack.getItem();
                    if (var3_3 instanceof ClassicUpgradeableArmorItem) {
                        ClassicUpgradeableArmorItem armor = (ClassicUpgradeableArmorItem)var3_3;
                        v2 = armor.hasHillStep(stack);
                    } else {
                        v2 = OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_HILL_STEP, false);
                    }
                    yield ConfigEntry.boolText(v2);
                }
                case TOGGLE_SPRINT_BOOST -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.SPRINT_BOOST, false));
                case TOGGLE_FLIGHT_LOCK -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ARMOR_FLIGHT_LOCK, false));
                case CYCLE_MOVE_SPEED -> {
                    int v3;
                    Item var3_4 = stack.getItem();
                    if (var3_4 instanceof ClassicUpgradeableArmorItem) {
                        ClassicUpgradeableArmorItem armor = (ClassicUpgradeableArmorItem)var3_4;
                        v3 = armor.getConfiguredSpeedPercent(stack);
                    } else {
                        v3 = 0;
                    }
                    yield "+" + v3 + "%";
                }
                case CYCLE_JUMP_BOOST -> {
                    int v4;
                    Item var3_5 = stack.getItem();
                    if (var3_5 instanceof ClassicUpgradeableArmorItem) {
                        ClassicUpgradeableArmorItem armor = (ClassicUpgradeableArmorItem)var3_5;
                        v4 = armor.getConfiguredJumpPercent(stack);
                    } else {
                        v4 = 0;
                    }
                    yield "+" + v4 + "%";
                }
                case CYCLE_FLIGHT_SPEED -> {
                    int v5;
                    Item var3_6 = stack.getItem();
                    if (var3_6 instanceof ClassicUpgradeableArmorItem) {
                        ClassicUpgradeableArmorItem armor = (ClassicUpgradeableArmorItem)var3_6;
                        v5 = armor.getConfiguredFlightSpeed(stack);
                    } else {
                        v5 = 0;
                    }
                    yield "+" + v5 + "%";
                }
                case CYCLE_VERTICAL_FLIGHT_SPEED -> {
                    int v6;
                    Item var3_7 = stack.getItem();
                    if (var3_7 instanceof ClassicUpgradeableArmorItem) {
                        ClassicUpgradeableArmorItem armor = (ClassicUpgradeableArmorItem)var3_7;
                        v6 = armor.getConfiguredVerticalFlightSpeed(stack);
                    } else {
                        v6 = 0;
                    }
                    yield "+" + v6 + "%";
                }
                case SET_DIG_SPEED -> ClassicToolConfig.getDigSpeedPercent(stack) + "%";
                case TOGGLE_AOE_SAFE_MODE -> ConfigEntry.boolText(ClassicToolConfig.isAoeSafeMode(stack));
                case TOGGLE_AOE_HEIGHT_NORMALIZATION -> ConfigEntry.boolText(ClassicToolConfig.isAoeHeightNormalized(stack));
                case TOGGLE_SHOW_DIG_AOE -> ConfigEntry.boolText(ClassicToolConfig.shouldShowDigAoe(stack));
                case CONFIGURE_JUNK_FILTER -> ArmorConfigScreen.tr("config.originalarmorstuff.configure");
                case TOGGLE_JUNK_FILTER -> ConfigEntry.boolText(ClassicToolConfig.isJunkFilterEnabled(stack));
                case TOGGLE_JUNK_NBT_SENS -> ConfigEntry.boolText(ClassicToolConfig.isJunkNbtSensitive(stack));
                case TOGGLE_HARVEST_INDICATOR -> ConfigEntry.boolText(ClassicToolConfig.shouldShowHarvestIndicator(stack));
                case TOGGLE_HOE_LAND_FILL -> ConfigEntry.boolText(ClassicToolConfig.isHoeLandFillEnabled(stack));
                case CYCLE_DIG_AOE -> {
                    int aoe = ClassicToolConfig.getDigAoe(stack);
                    if (aoe == 0) {
                        yield ArmorConfigScreen.tr("config.originalarmorstuff.off");
                    }
                    yield 1 + 2 * aoe + "x" + (1 + 2 * aoe);
                }
                case CYCLE_DIG_DEPTH -> {
                    int depth = ClassicToolConfig.getDigDepth(stack);
                    if (depth == 0) {
                        yield ArmorConfigScreen.tr("config.originalarmorstuff.off");
                    }
                    yield "+" + depth;
                }
                case CYCLE_ATTACK_AOE -> {
                    int aoe = ClassicToolConfig.getAttackAoe(stack);
                    if (aoe == 0) {
                        yield ArmorConfigScreen.tr("config.originalarmorstuff.off");
                    }
                    yield 1 + 2 * aoe + "x" + (1 + 2 * aoe);
                }
                case TOGGLE_ATTACK_DAMAGE -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.ATTACK_DAMAGE_BOOST, false));
                case TOGGLE_AUTO_FIRE -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.AUTO_FIRE, false));
                case SET_BOW_ARROW_DAMAGE -> {
                    Item var3_8 = stack.getItem();
                    // bow not applicable
                    yield "0";
                }
                case SET_BOW_ARROW_SPEED -> {
                    Item var3_9 = stack.getItem();
                    // bow not applicable
                    yield "+0%";
                }
                case SET_BOW_EXPLOSION_POWER -> {
                    Item var3_10 = stack.getItem();
                    // bow not applicable
                    yield "0";
                }
                case SET_BOW_ZOOM -> {
                    Item var3_11 = stack.getItem();
                    // bow not applicable
                    yield "+0%";
                }
                case TOGGLE_BOW_FIRE_ARROW -> ConfigEntry.boolText(OASArmorConfig.getBoolean(stack, OASArmorConfigKey.BOW_FIRE_ARROW, false));
                case SET_BOW_SHOCK_POWER -> {
                    Item var3_12 = stack.getItem();
                    // bow not applicable
                    yield "0";
                }
            };
        }

        float progress(ItemStack stack) {
            if (!this.usesSlider() && !this.isDropdown()) {
                return this.valueText(stack).equals(ArmorConfigScreen.tr("config.originalarmorstuff.on")) ? 1.0f : 0.0f;
            }
            Item item = stack.getItem();
            if (ArmorConfigScreen.isToolDropdown(this.action)) {
                int dropdownValue = switch (this.action) {
                    case CYCLE_DIG_AOE -> ClassicToolConfig.getDigAoe(stack);
                    case CYCLE_DIG_DEPTH -> ClassicToolConfig.getDigDepth(stack);
                    case CYCLE_ATTACK_AOE -> ClassicToolConfig.getAttackAoe(stack);
                    default -> 0;
                };
                return ClassicToolConfig.progressForValue(stack, ArmorConfigScreen.toolActionLike(this.action), dropdownValue);
            }
            int value = switch (this.action) {
                case CYCLE_MOVE_SPEED -> {
                    ClassicUpgradeableArmorItem armor;
                    Item var4_7 = stack.getItem();
                    if (var4_7 instanceof ClassicUpgradeableArmorItem) {
                        armor = (ClassicUpgradeableArmorItem)var4_7;
                        yield armor.getConfiguredSpeedPercent(stack);
                    }
                    yield 0;
                }
                case CYCLE_JUMP_BOOST -> {
                    ClassicUpgradeableArmorItem armor;
                    Item var4_7 = stack.getItem();
                    if (var4_7 instanceof ClassicUpgradeableArmorItem) {
                        armor = (ClassicUpgradeableArmorItem)var4_7;
                        yield armor.getConfiguredJumpPercent(stack);
                    }
                    yield 0;
                }
                case CYCLE_FLIGHT_SPEED -> {
                    ClassicUpgradeableArmorItem armor;
                    Item var4_7 = stack.getItem();
                    if (var4_7 instanceof ClassicUpgradeableArmorItem) {
                        armor = (ClassicUpgradeableArmorItem)var4_7;
                        yield armor.getConfiguredFlightSpeed(stack);
                    }
                    yield 0;
                }
                case CYCLE_VERTICAL_FLIGHT_SPEED -> {
                    ClassicUpgradeableArmorItem armor;
                    Item var4_7 = stack.getItem();
                    if (var4_7 instanceof ClassicUpgradeableArmorItem) {
                        armor = (ClassicUpgradeableArmorItem)var4_7;
                        yield armor.getConfiguredVerticalFlightSpeed(stack);
                    }
                    yield 0;
                }
                case SET_DIG_SPEED -> ClassicToolConfig.getDigSpeedPercent(stack);
                case CYCLE_DIG_AOE -> OASArmorConfig.getInt(stack, OASArmorConfigKey.DIG_AOE, 0);
                case CYCLE_DIG_DEPTH -> OASArmorConfig.getInt(stack, OASArmorConfigKey.DIG_DEPTH, 0);
                case CYCLE_ATTACK_AOE -> OASArmorConfig.getInt(stack, OASArmorConfigKey.ATTACK_AOE, 0);
                default -> 0;
            };
            int min = ArmorConfigScreen.getMinValue(this.action);
            int max = ArmorConfigScreen.getMaxValue(stack, this.action);
            return max - min <= 0 ? 0.0f : (float)(value - min) / (float)(max - min);
        }

        private static String boolText(boolean value) {
            return value ? ArmorConfigScreen.tr("config.originalarmorstuff.on") : ArmorConfigScreen.tr("config.originalarmorstuff.off");
        }

        private static float progressDouble(double value, double max) {
            return max <= 0.0 ? 0.0f : (float)Math.max(0.0, Math.min(1.0, value / max));
        }

        static enum EntryType {
            TOGGLE,
            SLIDER,
            STEPPED_SLIDER,
            DROPDOWN,
            ACTION;

        }
    }

    private final class ConfigButton
    extends Button {
        private final ConfigEntry entry;

        private ConfigButton(int x, int y, int width, int height, ConfigEntry entry) {
            super(x, y, width, height, (Component)Component.literal((String)entry.label()), btn -> {
                ArmorConfigScreen.this.selectedEntry = entry;
            }, Button.DEFAULT_NARRATION);
            this.entry = entry;
        }

        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int border = ArmorConfigScreen.this.selectedEntry == this.entry || this.isHoveredOrFocused() ? 0xFF00FF00 : 0xFF440066;
            guiGraphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, -16777216);
            ArmorConfigScreen.drawBorder(guiGraphics, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, border);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate((float)this.getX() + (float)this.width / 2.0f, (float)this.getY() + (float)this.height / 2.0f - 4.0f, 0.0f);
            float textScale = Math.min(0.85f, (float)(this.width - 8) / (float)Math.max(1, ArmorConfigScreen.this.font.width((FormattedText)this.getMessage())));
            guiGraphics.pose().scale(textScale, textScale, 1.0f);
            guiGraphics.drawString(ArmorConfigScreen.this.font, this.getMessage(), 0, 0, 0xFFFFFF);
            guiGraphics.pose().popPose();
        }
    }

    private final class ControlWidget
    extends AbstractWidget {
        private boolean dropdownOpen;
        private ConfigEntry lastSelected;

        private ControlWidget(int x, int y, int width, int height) {
            super(x, y, width, height, (Component)Component.empty());
            this.dropdownOpen = false;
            this.lastSelected = null;
        }

        public void onClick(double mouseX, double mouseY) {
            if (ArmorConfigScreen.this.selectedEntry != null) {
                if (ArmorConfigScreen.this.selectedEntry.usesSlider()) {
                    this.updateSliderValue(mouseX);
                } else if (ArmorConfigScreen.this.selectedEntry.isDropdown()) {
                    this.dropdownOpen = !this.dropdownOpen;
                } else if (ArmorConfigScreen.this.selectedEntry.action() == ArmorConfigAction.CONFIGURE_JUNK_FILTER) {
                    // Junk filter screen not implemented
                } else {
                    ArmorConfigScreen.this.send(ArmorConfigScreen.this.selectedEntry.action());
                }
            }
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.dropdownOpen && ArmorConfigScreen.this.selectedEntry != null && ArmorConfigScreen.this.selectedEntry.isDropdown()) {
                String[] options = ArmorConfigScreen.dropdownOptions(ArmorConfigScreen.this.stack, ArmorConfigScreen.this.selectedEntry.action());
                int option = this.getDropdownOptionAt(mouseX, mouseY, options.length);
                if (option != -1) {
                    double progress = options.length <= 1 ? 0.0 : (double)(options.length - 1 - option) / (double)(options.length - 1);
                    ArmorConfigScreen.this.send(ArmorConfigScreen.this.selectedEntry.action(), progress);
                    this.dropdownOpen = false;
                    return true;
                }
                if (mouseY < (double)this.getY()) {
                    this.dropdownOpen = false;
                    return true;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        private int getDropdownOptionAt(double mouseX, double mouseY, int optionsCount) {
            int optionHeight = 20;
            int listTop = this.getY() - optionsCount * optionHeight;
            if (mouseX >= (double)this.getX() && mouseX <= (double)(this.getX() + this.width) && mouseY >= (double)listTop && mouseY < (double)this.getY()) {
                return (int)((mouseY - (double)listTop) / (double)optionHeight);
            }
            return -1;
        }

        protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
            if (ArmorConfigScreen.this.selectedEntry != null && ArmorConfigScreen.this.selectedEntry.usesSlider()) {
                this.updateSliderValue(mouseX);
            }
        }

        private void updateSliderValue(double mouseX) {
            int steps;
            int min = ArmorConfigScreen.getMinValue(ArmorConfigScreen.this.selectedEntry.action());
            int max = ArmorConfigScreen.getMaxValue(ArmorConfigScreen.this.stack, ArmorConfigScreen.this.selectedEntry.action());
            if (max <= min) {
                return;
            }
            int barLeft = this.getX() + 3;
            int barRight = this.getX() + this.width - 3;
            double progress = (mouseX - (double)barLeft) / (double)(barRight - barLeft);
            progress = Math.max(0.0, Math.min(1.0, progress));
            if (ArmorConfigScreen.this.selectedEntry.isSteppedSlider() && (steps = ArmorConfigScreen.steppedValueCount(ArmorConfigScreen.this.stack, ArmorConfigScreen.this.selectedEntry.action())) > 1) {
                progress = (double)Math.round(progress * (double)(steps - 1)) / (double)(steps - 1);
            }
            ArmorConfigScreen.this.send(ArmorConfigScreen.this.selectedEntry.action(), progress);
        }

        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (ArmorConfigScreen.this.selectedEntry != this.lastSelected) {
                this.dropdownOpen = false;
                this.lastSelected = ArmorConfigScreen.this.selectedEntry;
            }
            if (ArmorConfigScreen.this.selectedEntry == null) {
                return;
            }
            guiGraphics.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, -16777216);
            ArmorConfigScreen.drawBorder(guiGraphics, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, this.isHoveredOrFocused() ? -16711936 : -65536);
            if (ArmorConfigScreen.this.selectedEntry.usesSlider()) {
                int barLeft = this.getX() + 3;
                int barRight = this.getX() + this.width - 3;
                int barY = this.getY() + this.height / 2 - 2;
                guiGraphics.fill(barLeft, barY, barRight, barY + 4, -13619152);
                if (ArmorConfigScreen.this.selectedEntry.isSteppedSlider()) {
                    this.drawSliderSteps(guiGraphics, barLeft, barRight);
                }
                float progress = ArmorConfigScreen.this.selectedEntry.progress(ArmorConfigScreen.this.stack);
                int knob = barLeft + Math.round((float)(barRight - barLeft - 6) * progress);
                guiGraphics.fill(knob, this.getY() + 1, knob + 6, this.getY() + this.height - 1, -65536);
            }
            Object text = ArmorConfigScreen.this.selectedEntry.valueText(ArmorConfigScreen.this.stack);
            if (ArmorConfigScreen.this.selectedEntry.isDropdown()) {
                text = (String)text + " \u25bc";
            }
            guiGraphics.drawCenteredString(ArmorConfigScreen.this.font, (String)text, this.getX() + this.width / 2, this.getY() + 5, 0xFFFFFF);
            if (this.dropdownOpen && ArmorConfigScreen.this.selectedEntry.isDropdown()) {
                this.renderDropdown(guiGraphics, mouseX, mouseY);
            }
        }

        private void drawSliderSteps(GuiGraphics guiGraphics, int barLeft, int barRight) {
            int steps = ArmorConfigScreen.steppedValueCount(ArmorConfigScreen.this.stack, ArmorConfigScreen.this.selectedEntry.action());
            if (steps <= 1) {
                return;
            }
            for (int i = 0; i < steps; ++i) {
                int x = barLeft + Math.round((float)(barRight - barLeft) * ((float)i / (float)(steps - 1)));
                guiGraphics.fill(x - 1, this.getY() + 3, x + 1, this.getY() + this.height - 3, -10461088);
            }
        }

        private void renderDropdown(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            String[] options = ArmorConfigScreen.dropdownOptions(ArmorConfigScreen.this.stack, ArmorConfigScreen.this.selectedEntry.action());
            int optionHeight = 20;
            int listTop = this.getY() - options.length * optionHeight;
            guiGraphics.fill(this.getX(), listTop, this.getX() + this.width, this.getY(), -805306368);
            ArmorConfigScreen.drawBorder(guiGraphics, this.getX(), listTop, this.getX() + this.width, this.getY(), -16711936);
            int hovered = this.getDropdownOptionAt(mouseX, mouseY, options.length);
            for (int i = 0; i < options.length; ++i) {
                int y = listTop + i * optionHeight;
                if (i == hovered) {
                    guiGraphics.fill(this.getX() + 2, y, this.getX() + this.width - 2, y + optionHeight, 0x40FFFFFF);
                }
                guiGraphics.drawCenteredString(ArmorConfigScreen.this.font, options[i], this.getX() + this.width / 2, y + 6, 0xFFFFFF);
            }
        }

        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }
}
