/*
 * Decompiled with CFR 0.152.
 */
package com.draconicarmorrewrite.config;

public enum OASArmorConfigKey {
    ARMOR_SPEED_MODIFIER("armor_speed_modifier"),
    ARMOR_SPEED_FOV_WARP("armor_speed_fov_warp"),
    ARMOR_JUMP_MODIFIER("armor_jump_modifier"),
    SPRINT_BOOST("sprint_boost"),
    ARMOR_HILL_STEP("armor_hill_step"),
    ARMOR_NIGHT_VISION("armor_night_vision"),
    ARMOR_NIGHT_VISION_LOCK("armor_night_vision_lock"),
    ARMOR_AUTO_FEED("armor_auto_feed"),
    ARMOR_FLIGHT_SPEED("armor_flight_speed"),
    ARMOR_VERTICAL_FLIGHT_SPEED("armor_vertical_flight_speed"),
    ARMOR_INERTIA_CANCEL("armor_inertia_cancel"),
    ARMOR_FLIGHT_LOCK("armor_flight_lock"),
    ARMOR_SHIELD_ENABLED("armor_shield_enabled"),
    HIDE_ARMOR("hide_armor"),
    DIG_SPEED("digSpeed"),
    AOE_SAFE_MODE("aoeSafeMode"),
    AOE_HEIGHT_NORMALIZATION("aoeHeightNormalization"),
    SHOW_DIG_AOE("showDigAOE"),
    ENABLE_JUNK_FILTER("enableJunkFilter"),
    JUNK_NBT_SENS("junkNbtSens"),
    SHOW_HARVEST_INDICATOR("showHarvestIndicator"),
    HOE_LAND_FILL("landFill"),
    DIG_AOE("dig_aoe"),
    DIG_DEPTH("dig_depth"),
    ATTACK_AOE("attack_aoe"),
    ATTACK_DAMAGE_BOOST("attack_damage_boost"),
    AUTO_FIRE("auto_fire"),
    BOW_ARROW_DAMAGE("bowArrowDamage"),
    BOW_ARROW_SPEED_MODIFIER("bowArrowSpeedModifier"),
    BOW_EXPLOSION_POWER("bowExplosionPower"),
    BOW_ZOOM_MODIFIER("bowZoomModifier"),
    BOW_FIRE_ARROW("bowFireArrow"),
    BOW_SHOCK_POWER("bowShockPower");

    private final String key;

    private OASArmorConfigKey(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }
}
