package model;

public class SecondBound {
    private Integer id;
    private Integer characterId;
    private String boundName;
    private Integer value = 0;

    public SecondBound() {
    }

    public SecondBound(Integer id, Integer characterId, String boundName, Integer value) {
        this.id = id;
        this.characterId = characterId;
        if (boundName != null && !boundName.trim().isEmpty()) {
            this.boundName = boundName;
        } else {
            throw new IllegalArgumentException("Bound name must not be null!");
        }
        this.value = (value != null) ? value : 0;
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

    public String getBoundName() {
        return boundName;
    }

    public void setBoundName(String boundName) {
        this.boundName = boundName;
    }


    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = (value != null) ? value : 0;
    }
}
