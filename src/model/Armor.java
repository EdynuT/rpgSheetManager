package model;

public class Armor extends Item {
    private Integer physicalAC;
    private Integer elementalAC;

    public Armor() {
        super();
    }

    public Armor(Integer id, String name, String description, Double weight, Integer physicalAC, Integer elementalAC) {
        super(id, name, description, weight, ItemCategory.ARMOR);
        this.physicalAC = physicalAC;
        this.elementalAC = elementalAC;
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
