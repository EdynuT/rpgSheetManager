package src.model;

public class Inventory extends PlayerCharacter {
    private int id;
    private int characterID;
    private int itemID;
    private int quantity;
    private boolean isEquipped;

    public Inventory() {
        super();
    }

    public Inventory(int id, int characterID, int itemID, int quantity, boolean isEquipped) {
        this.id = id;
        this.characterID = characterID;
        this.itemID = itemID;
        this.quantity = quantity;
        this.isEquipped = isEquipped;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCharacterID() {
        return characterID;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setEquipped(boolean isEquipped) {
        this.isEquipped = isEquipped;
    }
}