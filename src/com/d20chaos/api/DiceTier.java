package com.d20chaos.api;

public enum DiceTier {
    TIER_1_CURSE(1, "CALAMIDADE (MALDIÇÃO)", 0xFFFF3333, "§c[Crítico]"),
    TIER_2_DEBUFF(2, "SABOTAGEM & AZAR", 0xFFFFAA00, "§6[Debuff]"),
    TIER_3_NEUTRAL(3, "EQUILÍBRIO VANILLA", 0xFFDDDDDD, "§f[Neutro]"),
    TIER_4_BUFF(4, "VENTOS FAVORÁVEIS", 0xFF55FF55, "§a[Buff]"),
    TIER_5_DIVINE(5, "MODO DEUS (BÊNÇÃO)", 0xFFFFD700, "§e[Divino]");

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
                case MOVEMENT -> "§cAbismo";
                case MINING -> "§cDesabamento";
                case BLOCK_PLACE -> "§cTraidor";
                case COMBAT_EXPLORE -> "§cEspinhos";
                case CRAFTING -> "§cSucata";
            };
            case TIER_2_DEBUFF -> switch (category) {
                case MOVEMENT -> "§6Tropeço";
                case MINING -> "§6Gás Tóxico";
                case BLOCK_PLACE -> "§6Frágil";
                case COMBAT_EXPLORE -> "§6Guarda Baixa";
                case CRAFTING -> "§6Desgaste";
            };
            case TIER_3_NEUTRAL -> "§fVanilla";
            case TIER_4_BUFF -> switch (category) {
                case MOVEMENT -> "§aÍmpeto";
                case MINING -> "§aVeio Rico";
                case BLOCK_PLACE -> "§aArquiteto";
                case COMBAT_EXPLORE -> "§aCaçador";
                case CRAFTING -> "§aReforçado";
            };
            case TIER_5_DIVINE -> switch (category) {
                case MOVEMENT -> "§eHermes ★";
                case MINING -> "§eMidas ★";
                case BLOCK_PLACE -> "§eInfinita ★";
                case COMBAT_EXPLORE -> "§eThor ★";
                case CRAFTING -> "§eObra-Prima ★";
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
