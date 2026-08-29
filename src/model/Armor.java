package src.model;

public class Armor extends Item {
    private Integer id;
    private Integer physicalAC;
    private Integer elementalAC;

    public Armor() {
        super();
    }

    public Armor(Integer id, Integer itemID, String name, String description, Integer physicalAC, Integer elementalAC) {
        super(itemID, name, description, null, null);
        this.id = id;
        this.physicalAC = physicalAC;
        this.elementalAC = elementalAC;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
