package com.edynu.rpgSheetManager.model;

public enum AbilityCategory {
    ACTIVE,
    PASSIVE,
    MAGIC;

    @Override
    public String toString() {
        switch (this) {
            case ACTIVE:
                return "Ativa";
            case PASSIVE:
                return "Passiva";
            case MAGIC:
                return "Mágica";
            default:
                return name();
        }
    }
}
