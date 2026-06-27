package com.draconicarmorrewrite.upgrade;

import java.util.Arrays;

public enum OASUpgrade {
    RF_CAP("rfCap", "rf_cap", 4),
    DIG_SPEED("digSpeed", "dig_speed", 4),
    DIG_AOE("digAOE", "dig_aoe", 4),
    ATTACK_DAMAGE("attackDmg", "attack_damage", 4),
    ATTACK_AOE("attackAOE", "attack_aoe", 4),
    ARROW_DAMAGE("arrowDmg", "arrow_damage", 4),
    DRAW_SPEED("drawSpeed", "draw_speed", 3),
    ARROW_SPEED("arrowSpeed", "arrow_speed", 4),
    SHIELD_CAP("shieldCap", "shield_cap", 4),
    SHIELD_REC("shieldRec", "shield_rec", 4),
    MOVE_SPEED("moveSpeed", "move_speed", 4),
    JUMP_BOOST("jumpBoost", "jump_boost", 4);

    private final String legacyId;
    private final String registryPath;
    private final int maxRecipeLevel;

    private OASUpgrade(String legacyId, String registryPath, int maxRecipeLevel) {
        this.legacyId = legacyId;
        this.registryPath = registryPath;
        this.maxRecipeLevel = maxRecipeLevel;
    }

    public String id() {
        return this.legacyId;
    }

    public String registryPath() {
        return this.registryPath;
    }

    public int maxRecipeLevel() {
        return this.maxRecipeLevel;
    }

    public String translationKey() {
        return "upgrade.draconicarmorrewrite." + this.registryPath;
    }

    public static OASUpgrade byId(String id) {
        return Arrays.stream(OASUpgrade.values())
                .filter(value -> value.legacyId.equals(id) || value.registryPath.equals(id) || value.name().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown upgrade id: " + id));
    }
}
