package model;

public class Inventory {
    private Integer id;
    private Integer characterId;
    private Integer itemId;
    private Integer quantity;
    private boolean isEquipped;

    public Inventory() {
        super();
    }

    public Inventory(Integer id, Integer characterId, Integer itemId, Integer quantity, boolean isEquipped) {
        this.id = id;
        this.characterId = characterId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.isEquipped = isEquipped;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Integer characterId) {
        this.characterId = characterId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
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