package com.edynu.rpgSheetManager.model;

public enum ItemCategory {
    WEAPON,
    ARMOR,
    AMMUNITION,
    GENERAL;

    @Override
    public String toString() {
        switch (this) {
            case WEAPON:
                return "Arma";
            case ARMOR:
                return "Armadura";
            case AMMUNITION:
                return "Munição";
            case GENERAL:
                return "Geral";
            default:
                return name();
        }
    }
}
