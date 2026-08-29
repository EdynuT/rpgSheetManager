package src.model;

public class Armor extends Item {
    private Integer armorID;
    private Integer physicalAC;
    private Integer elementalAC;

    public Armor() {
        super();
    }

    public Armor(Integer armorID, Integer itemID, String name, String description, Double weight, Integer physicalAC, Integer elementalAC) {
        super(itemID, name, description, weight, ItemCategory.ARMOR);
        this.armorID = armorID;
        this.physicalAC = physicalAC;
        this.elementalAC = elementalAC;
    }

    public Integer getArmotId() {
        return armorID;
    }

    public void setArmorId(Integer armorID) {
        this.armorID = armorID;
    }

    public Integer getPhysicalAC() {
        return physicalAC;
    }

    public void setPhysicalAC(Integer physicalAC) {
        this.physicalAC = physicalAC;
    }

    public Integer getElementalAC() {
        return elementalAC;
    }

    public void setElementalAC(Integer elementalAC) {
        this.elementalAC = elementalAC;
    }
}
