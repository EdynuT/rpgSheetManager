package src.model;

public class Weapon extends Item {
    private Integer id;
    private String scalingAttribute;
    private String damageDice;
    private int flatDamage;
    private int criticalRange;
    private int criticalMultiplier;

    public Weapon() {
        super();
    }

    public Weapon(Integer id, Integer itemID, String name, String description, double weight, ItemCategory category,
                  String scalingAttribute, String damageDice, int flatDamage, int criticalRange, int criticalMultiplier) {
        super(itemID, name, description, weight, category);
        this.id = id;
        this.scalingAttribute = scalingAttribute;
        this.damageDice = damageDice;
        this.flatDamage = flatDamage;
        this.criticalRange = criticalRange;
        this.criticalMultiplier = criticalMultiplier;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getFlatDamage() {
        return flatDamage;
    }

    public void setFlatDamage(int flatDamage) {
        this.flatDamage = flatDamage;
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
                ", flatDamage=" + flatDamage +
                ", criticalRange=" + criticalRange +
                ", criticalMultiplier=" + criticalMultiplier +
                '}';
    }
}
