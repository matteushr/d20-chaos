package com.d20chaos.api;

public enum ActionCategory {
    MINING("Mining"),
    MOVEMENT("Movement"),
    BLOCK_PLACE("Building"),
    COMBAT_EXPLORE("Combat"),
    CRAFTING("Crafting");

    private final String displayName;

    ActionCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return switch (this) {
            case MINING -> "[MIN]";
            case MOVEMENT -> "[MOV]";
            case BLOCK_PLACE -> "[BLD]";
            case COMBAT_EXPLORE -> "[CBT]";
            case CRAFTING -> "[CRF]";
        };
    }
}
