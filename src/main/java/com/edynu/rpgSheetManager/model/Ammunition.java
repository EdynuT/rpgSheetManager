package com.edynu.rpgSheetManager.model;

public class Ammunition extends Item {
    private String damageDice;

    public Ammunition() {
        super();
    }

    public Ammunition(Integer id, String name, String description, double weight, String damageDice) {
        super(id, name, description, weight, ItemCategory.AMMUNITION);
        this.damageDice = damageDice;
    }

    public String getDamageDice() {
        return damageDice;
    }

    public void setDamageDice(String damageDice) {
        this.damageDice = damageDice;
    }
}
