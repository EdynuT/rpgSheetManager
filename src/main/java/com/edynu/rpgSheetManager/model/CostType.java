package com.edynu.rpgSheetManager.model;

public enum CostType {
    HEALTH,
    MANA,
    STAMINA,
    SANITY;

    @Override
    public String toString() {
        switch (this) {
            case HEALTH:
                return "Vida";
            case MANA:
                return "Mana";
            case STAMINA:
                return "Vigor";
            case SANITY:
                return "Sanidade";
            default:
                return name();
        }
    }
}
