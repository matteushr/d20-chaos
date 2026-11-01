package com.d20chaos.api;

public enum ActionCategory {
    MINING("Mineração"),
    MOVEMENT("Locomoção"),
    BLOCK_PLACE("Construção"),
    COMBAT_EXPLORE("Combate"),
    CRAFTING("Fabricação");

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
            case MOVEMENT -> "[LOC]";
            case BLOCK_PLACE -> "[CON]";
            case COMBAT_EXPLORE -> "[COM]";
            case CRAFTING -> "[FAB]";
        };
    }
}
