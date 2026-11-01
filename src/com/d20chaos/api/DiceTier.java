package com.d20chaos.api;

public enum DiceTier {
    TIER_1_CURSE(1, "MORTAL CALAMITY", 0xFFFF3333, "§c[Critical]"),
    TIER_2_DEBUFF(2, "SABOTAGE & HAZARDS", 0xFFFFAA00, "§6[Debuff]"),
    TIER_3_NEUTRAL(3, "VANILLA EQUILIBRIUM", 0xFFDDDDDD, "§f[Neutral]"),
    TIER_4_BUFF(4, "FAVORABLE WINDS", 0xFF55FF55, "§a[Buff]"),
    TIER_5_DIVINE(5, "GOD MODE (DIVINE)", 0xFFFFD700, "§e[Divine]");

    private final int level;
    private final String title;
    private final int colorRgb;
    private final String statusTag;

    DiceTier(int level, String title, int colorRgb, String statusTag) {
        this.level = level;
        this.title = title;
        this.colorRgb = colorRgb;
        this.statusTag = statusTag;
    }

    public int getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public int getColorRgb() {
        return colorRgb;
    }

    public String getStatusTag() {
        return statusTag;
    }

    public String getArchetype(ActionCategory category) {
        return switch (this) {
            case TIER_1_CURSE -> switch (category) {
                case MOVEMENT -> "§cAnvil";
                case MINING -> "§cNest";
                case BLOCK_PLACE -> "§cGravity";
                case COMBAT_EXPLORE -> "§cThorns";
                case CRAFTING -> "§cScrap";
            };
            case TIER_2_DEBUFF -> switch (category) {
                case MOVEMENT -> "§6Stumble";
                case MINING -> "§6Toxic Gas";
                case BLOCK_PLACE -> "§6Brittle";
                case COMBAT_EXPLORE -> "§6Low Guard";
                case CRAFTING -> "§6Wear";
            };
            case TIER_3_NEUTRAL -> "§fVanilla";
            case TIER_4_BUFF -> switch (category) {
                case MOVEMENT -> "§aMomentum";
                case MINING -> "§aRich Vein";
                case BLOCK_PLACE -> "§aArchitect";
                case COMBAT_EXPLORE -> "§aHunter";
                case CRAFTING -> "§aReinforced";
            };
            case TIER_5_DIVINE -> switch (category) {
                case MOVEMENT -> "§eHermes ★";
                case MINING -> "§eMidas ★";
                case BLOCK_PLACE -> "§eInfinite ★";
                case COMBAT_EXPLORE -> "§eThor ★";
                case CRAFTING -> "§eMasterpiece ★";
            };
        };
    }

    public static DiceTier fromDecision(int roll) {
        if (roll <= 3) return TIER_1_CURSE;
        if (roll <= 9) return TIER_2_DEBUFF;
        if (roll <= 11) return TIER_3_NEUTRAL;
        if (roll <= 17) return TIER_4_BUFF;
        return TIER_5_DIVINE;
    }
}
