package src.model;

public class Ammunition extends Item {
    private Integer ammoID;
    private String damageDice;

    public Integer getAmmoID() {
        return ammoID;
    }

    public void setAmmoID(Integer ammoID) {
        this.ammoID = ammoID;
    }

    public String getDamageDice() {
        return damageDice;
    }

    public void setDamageDice(String damageDice) {
        this.damageDice = damageDice;
    }
}
