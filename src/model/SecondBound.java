package src.model;

public class SecondBound extends PlayerCharacter {
    private Integer boundID;
    private Integer characterID;
    private String boundName;
    private String type;
    private String description;
    private boolean isActive = false;

    public SecondBound() {
        super();
    }

    public SecondBound(Integer id, Integer level, String name, Integer age, String race, String characterClass,
            String subclass, String origin, String language, int baseHealth, int baseMana, int baseStamina,
            int baseSanity, int currentHealth, int currentMana, int currentStamina, int currentSanity, Integer boundID,
            Integer characterID, String boundName, String type, String description, boolean isActive) {
        super(id, level, name, age, race, characterClass, subclass, origin, language, baseHealth, baseMana, baseStamina,
                baseSanity, currentHealth, currentMana, currentStamina, currentSanity);
        this.boundID = boundID;
        this.characterID = characterID;
        if (boundName != null && !boundName.trim().isEmpty()) {
            this.boundName = boundName;
        } else {
            throw new IllegalArgumentException("Bound name must not be null!");
        }
        this.type = type;
        this.description = description;
        this.isActive = isActive;
    }

    public Integer getBoundID() {
        return boundID;
    }

    public void setBoundID(Integer boundID) {
        this.boundID = boundID;
    }

    public Integer getCharacterID() {
        return characterID;
    }

    public void setCharacterID(Integer characterID) {
        this.characterID = characterID;
    }

    public String getBoundName() {
        return boundName;
    }

    public void setBoundName(String boundName) {
        this.boundName = boundName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}