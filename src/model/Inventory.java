package src.model;

public class Inventory extends PlayerCharacter {
    private Integer id;
    private Integer characterID;
    private Integer itemID;
    private Integer quantity;
    private boolean isEquipped;

    public Inventory() {
        super();
    }

    public Inventory(Integer id, Integer characterID, Integer itemID, Integer quantity, boolean isEquipped) {
        this.id = id;
        this.characterID = characterID;
        this.itemID = itemID;
        this.quantity = quantity;
        this.isEquipped = isEquipped;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCharacterID() {
        return characterID;
    }

    public void setCharacterID(Integer characterID) {
        this.characterID = characterID;
    }

    public Integer getItemID() {
        return itemID;
    }

    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }
}