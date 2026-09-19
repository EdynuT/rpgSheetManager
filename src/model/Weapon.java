package model;

public class Weapon extends Item {
    private String scalingAttribute;
    private String damageDice;
    private int criticalRange;
    private int criticalMultiplier;

    public Weapon() {
        super();
    }

    public Weapon(Integer id, String name, String description, double weight, ItemCategory category,
                  String scalingAttribute, String damageDice, int criticalRange, int criticalMultiplier) {
        super(id, name, description, weight, category);
        this.scalingAttribute = scalingAttribute;
        this.damageDice = damageDice;
        this.criticalRange = criticalRange;
        this.criticalMultiplier = criticalMultiplier;
    }

    public String getScalingAttribute() {
        return scalingAttribute;
    }

    public void setScalingAttribute(String scalingAttribute) {
        this.scalingAttribute = scalingAttribute;
    }

    public String getDamageDice() {
        return damageDice;
    }

    public void setDamageDice(String damageDice) {
        this.damageDice = damageDice;
    }

    public int getCriticalRange() {
        return criticalRange;
    }

    public void setCriticalRange(int criticalRange) {
        this.criticalRange = criticalRange;
    }

    public int getCriticalMultiplier() {
        return criticalMultiplier;
    }

    public void setCriticalMultiplier(int criticalMultiplier) {
        this.criticalMultiplier = criticalMultiplier;
    }
    
    @Override
    public String toString() {
        return "Weapon{" +
                "scalingAttribute='" + scalingAttribute + '\'' +
                ", damageDice='" + damageDice + '\'' +
                ", criticalRange=" + criticalRange +
                ", criticalMultiplier=" + criticalMultiplier +
                '}';
    }
}
