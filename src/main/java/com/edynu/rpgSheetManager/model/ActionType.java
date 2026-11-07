package com.edynu.rpgSheetManager.model;

public enum ActionType {
    STANDARD,
    MOVE,
    BONUS,
    FREE,
    COMPLETE,
    REACTION;

    @Override
    public String toString() {
        switch (this) {
            case STANDARD:
                return "Ação padrão";
            case MOVE:
                return "Ação de movimento";
            case BONUS:
                return "Ação bônus";
            case FREE:
                return "Ação livre";
            case COMPLETE:
                return "Ação completa";
            case REACTION:
                return "Reação";
            default:
                return name();
        }
    }
}
